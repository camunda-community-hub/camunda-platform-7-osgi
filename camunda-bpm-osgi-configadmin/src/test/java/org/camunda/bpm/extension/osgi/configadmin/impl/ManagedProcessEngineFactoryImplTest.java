package org.camunda.bpm.extension.osgi.configadmin.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.extension.osgi.configadmin.ManagedProcessEngineFactory;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

public class ManagedProcessEngineFactoryImplTest {

  @Test
  public void getName() {
    ManagedProcessEngineFactoryImpl factory = new ManagedProcessEngineFactoryImpl();
    assertThat(factory.getName(), is(ManagedProcessEngineFactory.SERVICE_PID));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void updateEmptyConfiguration() throws ConfigurationException {
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    ManagedProcessEngineFactoryImpl factory = new ManagedProcessEngineFactoryImpl(bundle);

    factory.updated("id", new Hashtable<String, String>());

    verify(bundleContext, never()).registerService(eq(ProcessEngine.class), any(ProcessEngine.class), any(Dictionary.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void updateConfigurationWithOnlyPidAndFactoryPid() throws ConfigurationException {
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    ManagedProcessEngineFactoryImpl factory = new ManagedProcessEngineFactoryImpl(bundle);
    Hashtable<String, String> properties = new Hashtable<String, String>();
    properties.put(Constants.SERVICE_PID, "id");
    properties.put("service.factoryPid", "factory id");

    factory.updated("id", properties);

    verify(bundleContext, never()).registerService(eq(ProcessEngine.class), any(ProcessEngine.class), any(Dictionary.class));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void updatedNewConfiguration() throws Exception {
    String engineName = "TestEngine";
    Bundle bundle = mock(Bundle.class);
    // mock stuff the BundleDelegatingClassLoader does/needs
    mockGetResource(bundle);
    mockLoadClass(bundle);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    when(bundleContext.registerService(eq(ProcessEngine.class), any(ProcessEngine.class), any(Dictionary.class))).thenReturn(mock(ServiceRegistration.class));
    ManagedProcessEngineFactoryImpl factory = new ManagedProcessEngineFactoryImpl(bundle);
    Hashtable<String, String> properties = createEngineProperties(engineName);

    factory.updated("id", properties);

    ArgumentCaptor<Dictionary> propsCapture = ArgumentCaptor.forClass(Dictionary.class);
    ArgumentCaptor<ProcessEngine> engineCapture = ArgumentCaptor.forClass(ProcessEngine.class);

    verify(bundleContext, times(1)).registerService(eq(ProcessEngine.class), engineCapture.capture(), propsCapture.capture());
    assertThat(engineCapture.getValue(), is(notNullValue()));
    assertThat(propsCapture.getValue().get("process-engine-name"), is((Object) engineName));
    assertThat(ProcessEngines.getProcessEngine(engineName), is(notNullValue()));
  }

  private Hashtable<String, String> createEngineProperties(String engineName) {
    Hashtable<String, String> properties = new Hashtable<String, String>();
    properties.put("processEngineName", engineName);
    properties.put("jdbcUrl", "jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1");
    properties.put("databaseSchemaUpdate", "true");
    return properties;
  }
  
  /**
   * Although the call from the framework to delete a Pid that doesn't exist
   * seems unrealistic the factory shouldn't collaps when it happens anyway.
   */
  @Test
  public void deleteForNonExistingId(){
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    ManagedProcessEngineFactoryImpl factory = new ManagedProcessEngineFactoryImpl(bundle);
    
    factory.deleted("notRegisteredId");
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void deleteEngine() throws Throwable{
    String engineName = "TestEngine";
    Bundle bundle = mock(Bundle.class);
    // mock stuff the BundleDelegatingClassLoader does/needs
    mockGetResource(bundle);
    mockLoadClass(bundle);

    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    ServiceRegistration registrationMock = mock(ServiceRegistration.class);
    when(bundleContext.registerService(eq(ProcessEngine.class), any(ProcessEngine.class), any(Dictionary.class))).thenReturn(registrationMock);
    ManagedProcessEngineFactoryImpl factory = new ManagedProcessEngineFactoryImpl(bundle);
    Hashtable<String, String> properties = createEngineProperties(engineName);
    factory.updated("id", properties);
    
    factory.deleted("id");
    
    verify(registrationMock, times(1)).unregister();
    assertThat(ProcessEngines.getProcessEngine(engineName), is(nullValue()));
  }

  @SuppressWarnings("rawtypes")
  private void mockLoadClass(Bundle bundle) throws ClassNotFoundException {
    final ArgumentCaptor<String> classCaptor = ArgumentCaptor.forClass(String.class);
    when(bundle.loadClass(classCaptor.capture())).thenAnswer(new Answer<Class>() {
      @Override
      public Class answer(InvocationOnMock invocation) throws Throwable {
        return this.getClass().getClassLoader().loadClass(classCaptor.getValue());
      }
    });
  }

  private void mockGetResource(Bundle bundle) {
    final ArgumentCaptor<String> resourceCaptor = ArgumentCaptor.forClass(String.class);
    when(bundle.getResource(resourceCaptor.capture())).thenAnswer(new Answer<URL>() {
      @Override
      public URL answer(InvocationOnMock invocation) throws Throwable {
        return this.getClass().getResource(resourceCaptor.getValue());
      }
    });
  }

}
