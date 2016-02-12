package org.camunda.bpm.extension.osgi.scripting.impl;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;

import org.camunda.bpm.extension.osgi.scripting.ScriptEngineResolver;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ScriptEngineBundleScannerTest {

  private URL scriptEngineUrl;

  @Test
  public void registerNonExistingScriptEngine() {
    Bundle bundle = mock(Bundle.class);
    when(bundle.findEntries(any(String.class), any(String.class), anyBoolean())).thenReturn(Collections.enumeration(Collections.<URL> emptyList()));
    ScriptEngineBundleScanner customizer = new ScriptEngineBundleScanner();
    ArrayList<BundleScriptEngineResolver> resolvers = new ArrayList<BundleScriptEngineResolver>(0);
    customizer.registerScriptEngines(bundle, resolvers);
    assertThat(resolvers.isEmpty(), is(true));
  }

  @Test
  public void registerNullScriptEngine() {
    Bundle bundle = mock(Bundle.class);
    when(bundle.findEntries(any(String.class), any(String.class), anyBoolean())).thenReturn(null);
    ScriptEngineBundleScanner customizer = new ScriptEngineBundleScanner();
    ArrayList<BundleScriptEngineResolver> resolvers = new ArrayList<BundleScriptEngineResolver>(0);
    customizer.registerScriptEngines(bundle, resolvers);
    assertThat(resolvers.isEmpty(), is(true));
  }

  @Test
  public void registerScriptEngine() throws MalformedURLException {
    Bundle bundle = mockBundleWithScriptEngine(435L);
    ScriptEngineBundleScanner customizer = new ScriptEngineBundleScanner();
    ArrayList<BundleScriptEngineResolver> resolvers = new ArrayList<BundleScriptEngineResolver>(1);
    customizer.registerScriptEngines(bundle, resolvers);
    assertThat(resolvers.size(), is(1));
    BundleScriptEngineResolver resolver = resolvers.get(0);
    assertThat(resolver, is(instanceOf(BundleScriptEngineResolver.class)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void addingBundleWithService() throws MalformedURLException {
    long bundleId = 123L;
    Bundle bundle = mockBundleWithScriptEngine(bundleId);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    ScriptEngineBundleScanner customizer = new ScriptEngineBundleScanner();
    Bundle addedBundle = customizer.addBundle(bundle);
    assertThat(addedBundle, is(bundle));
    BundleScriptEngineResolver bundleScriptEngineResolver = customizer.getResolvers().get(bundleId).get(0);
    verify(bundleContext).registerService(eq(ScriptEngineResolver.class), eq(bundleScriptEngineResolver), (Dictionary<String, ?>) isNull());
    assertThat(customizer.getResolvers().size(), is(1));
    assertThat(customizer.getResolvers().containsKey(bundleId), is(true));
  }

  @Test
  public void removedBundleWithoutScriptingEngine() {
    long bundleId = 123L;
    Bundle bundle = mock(Bundle.class);
    when(bundle.getBundleId()).thenReturn(bundleId);
    ScriptEngineBundleScanner customizer = new ScriptEngineBundleScanner();
    customizer.addBundle(bundle);
    customizer.removedBundle(bundle);
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
    ScriptEngineBundleScanner customizer = new ScriptEngineBundleScanner();
    customizer.addBundle(bundle);
    customizer.addBundle(bundle2);
    customizer.addBundle(bundle3);
    customizer.removedBundle(bundle);
    assertThat(customizer.getResolvers().size(), is(2));
    assertThat(customizer.getResolvers().get(bundleId), is(nullValue()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void removeBundleWithService() throws MalformedURLException {
    Bundle bundle = mockBundleWithScriptEngine(1234L);
    BundleContext bundleContext = mock(BundleContext.class);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    ServiceRegistration<ScriptEngineResolver> serviceRegMock = mock(ServiceRegistration.class);
    when(bundleContext.registerService(eq(ScriptEngineResolver.class), any(BundleScriptEngineResolver.class), (Dictionary<String, ?>) isNull())).thenReturn(
        serviceRegMock);

    ScriptEngineBundleScanner customizer = new ScriptEngineBundleScanner();
    customizer.addBundle(bundle);
    customizer.removedBundle(bundle);
    verify(serviceRegMock).unregister();
  }

  public Bundle mockBundleWithScriptEngine(Long bundleId) {
    try {
      scriptEngineUrl = new URL("http://localhost");
    } catch (MalformedURLException e) {
      e.printStackTrace();
      fail();
    }
    Bundle bundle = mock(Bundle.class);
    when(bundle.getSymbolicName()).thenReturn("mock bundle");
    when(bundle.findEntries(any(String.class), any(String.class), anyBoolean())).thenReturn(Collections.enumeration(Collections.singleton(scriptEngineUrl)));
    when(bundle.getBundleId()).thenReturn(bundleId);
    return bundle;
  }
}
