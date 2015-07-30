package org.camunda.bpm.extension.osgi.application;

import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.extension.osgi.application.impl.BlueprintBundleLocalELResolver;

/**
 * Interface, so we can inject the real {@link BlueprintBundleLocalELResolver}
 * into the test-class. TODO find a better way
 * 
 */
public interface TestBlueprintBundleLocalELResolver {

  Object getValue(ELContext context, Object base, Object property);

}
