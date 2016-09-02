package org.camunda.bpm.extension.osgi.scripting.impl;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;

import javax.script.ScriptEngine;

import org.camunda.bpm.extension.osgi.TestScriptEngineFactory;
import org.camunda.bpm.extension.osgi.scripting.ScriptEngineResolver;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class BundleScriptEngineResolverTest {

  @SuppressWarnings("unchecked")
  @Test
  public void register() {
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    BundleScriptEngineResolver scriptEngineResolver = new BundleScriptEngineResolver(bundle, null);
    scriptEngineResolver.register();
    verify(bundleContext).registerService(eq(ScriptEngineResolver.class), eq(scriptEngineResolver), isNull(Dictionary.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void unregister() {
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    BundleScriptEngineResolver scriptEngineResolver = new BundleScriptEngineResolver(bundle, null);
    ServiceRegistration<ScriptEngineResolver> serviceReg = mock(ServiceRegistration.class);
    when(bundleContext.registerService(eq(ScriptEngineResolver.class), eq(scriptEngineResolver), isNull(Dictionary.class))).thenReturn(serviceReg);
    scriptEngineResolver.register();
    scriptEngineResolver.unregister();
    verify(serviceReg).unregister();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void resolveTestScriptEngine() throws MalformedURLException, ClassNotFoundException {
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    Class factoryClazz = TestScriptEngineFactory.class;
    when(bundle.loadClass(eq(factoryClazz.getName()))).thenReturn(factoryClazz);
    URL configFile = new File("src/test/resources/javax.script.ScriptEngineFactory").toURI().toURL();
    BundleScriptEngineResolver scriptEngineResolver = new BundleScriptEngineResolver(bundle, configFile);
    ScriptEngine scriptEngine = scriptEngineResolver.resolveScriptEngine("Uber-language");
    assertThat(scriptEngine, is(instanceOf(TestScriptEngineFactory.TestScriptEngine.class)));
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void resolveTestScriptEngineWithCommentsInFile() throws MalformedURLException, ClassNotFoundException {
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    Class factoryClazz = TestScriptEngineFactory.class;
    when(bundle.loadClass(eq(factoryClazz.getName()))).thenReturn(factoryClazz);
    URL configFile = new File("src/test/resources/javax.script.ScriptEngineFactoryComment").toURI().toURL();
    BundleScriptEngineResolver scriptEngineResolver = new BundleScriptEngineResolver(bundle, configFile);
    ScriptEngine scriptEngine = scriptEngineResolver.resolveScriptEngine("Uber-language");
    assertThat(scriptEngine, is(instanceOf(TestScriptEngineFactory.TestScriptEngine.class)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void throwsExceptionWhenReceivingInvalidScriptEngine() throws Exception {
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    @SuppressWarnings("rawtypes")
    Class clazz = this.getClass();
    when(bundle.loadClass(anyString())).thenReturn(clazz);
    URL configFile = new File("src/test/resources/javax.script.ScriptEngineFactory").toURI().toURL();
    BundleScriptEngineResolver scriptEngineResolver = new BundleScriptEngineResolver(bundle, configFile);
    ScriptEngine scriptEngine = scriptEngineResolver.resolveScriptEngine("Uber-language");
    assertThat(scriptEngine, is(nullValue()));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void resolveScriptEngineWithWrongName() throws MalformedURLException, ClassNotFoundException {
    Bundle bundle = mock(Bundle.class);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    Class factoryClazz = TestScriptEngineFactory.class;
    when(bundle.loadClass(eq(factoryClazz.getName()))).thenReturn(factoryClazz);
    URL configFile = new File("src/test/resources/javax.script.ScriptEngineFactory").toURI().toURL();
    BundleScriptEngineResolver scriptEngineResolver = new BundleScriptEngineResolver(bundle, configFile);
    ScriptEngine scriptEngine = scriptEngineResolver.resolveScriptEngine("Java");
    assertThat(scriptEngine, is(nullValue()));
  }
}
