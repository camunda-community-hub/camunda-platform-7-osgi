package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;

public class ScriptEngineBundleTrackerCustomizerTest {

	private ProcessDefintionChecker processDefintionChecker = mock(ProcessDefintionChecker.class);

	private URL scriptEngineUrl;

	@Test
	public void registerNonExistingScriptEngine() {
		Bundle bundle = mock(Bundle.class);
		when(
				bundle.findEntries(any(String.class), any(String.class),
						anyBoolean())).thenReturn(
				Collections.enumeration(Collections.emptyList()));
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		ArrayList<BundleScriptEngineResolver> resolvers = new ArrayList<BundleScriptEngineResolver>(
				0);
		customizer.registerScriptEngines(bundle, resolvers);
		assertThat(resolvers.isEmpty(), is(true));
	}

	@Test
	public void registerNullScriptEngine() {
		Bundle bundle = mock(Bundle.class);
		when(
				bundle.findEntries(any(String.class), any(String.class),
						anyBoolean())).thenReturn(null);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		ArrayList<BundleScriptEngineResolver> resolvers = new ArrayList<BundleScriptEngineResolver>(
				0);
		customizer.registerScriptEngines(bundle, resolvers);
		assertThat(resolvers.isEmpty(), is(true));
	}

	@Test
	public void registerScriptEngine() throws MalformedURLException {
		Bundle bundle = mockBundleWithSkriptEngine(435L);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		ArrayList<BundleScriptEngineResolver> resolvers = new ArrayList<BundleScriptEngineResolver>(
				1);
		customizer.registerScriptEngines(bundle, resolvers);
		assertThat(resolvers.size(), is(1));
		BundleScriptEngineResolver resolver = resolvers.get(0);
		assertThat(resolver, is(instanceOf(BundleScriptEngineResolver.class)));
	}

	@Test
	public void modifiedBundleNullEvent() {
		Bundle bundle = mock(Bundle.class);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		customizer.modifiedBundle(bundle, null, null);
		// nothing should happen
	}

	@Test
	public void modifiedNonResolvedBundle() {
		Bundle bundle = mock(Bundle.class);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		BundleEvent event = new BundleEvent(BundleEvent.INSTALLED, bundle);
		customizer.modifiedBundle(bundle, event, null);
		verifyNoMoreInteractions(processDefintionChecker);
	}

	@Test
	public void modifiedResolvedBundle() {
		Bundle bundle = mock(Bundle.class);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		BundleEvent event = new BundleEvent(BundleEvent.RESOLVED, bundle);
		customizer.modifiedBundle(bundle, event, null);
		verify(processDefintionChecker).checkBundle(bundle);
	}

	@Test
	public void addingResolvedBundleWithNullEvent() {
		addBundle(Bundle.RESOLVED, 123L, null);
		verify(processDefintionChecker).checkBundle(any(Bundle.class));
	}

	@Test
	public void addingStartingBundleWithNullEvent() {
		addBundle(Bundle.STARTING, 1234L, null);
		verify(processDefintionChecker).checkBundle(any(Bundle.class));
	}

	@Test
	public void addingActiveBundleWithNullEvent() {
		addBundle(Bundle.ACTIVE, 1L, null);
		verify(processDefintionChecker).checkBundle(any(Bundle.class));
	}

	@Test
	public void addingStoppingBundleWithNullEvent() {
		addBundle(Bundle.STOPPING, 235L, null);
		verifyZeroInteractions(processDefintionChecker);
	}

	private void addBundle(int state, Long bundleId, Integer eventType) {
		Bundle bundle = mock(Bundle.class);
		when(bundle.getBundleId()).thenReturn(bundleId);
		when(bundle.getState()).thenReturn(state);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		BundleEvent event = null;
		if (eventType != null) {
			event = new BundleEvent(eventType, bundle);
		}
		Bundle addedBundle = (Bundle) customizer.addingBundle(bundle, event);
		assertThat(addedBundle, is(bundle));
		assertThat(customizer.getResolvers().size(), is(1));
		assertThat(customizer.getResolvers().containsKey(bundleId), is(true));
	}

