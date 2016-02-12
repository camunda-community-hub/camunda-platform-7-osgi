package org.camunda.bpm.extension.osgi.configadmin.impl;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.classloading.BundleDelegatingClassLoader;
import org.camunda.bpm.extension.osgi.classloading.ClassLoaderWrapper;
import org.camunda.bpm.extension.osgi.configadmin.ManagedProcessEngineFactory;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

@SuppressWarnings("rawtypes")
public class ManagedProcessEngineFactoryImpl implements ManagedProcessEngineFactory {

  private Map<String, ProcessEngine> existingEngines = new ConcurrentHashMap<String, ProcessEngine>();
  private Map<String, ServiceRegistration<ProcessEngine>> existingRegisteredEngines = new ConcurrentHashMap<String, ServiceRegistration<ProcessEngine>>();
  private volatile Bundle bundle;

  /**
   * Default constructor for Apache Felix Dependency Manager.
   */
  public ManagedProcessEngineFactoryImpl() {
  }

  public ManagedProcessEngineFactoryImpl(Bundle bundle) {
    this.bundle = bundle;
  }

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
    ServiceRegistration<ProcessEngine> serviceRegistration = this.bundle.getBundleContext().registerService(ProcessEngine.class, engine, props);
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
    mapProperties.remove(Constants.SERVICE_PID);
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
    ProcessEngine engine = existingEngines.get(pid);
    if (engine != null) {
      engine.close();
      existingEngines.remove(pid);
      existingRegisteredEngines.get(pid).unregister();
      existingRegisteredEngines.remove(pid);
    }
  }

}
