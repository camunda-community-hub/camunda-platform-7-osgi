package org.camunda.bpm.extension.osgi.application;

import org.camunda.bpm.application.AbstractProcessApplication;
import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.extension.osgi.application.impl.BlueprintBundleLocalELResolver;
import org.camunda.bpm.extension.osgi.application.impl.OSGiProcessApplicationReference;
import org.camunda.bpm.extension.osgi.blueprint.BundleDelegatingClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.blueprint.container.BlueprintContainer;

public class OSGiProcessApplication extends AbstractProcessApplication {

  private static final BlueprintBundleLocalELResolver EL_RESOLVER = new BlueprintBundleLocalELResolver();
  private Bundle bundle = FrameworkUtil.getBundle(getClass());
  private BundleDelegatingClassLoader bundleDelegatingCL = new BundleDelegatingClassLoader(bundle);
  private ProcessApplicationReference reference;

  public OSGiProcessApplication(Bundle bundle, BlueprintContainer blueprintContainer){
    this.bundle = bundle;
    EL_RESOLVER.setBlueprintContainer(blueprintContainer);
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
    //FIXME always subclass or injection of BundleContext?
    return getBundle().getSymbolicName();
  }

  @Override
  public ClassLoader getProcessApplicationClassloader() {
    return bundleDelegatingCL;
  }

  @Override
  protected ELResolver initProcessApplicationElResolver() {
    return EL_RESOLVER;
  }

  public Bundle getBundle() {
    return bundle;
  }
}