	@Test
	public void addingBundleWithInstalledEvent() {
		addBundle(Bundle.INSTALLED, 34L, BundleEvent.INSTALLED);
		verifyZeroInteractions(processDefintionChecker);
	}

	@Test
	public void addingBundleWithResolvedEvent() {
		addBundle(Bundle.RESOLVED, 34L, BundleEvent.RESOLVED);
		verify(processDefintionChecker).checkBundle(any(Bundle.class));
	}

	@Test
	public void addingBundleWithService() throws MalformedURLException {
		long bundleId = 123L;
		Bundle bundle = mockBundleWithSkriptEngine(bundleId);
		BundleContext bundleContext = mock(BundleContext.class);
		when(bundle.getBundleContext()).thenReturn(bundleContext);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		Bundle addedBundle = (Bundle) customizer.addingBundle(bundle, null);
		assertThat(addedBundle, is(bundle));
		BundleScriptEngineResolver bundleScriptEngineResolver = customizer
				.getResolvers().get(bundleId).get(0);
		verify(bundleContext).registerService(
				eq(ScriptEngineResolver.class.getName()),
				eq(bundleScriptEngineResolver), (Dictionary<?, ?>) isNull());
	}

	@Test
	public void removedBundleWithoutScriptingEngine() {
		long bundleId = 123L;
		addBundle(Bundle.ACTIVE, bundleId, null);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		Bundle bundle = mock(Bundle.class);
		when(bundle.getBundleId()).thenReturn(bundleId);
		customizer.removedBundle(bundle, null, null);
		assertThat(customizer.getResolvers().isEmpty(), is(true));
	}

	@Test
	public void removedBundleWithoutScriptingEngineWhenMultipleArePresent() {
		long bundleId = 123L;
		Bundle bundle = mock(Bundle.class);
		Bundle bundle2 = mock(Bundle.class);
		Bundle bundle3 = mock(Bundle.class);
		when(bundle.getBundleId()).thenReturn(bundleId);
		when(bundle2.getBundleId()).thenReturn(bundleId + 2);
		when(bundle3.getBundleId()).thenReturn(bundleId + 4);
		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		customizer.addingBundle(bundle, null);
		customizer.addingBundle(bundle2, null);
		customizer.addingBundle(bundle3, null);
		customizer.removedBundle(bundle, null, null);
		assertThat(customizer.getResolvers().size(), is(2));
		assertThat(customizer.getResolvers().get(bundleId), is(nullValue()));
	}

	@Test
	public void removeBundleWithService() throws MalformedURLException {
		Bundle bundle = mockBundleWithSkriptEngine(1234L);
		BundleContext bundleContext = mock(BundleContext.class);
		when(bundle.getBundleContext()).thenReturn(bundleContext);
		ServiceRegistration serviceRegMock = mock(ServiceRegistration.class);
		when(
				bundleContext.registerService(
						eq(ScriptEngineResolver.class.getName()),
						any(BundleScriptEngineResolver.class),
						(Dictionary<?, ?>) isNull()))
				.thenReturn(serviceRegMock);

		ScriptEngineBundleTrackerCustomizer customizer = new ScriptEngineBundleTrackerCustomizer(
				processDefintionChecker);
		customizer.addingBundle(bundle, null);
		customizer.removedBundle(bundle, null, null);
		verify(serviceRegMock).unregister();
	}

	public Bundle mockBundleWithSkriptEngine(Long bundleId) {
		try {
			scriptEngineUrl = new URL("http://localhost");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail();
		}
		Bundle bundle = mock(Bundle.class);
		when(bundle.getSymbolicName()).thenReturn("mock bundle");
		when(
				bundle.findEntries(any(String.class), any(String.class),
						anyBoolean()))
				.thenReturn(
						Collections.enumeration(Collections
								.singleton(scriptEngineUrl)));
		when(bundle.getBundleId()).thenReturn(bundleId);
		return bundle;
	}
}
