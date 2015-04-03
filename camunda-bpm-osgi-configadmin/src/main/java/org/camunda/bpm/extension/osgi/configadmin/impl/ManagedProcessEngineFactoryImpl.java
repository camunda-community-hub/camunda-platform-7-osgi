package org.camunda.bpm.extension.osgi.configadmin.impl;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.blueprint.BundleDelegatingClassLoader;
import org.camunda.bpm.extension.osgi.blueprint.ClassLoaderWrapper;
import org.camunda.bpm.extension.osgi.configadmin.ManagedProcessEngineFactory;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

@SuppressWarnings("rawtypes")
public class ManagedProcessEngineFactoryImpl implements ManagedProcessEngineFactory {

  private Map<String, ProcessEngine> existingEngines = new ConcurrentHashMap<String, ProcessEngine>();
  private Map<String, ServiceRegistration> existingRegisteredEngines = new ConcurrentHashMap<String, ServiceRegistration>();
  private volatile Bundle bundle;

  @Override
  public String getName() {
    return SERVICE_PID;
  }

  @Override
  public void updated(String pid, Dictionary properties) throws ConfigurationException {
    if (existingEngines.containsKey(pid)) {
      existingEngines.get(pid).close();
      existingEngines.remove(pid);
      existingRegisteredEngines.get(pid).unregister();
      existingRegisteredEngines.remove(pid);
    }
    if (!hasPropertiesConfiguration(properties)) {
      return;
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
    ServiceRegistration serviceRegistration = this.bundle.getBundleContext().registerService(ProcessEngine.class.getName(), engine, props);
    existingRegisteredEngines.put(pid, serviceRegistration);
  }

  /**
   * It happends that the factory get called with properties that only contain
   * service.pid and service.factoryPid. If that happens we don't want to create
   * an engine.
   * 
   * @param properties
   * @return
   */
  @SuppressWarnings("unchecked")
  private boolean hasPropertiesConfiguration(Dictionary properties) {
    HashMap<Object, Object> mapProperties = new HashMap<Object, Object>(properties.size());
    for (Object key : Collections.list(properties.keys())) {
      mapProperties.put(key, properties.get(key));
    }
    mapProperties.remove("service.pid");
    mapProperties.remove("service.factoryPid");
    return !mapProperties.isEmpty();
  }

  @SuppressWarnings("unchecked")
  private ProcessEngineConfiguration createProcessEngineConfiguration(Dictionary properties) throws ConfigurationException {
    ProcessEngineConfigurationFromProperties processEngineConfiguration = new ProcessEngineConfigurationFromProperties();
    processEngineConfiguration.configure(properties);
    return processEngineConfiguration;
  }

  @Override
  public void deleted(String pid) {
    try {
      ProcessEngine engine = existingEngines.get(pid);
      engine.close();
      existingEngines.remove(pid);
      existingRegisteredEngines.get(pid).unregister();
      existingRegisteredEngines.remove(pid);
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception when trying to delete service with pid " + pid, e);
    }
  }

}
