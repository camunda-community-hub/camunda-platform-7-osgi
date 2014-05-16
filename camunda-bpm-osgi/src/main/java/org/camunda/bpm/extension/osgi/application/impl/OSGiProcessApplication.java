package org.camunda.bpm.extension.osgi.application.impl;

import org.camunda.bpm.application.AbstractProcessApplication;
import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.extension.osgi.blueprint.BundleDelegatingClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class OSGiProcessApplication extends AbstractProcessApplication {

  private static final ELResolver EL_RESOLVER = new BlueprintBundleLocalELResolver();
  private Bundle bundle = FrameworkUtil.getBundle(getClass());
  private BundleDelegatingClassLoader bundleDelegatingCL = new BundleDelegatingClassLoader(bundle);
  private ProcessApplicationReference reference;

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
    return FrameworkUtil.getBundle(getClass()).getSymbolicName();
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
