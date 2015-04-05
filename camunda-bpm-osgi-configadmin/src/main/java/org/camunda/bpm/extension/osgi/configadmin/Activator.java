package org.camunda.bpm.extension.osgi.configadmin;

import java.util.Collections;
import java.util.Hashtable;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.camunda.bpm.extension.osgi.configadmin.impl.ManagedProcessEngineFactoryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedServiceFactory;

public class Activator extends DependencyActivatorBase {

  @Override
  public void init(BundleContext context, DependencyManager manager) throws Exception {
    manager.add(createComponent()
        .setInterface(ManagedServiceFactory.class.getName(),
            new Hashtable<String, String>(Collections.singletonMap(Constants.SERVICE_PID, ManagedProcessEngineFactory.SERVICE_PID)))
        .setImplementation(ManagedProcessEngineFactoryImpl.class).add(createBundleDependency().setBundle(context.getBundle()).setRequired(true)));
  }

}
