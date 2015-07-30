package org.camunda.bpm.extension.osgi.application;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.extension.osgi.application.impl.ProcessApplicationDeployer;
import org.camunda.bpm.extension.osgi.container.OSGiRuntimeContainerDelegate;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Ronny Bräunlich
 *
 */
public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager)
			throws Exception {

		RuntimeContainerDelegate.INSTANCE.set(new OSGiRuntimeContainerDelegate(
				context));
		manager.add(createComponent().setImplementation(
				ProcessApplicationDeployer.class).add(
				createServiceDependency().setService(
						ProcessApplicationInterface.class).setCallbacks(
						"addProcessApplication", "removeProcessApplication")));
	}

}
