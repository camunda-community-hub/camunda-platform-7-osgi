/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.extension.osgi;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.extension.osgi.internal.ProcessDefintionChecker;
import org.camunda.bpm.extension.osgi.internal.impl.ProcessDefinitionCheckerImpl;
import org.camunda.bpm.extension.osgi.internal.impl.ProcessDefinitionDeployerImpl;
import org.camunda.bpm.extension.osgi.scripting.impl.ScriptEngineBundleTrackerCustomizer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author <a href="gnodet@gmail.com">Guillaume Nodet</a>
 * @author Ronny Bräunlich
 */
public class Extender implements ServiceTrackerCustomizer {

	private final BundleContext context;
	private final BundleTracker bundleTracker;
	private final ServiceTracker engineServiceTracker;
	private final ProcessDefintionChecker procDefChecker;

	public Extender(BundleContext context) {
		this.context = context;
		this.engineServiceTracker = new ServiceTracker(context,
				ProcessEngine.class.getName(), this);
		procDefChecker = new ProcessDefinitionCheckerImpl(
				new ProcessDefinitionDeployerImpl(engineServiceTracker));
		this.bundleTracker = new BundleTracker(context, Bundle.RESOLVED
				| Bundle.STARTING | Bundle.ACTIVE,
				new ScriptEngineBundleTrackerCustomizer(procDefChecker));
	}

	public void open() {
		engineServiceTracker.open();
	}

	public void close() {
		engineServiceTracker.close();
	}

	public Object addingService(ServiceReference reference) {
		new Thread() {
			public void run() {
				bundleTracker.open();
			}
		}.start();
		return context.getService(reference);
	}

	public void modifiedService(ServiceReference reference, Object service) {
	}

	public void removedService(ServiceReference reference, Object service) {
		context.ungetService(reference);
		if (engineServiceTracker.size() == 0) {
			bundleTracker.close();
		}
	}

	public void bundleChanged(BundleEvent event) {
		Bundle bundle = event.getBundle();
		if (event.getType() == BundleEvent.RESOLVED) {
			procDefChecker.checkBundle(bundle);
		}
	}
}
