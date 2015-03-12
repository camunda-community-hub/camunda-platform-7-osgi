package org.camunda.bpm.extension.osgi.eventing;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.camunda.bpm.extension.osgi.eventing.api.OSGiEventBridgeActivator;
import org.camunda.bpm.extension.osgi.eventing.impl.GlobalOSGiEventBridgeActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;

/**
 * @author Ronny Bräunlich
 */
public class Activator extends DependencyActivatorBase {
  @Override
  public void init(BundleContext context, DependencyManager manager) throws Exception {
    manager.add(createComponent()
      .setImplementation(GlobalOSGiEventBridgeActivator.class)
      .add(createServiceDependency()
          .setService(EventAdmin.class)
          .setRequired(true)
      )
      .add(createBundleDependency().setBundle(context.getBundle()).setRequired(true))
      .setInterface(OSGiEventBridgeActivator.class.getName(), null));
  }
}
