package org.camunda.bpm.extension.osgi.application;

import org.camunda.bpm.application.AbstractProcessApplication;
import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.extension.osgi.application.impl.BlueprintBundleLocalELResolver;
import org.camunda.bpm.extension.osgi.application.impl.OSGiProcessApplicationReference;
import org.camunda.bpm.extension.osgi.classloading.BundleDelegatingClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.service.blueprint.container.BlueprintContainer;

public class OSGiProcessApplication extends AbstractProcessApplication {

  private Bundle bundle;
  private BundleDelegatingClassLoader bundleDelegatingCL;
  private ProcessApplicationReference reference;
  private BlueprintContainer blueprintContainer;

  public OSGiProcessApplication(Bundle bundle, BlueprintContainer blueprintContainer) {
    this.bundle = bundle;
    this.blueprintContainer = blueprintContainer;
    bundleDelegatingCL = new BundleDelegatingClassLoader(bundle);
  }

  @Override
  public ProcessApplicationReference getReference() {
    if (reference == null) {
      reference = new OSGiProcessApplicationReference(this, getName());
    }
    return reference;
  }

  @Override
  protected String autodetectProcessApplicationName() {
    return getBundle().getSymbolicName();
  }

  @Override
  public ClassLoader getProcessApplicationClassloader() {
    return bundleDelegatingCL;
  }

  @Override
  protected ELResolver initProcessApplicationElResolver() {
    BlueprintBundleLocalELResolver elResolver = new BlueprintBundleLocalELResolver();
    elResolver.setBlueprintContainer(blueprintContainer);
    return elResolver;
  }

  public Bundle getBundle() {
    return bundle;
  }
}
