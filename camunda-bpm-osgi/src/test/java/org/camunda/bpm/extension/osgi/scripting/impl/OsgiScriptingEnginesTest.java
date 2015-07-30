package org.camunda.bpm.extension.osgi.scripting.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.impl.scripting.engine.ScriptBindingsFactory;
import org.camunda.bpm.extension.osgi.TestScriptEngineFactory;
import org.camunda.bpm.extension.osgi.scripting.ScriptEngineResolver;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OsgiScriptingEnginesTest {

  @Test
  public void bindingsFactoryConstructor() {
    ScriptBindingsFactory mock = mock(ScriptBindingsFactory.class);
    OsgiScriptingEngines osgiScriptingEngines = new OsgiScriptingEngines(mock);
    assertThat(osgiScriptingEngines.getScriptBindingsFactory(), is(mock));
  }

  @Test(expected = ProcessEngineException.class)
  public void evaluateWithNonExistingEngine() throws InvalidSyntaxException {
    BundleContext context = mock(BundleContext.class);
    when(context.getServiceReferences(anyString(), isNull(String.class))).thenReturn(null);
    ScriptBindingsFactory bindingsFactory = mock(ScriptBindingsFactory.class);
    VariableScope variableScope = mock(VariableScope.class);
    TestOsgiScriptingEngines scriptingEngines = new TestOsgiScriptingEngines(bindingsFactory);
    scriptingEngines.ctx = context;
    Object evaluate = scriptingEngines.evaluate("print String", "Python", variableScope);
    assertThat(evaluate, is(nullValue()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void evaluateWithExistingEngine() throws InvalidSyntaxException, ScriptException {
    BundleContext context = mock(BundleContext.class);
    ServiceReference<ScriptEngineResolver> serviceReference = mock(ServiceReference.class);
    ScriptEngineResolver scriptEngineResolver = mock(ScriptEngineResolver.class);
    when(context.getService(serviceReference)).thenReturn(scriptEngineResolver);
    when(context.getServiceReferences(eq(ScriptEngineResolver.class), isNull(String.class))).thenReturn(Collections.singletonList(serviceReference));
    ScriptEngine scriptEngine = mock(ScriptEngine.class);
    when(scriptEngineResolver.resolveScriptEngine("Python")).thenReturn(scriptEngine);
    ScriptBindingsFactory bindingsFactory = mock(ScriptBindingsFactory.class);
    when(scriptEngine.eval(anyString(), isNull(Bindings.class))).thenReturn("Success");
    when(scriptEngine.eval(anyString(), any(Bindings.class))).thenReturn("Success");
    TestOsgiScriptingEngines scriptingEngines = new TestOsgiScriptingEngines(bindingsFactory);
    scriptingEngines.ctx = context;
    Object evaluate = scriptingEngines.evaluate("print String", "Python", mock(VariableScope.class));
    assertThat(evaluate.toString(), is("Success"));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = ProcessEngineException.class)
  public void evaluateSkriptWithSyntaxError() throws InvalidSyntaxException, ScriptException {
    BundleContext context = mock(BundleContext.class);
    ServiceReference<ScriptEngineResolver> serviceReference = mock(ServiceReference.class);
    ScriptEngineResolver scriptEngineResolver = mock(ScriptEngineResolver.class);
    when(context.getService(serviceReference)).thenReturn(scriptEngineResolver);
    when(context.getServiceReferences(eq(ScriptEngineResolver.class), isNull(String.class))).thenReturn(Collections.singletonList(serviceReference));
    ScriptEngine scriptEngine = mock(ScriptEngine.class);
    when(scriptEngineResolver.resolveScriptEngine("Python")).thenReturn(scriptEngine);
    ScriptBindingsFactory bindingsFactory = mock(ScriptBindingsFactory.class);
    when(scriptEngine.eval(anyString(), isNull(Bindings.class))).thenThrow(new Class[] { ScriptException.class });
    TestOsgiScriptingEngines scriptingEngines = new TestOsgiScriptingEngines(bindingsFactory);
    scriptingEngines.ctx = context;
    scriptingEngines.evaluate("print String", "Python", mock(VariableScope.class));
  }

  @Test
  public void resolveScriptEngineWhenNoScriptEngineResolverIsPresent() throws InvalidSyntaxException {
    ScriptBindingsFactory mock = mock(ScriptBindingsFactory.class);
    TestOsgiScriptingEngines osgiScriptingEngines = new TestOsgiScriptingEngines(mock);
    BundleContext context = mock(BundleContext.class);
    osgiScriptingEngines.ctx = context;
    ScriptEngine scriptEngine = osgiScriptingEngines.resolveScriptEngine("foo");
    assertThat(scriptEngine, is(nullValue()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void resolveScriptEngine() throws InvalidSyntaxException {
    ScriptBindingsFactory mock = mock(ScriptBindingsFactory.class);
    TestOsgiScriptingEngines osgiScriptingEngines = new TestOsgiScriptingEngines(mock);
    BundleContext context = mock(BundleContext.class);
    ServiceReference<ScriptEngineResolver> serviceRef = mock(ServiceReference.class);
    when(context.getServiceReferences(eq(ScriptEngineResolver.class), isNull(String.class))).thenReturn(Collections.singleton(serviceRef));
    ScriptEngineResolver scriptEngineResolver = mock(ScriptEngineResolver.class);
    when(context.getService(eq(serviceRef))).thenReturn(scriptEngineResolver);
    ScriptEngine scriptEngine = new TestScriptEngineFactory.TestScriptEngine();
    when(scriptEngineResolver.resolveScriptEngine(eq("foo"))).thenReturn(scriptEngine);
    osgiScriptingEngines.ctx = context;
    ScriptEngine resolvedScriptEngine = osgiScriptingEngines.resolveScriptEngine("foo");
    assertThat(resolvedScriptEngine, is(scriptEngine));
  }

  private class TestOsgiScriptingEngines extends OsgiScriptingEngines {

    public TestOsgiScriptingEngines(ScriptBindingsFactory scriptBindingsFactory) {
      super(scriptBindingsFactory);
    }

    private BundleContext ctx;

    /**
     * Overridden for test purposes.
     */
    @Override
    protected BundleContext getBundleContext() {
      return ctx;
    }

  }
}
