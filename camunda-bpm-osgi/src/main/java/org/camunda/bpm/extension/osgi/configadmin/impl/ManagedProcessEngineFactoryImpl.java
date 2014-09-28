package org.camunda.bpm.extension.osgi.configadmin.impl;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.MethodUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.blueprint.BundleDelegatingClassLoader;
import org.camunda.bpm.extension.osgi.blueprint.ClassLoaderWrapper;
import org.camunda.bpm.extension.osgi.configadmin.ManagedProcessEngineFactory;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

@SuppressWarnings("rawtypes")
public class ManagedProcessEngineFactoryImpl implements ManagedProcessEngineFactory {

  private Map<String, ProcessEngine> existingEngines = new ConcurrentHashMap<String, ProcessEngine>();
  private Map<String, ServiceRegistration> existingRegisteredEngines = new ConcurrentHashMap<String, ServiceRegistration>();
  private BundleContext ctx;

  public ManagedProcessEngineFactoryImpl(BundleContext bundleContext) {
    this.ctx = bundleContext;
  }

  @Override
  public String getName() {
    return SERVICE_PID;
  }

  @Override
  public void updated(String pid, Dictionary properties) throws ConfigurationException {
    if (existingEngines.containsKey(pid)) {
      existingEngines.get(pid).close();
      existingRegisteredEngines.get(pid).unregister();
    }
    ClassLoader previous = Thread.currentThread().getContextClassLoader();
    Bundle bundle = FrameworkUtil.getBundle(ProcessEngine.class);
    ProcessEngine engine;
    try {
      ClassLoader cl = new BundleDelegatingClassLoader(bundle);
      Thread.currentThread().setContextClassLoader(
          new ClassLoaderWrapper(cl, ProcessEngineFactory.class.getClassLoader(), ProcessEngineConfiguration.class.getClassLoader(), previous));
      ProcessEngineConfiguration processEngineConfiguration = createProcessEngineConfiguration(properties);
      processEngineConfiguration.setClassLoader(cl);
      engine = processEngineConfiguration.buildProcessEngine();
    } finally {
      Thread.currentThread().setContextClassLoader(previous);
    }
    existingEngines.put(pid, engine);
    Hashtable<String, Object> props = new Hashtable<String, Object>();
    props.put("process-engine-name", engine.getName());
    ServiceRegistration serviceRegistration = ctx.registerService(ProcessEngine.class.getName(), engine, props);
    existingRegisteredEngines.put(pid, serviceRegistration);
  }

  private ProcessEngineConfiguration createProcessEngineConfiguration(Dictionary properties) throws ConfigurationException {
    ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration();
    for (Object key : Collections.list(properties.keys())) {
      char[] keyStringArray = String.valueOf(key).toCharArray();
      keyStringArray[0] = Character.toUpperCase(keyStringArray[0]);
      String keyString = String.valueOf(keyStringArray);
      if (String.valueOf(key).equals("service.pid") || String.valueOf(key).equals("service.factoryPid")) {
        continue;
      }
      try {
        // because there are non-void setters we cannot use PropertyUtils
        MethodUtils.invokeMethod(processEngineConfiguration, "set" + keyString, properties.get(key));
      } catch (Exception e) {
        throw new ConfigurationException(String.valueOf(key), "Property does not exist", e);
      }
    }
    return processEngineConfiguration;
  }

  @Override
  public void deleted(String pid) {
    ProcessEngine engine = existingEngines.get(pid);
    engine.close();
    existingEngines.remove(pid);
    existingRegisteredEngines.get(pid).unregister();
  }

}
