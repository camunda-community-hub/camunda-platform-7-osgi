package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.impl.scripting.ScriptBindingsFactory;
import org.camunda.bpm.extension.osgi.Extender;
import org.camunda.bpm.extension.osgi.OsgiScriptingEngines;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OsgiScriptingEnginesTest {

	@Test
	public void bindingsFactoryConstructor() {
		ScriptBindingsFactory mock = mock(ScriptBindingsFactory.class);
		OsgiScriptingEngines osgiScriptingEngines = new OsgiScriptingEngines(
				mock);
		assertThat(osgiScriptingEngines.getScriptBindingsFactory(), is(mock));
	}

	@Test
	public void engineManagerConstructor() {
		ScriptEngineManager mock = mock(ScriptEngineManager.class);
		new OsgiScriptingEngines(mock);
	}

	@Test(expected = ProcessEngineException.class)
	public void evaluateWithNonExistingEngine() throws InvalidSyntaxException {
		BundleContext context = mock(BundleContext.class);
		when(context.getServiceReferences(anyString(), isNull(String.class)))
				.thenReturn(null);
		new Extender(context);
		ScriptBindingsFactory bindingsFactory = mock(ScriptBindingsFactory.class);
		VariableScope variableScope = mock(VariableScope.class);
		OsgiScriptingEngines scriptingEngines = new OsgiScriptingEngines(
				bindingsFactory);
		Object evaluate = scriptingEngines.evaluate("print String", "Python",
				variableScope);
		assertThat(evaluate, is(nullValue()));
	}

	@Test
	public void evaluateWithExistingEngine() throws InvalidSyntaxException,
			ScriptException {
		BundleContext context = mock(BundleContext.class);
		ServiceReference serviceReference = mock(ServiceReference.class);
		ScriptEngineResolver scriptEngineResolver = mock(ScriptEngineResolver.class);
		when(context.getService(serviceReference)).thenReturn(
				scriptEngineResolver);
		when(context.getServiceReferences(anyString(), isNull(String.class)))
				.thenReturn(new ServiceReference[] { serviceReference });
		ScriptEngine scriptEngine = mock(ScriptEngine.class);
		when(scriptEngineResolver.resolveScriptEngine("Python")).thenReturn(
				scriptEngine);
		new Extender(context);
		ScriptBindingsFactory bindingsFactory = mock(ScriptBindingsFactory.class);
		when(scriptEngine.eval(anyString(), isNull(Bindings.class)))
				.thenReturn("Success");
		when(scriptEngine.eval(anyString(), any(Bindings.class)))
		.thenReturn("Success");
		OsgiScriptingEngines scriptingEngines = new OsgiScriptingEngines(
				bindingsFactory);
		Object evaluate = scriptingEngines.evaluate("print String", "Python",
				mock(VariableScope.class));
		assertThat(evaluate.toString(), is("Success"));
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=ProcessEngineException.class)
	public void evaluateSkriptWithSyntaxError() throws InvalidSyntaxException,
			ScriptException {
		BundleContext context = mock(BundleContext.class);
		ServiceReference serviceReference = mock(ServiceReference.class);
		ScriptEngineResolver scriptEngineResolver = mock(ScriptEngineResolver.class);
		when(context.getService(serviceReference)).thenReturn(
				scriptEngineResolver);
		when(context.getServiceReferences(anyString(), isNull(String.class)))
				.thenReturn(new ServiceReference[] { serviceReference });
		ScriptEngine scriptEngine = mock(ScriptEngine.class);
		when(scriptEngineResolver.resolveScriptEngine("Python")).thenReturn(
				scriptEngine);
		new Extender(context);
		ScriptBindingsFactory bindingsFactory = mock(ScriptBindingsFactory.class);
		when(scriptEngine.eval(anyString(), isNull(Bindings.class)))
				.thenThrow(new Class[]{ ScriptException.class});
		OsgiScriptingEngines scriptingEngines = new OsgiScriptingEngines(
				bindingsFactory);
		scriptingEngines.evaluate("print String", "Python",
				mock(VariableScope.class));
	}
}
