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

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.extension.osgi.internal.ProcessDefinitionDeployer;
import org.camunda.bpm.extension.osgi.internal.impl.ProcessDefinitionCheckerImpl;
import org.camunda.bpm.extension.osgi.internal.impl.ProcessDefinitionDeployerImpl;
import org.camunda.bpm.extension.osgi.scripting.impl.ScriptEngineBundleTrackerCustomizer;
import org.osgi.framework.BundleContext;

/**
 * OSGi Activator
 * 
 * @author <a href="gnodet@gmail.com">Guillaume Nodet</a>
 * @author Ronny Bräunlich
 * @author Daniel Meyer
 * @author Roman Smirnov
 */
public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager)
			throws Exception {
		
		manager.add(createComponent()
						.setImplementation(ProcessDefinitionDeployerImpl.class)
						.setInterface(ProcessDefinitionDeployer.class.getName(), null)
						.add(createServiceDependency()
								.setService(ProcessEngine.class)
								.setRequired(true)));
		
		manager.add(createComponent()
						.setImplementation(ProcessDefinitionCheckerImpl.class)
						.add(createBundleDependency()
								.setCallbacks("checkBundle","checkBundle", "bundleRemoved"))
						.add(createServiceDependency()
								.setService(ProcessDefinitionDeployer.class)
								.setRequired(true)));

		manager.add(createComponent()
						.setImplementation(ScriptEngineBundleTrackerCustomizer.class)
						.add(createBundleDependency()
								.setCallbacks("addingBundle", "modifiedBundle", "removedBundle")));
		
	}
}
