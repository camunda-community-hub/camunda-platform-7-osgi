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
package org.camunda.bpm.extension.osgi.container.deployment;

import java.util.Properties;

import org.camunda.bpm.container.impl.jmx.services.JmxManagedProcessEngineController;
import org.camunda.bpm.container.impl.spi.PlatformServiceContainer;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.blueprint.ProcessEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 *
 */
public class BundleClassloaderAwareProcessEngineController extends JmxManagedProcessEngineController {

  protected ProcessEngineFactory processEngineFactory;
  protected BundleContext context;
  protected ServiceRegistration registration;

  public BundleClassloaderAwareProcessEngineController(ProcessEngineConfiguration processEngineConfiguration, BundleContext context) {
    super(processEngineConfiguration);
    this.context = context;
    this.processEngineFactory = new ProcessEngineFactory();
  }

  public void start(PlatformServiceContainer contanier) {

    processEngineFactory.setProcessEngineConfiguration(processEngineConfiguration);
    processEngineFactory.setBundle(context.getBundle());
    processEngineFactory.init();

    processEngine = processEngineFactory.getObject();

    registration = context.registerService(ProcessEngine.class.getName(), processEngine, new Properties());

  }

  public void stop(PlatformServiceContainer container) {
    registration.unregister();
    processEngineFactory.destroy();
  }

}
