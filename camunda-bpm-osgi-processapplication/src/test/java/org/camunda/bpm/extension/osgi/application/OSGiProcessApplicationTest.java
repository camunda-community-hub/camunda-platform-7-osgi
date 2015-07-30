package org.camunda.bpm.extension.osgi.application;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.application.ProcessApplicationUnavailableException;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.extension.osgi.application.impl.BlueprintBundleLocalELResolver;
import org.camunda.bpm.extension.osgi.application.impl.OSGiProcessApplicationReference;
import org.camunda.bpm.extension.osgi.blueprint.BundleDelegatingClassLoader;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.service.blueprint.container.BlueprintContainer;

public class OSGiProcessApplicationTest {

  private static final String BUNDLE_NAME = "foo.bar";

  @Test
  public void getReference() throws ProcessApplicationUnavailableException {
    OSGiProcessApplication app = new OSGiProcessApplication(createBundleMock(), createBlueprintContainerMock());
    ProcessApplicationReference ref = app.getReference();
    assertThat(ref, is(instanceOf(OSGiProcessApplicationReference.class)));
    assertThat((OSGiProcessApplication) ref.getProcessApplication(), is(sameInstance(app)));
    assertThat(ref.getName(), is(BUNDLE_NAME));
  }

  @Test
  public void autodetectProcessApplicationName() {
    OSGiProcessApplication app = new OSGiProcessApplication(createBundleMock(), createBlueprintContainerMock());
    assertThat(app.autodetectProcessApplicationName(), is(BUNDLE_NAME));
  }

  @Test
  public void getProcessApplicationClassloader() {
    Bundle bundle = createBundleMock();
    OSGiProcessApplication app = new OSGiProcessApplication(bundle, createBlueprintContainerMock());
    ClassLoader cl = app.getProcessApplicationClassloader();
    assertThat(cl, is(instanceOf(BundleDelegatingClassLoader.class)));
    assertThat(((BundleDelegatingClassLoader) cl).getBundle(), is(sameInstance(bundle)));
  }

  @Test
  public void initProcessApplicationElResolver() {
    BlueprintContainer containerMock = createBlueprintContainerMock();
    OSGiProcessApplication app = new OSGiProcessApplication(createBundleMock(), containerMock);
    ELResolver elResolver = app.getElResolver();
    assertThat(elResolver, is(instanceOf(BlueprintBundleLocalELResolver.class)));
    assertThat(((BlueprintBundleLocalELResolver) elResolver).getBlueprintContainer(), is(sameInstance(containerMock)));
  }

  private BlueprintContainer createBlueprintContainerMock() {
    return mock(BlueprintContainer.class);
  }

  private Bundle createBundleMock() {
    Bundle bundleMock = mock(Bundle.class);
    when(bundleMock.getSymbolicName()).thenReturn(BUNDLE_NAME);
    return bundleMock;

  }

}
