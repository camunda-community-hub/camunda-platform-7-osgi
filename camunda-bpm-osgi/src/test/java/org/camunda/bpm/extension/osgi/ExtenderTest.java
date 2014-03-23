package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import javax.script.ScriptEngine;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class ExtenderTest {

	private Extender extender;

	private BundleContext createBundleContextMock() {
		BundleContext bundleContext = mock(BundleContext.class);
		return bundleContext;
	}

	@Before
	public void setUp() {
		extender = new Extender(createBundleContextMock());
	}

	@Test
	public void emptyBundleChanged() {
		// mock Bundle
		Bundle bundle = mock(Bundle.class);
		when(bundle.getHeaders()).thenReturn(new Properties());
		when(bundle.getSymbolicName()).thenReturn("org.camunda.test");
		// call the extender
		extender.bundleChanged(createMockBundleEventFor(bundle,
				BundleEvent.RESOLVED));
		// nothing should happen
	}

	@Test
	public void bundleWithoutProcessDefinitionChanged()
			throws MalformedURLException {
		// mock Bundle
		Bundle bundle = mock(Bundle.class);
		when(bundle.getHeaders()).thenReturn(new Properties());
		when(bundle.getSymbolicName()).thenReturn("org.camunda.test");
		ArrayList<URL> urls = new ArrayList<URL>();
		urls.add(new URL("file:///"));
		when(bundle.findEntries(anyString(), anyString(), anyBoolean()))
				.thenReturn(Collections.enumeration(urls));
		// call the extender
		extender.bundleChanged(createMockBundleEventFor(bundle,
				BundleEvent.RESOLVED));
		// nothing should happen
	}

	private BundleEvent createMockBundleEventFor(Bundle bundle, int event) {
		BundleEvent bundleEvent = mock(BundleEvent.class);
		when(bundleEvent.getType()).thenReturn(event);
		when(bundleEvent.getBundle()).thenReturn(bundle);
		return bundleEvent;
	}

	@Test
	public void addingService() {
		BundleContext bundleContext = mock(BundleContext.class);
		ServiceReference serviceRef = mock(ServiceReference.class);
		Object object = new Object();
		when(bundleContext.getService(serviceRef)).thenReturn(object);
		Object service = new Extender(bundleContext).addingService(serviceRef);
		assertThat(service, is(object));
	}

	@Test
	public void removedService() {
		BundleContext bundleContext = mock(BundleContext.class);
		ServiceReference serviceRef = mock(ServiceReference.class);
		new Extender(bundleContext).removedService(serviceRef, new Object());
		verify(bundleContext).ungetService(eq(serviceRef));
	}

	@Test
	public void resolveScriptEngineWhenNoScriptEngineResolverIsPresent()
			throws InvalidSyntaxException {
		BundleContext bundleContext = mock(BundleContext.class);
		new Extender(bundleContext);
		ScriptEngine scriptEngine = Extender.resolveScriptEngine("foo");
		assertThat(scriptEngine, is(nullValue()));
	}

	@Test
	public void resolveScriptEngine() throws InvalidSyntaxException {
		BundleContext bundleContext = mock(BundleContext.class);
		ServiceReference serviceRef = mock(ServiceReference.class);
		when(
				bundleContext.getServiceReferences(
						eq(ScriptEngineResolver.class.getName()),
						isNull(String.class))).thenReturn(
				new ServiceReference[] { serviceRef });
		ScriptEngineResolver scriptEngineResolver = mock(ScriptEngineResolver.class);
		when(bundleContext.getService(eq(serviceRef))).thenReturn(
				scriptEngineResolver);
		ScriptEngine scriptEngine = new TestScriptEngineFactory.TestScriptEngine();
		when(scriptEngineResolver.resolveScriptEngine(eq("foo"))).thenReturn(
				scriptEngine);
		new Extender(bundleContext);
		ScriptEngine resolvedScriptEngine = Extender.resolveScriptEngine("foo");
		assertThat(resolvedScriptEngine, is(scriptEngine));
	}
}
