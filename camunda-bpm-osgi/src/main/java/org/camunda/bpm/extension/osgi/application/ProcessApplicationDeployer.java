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
package org.camunda.bpm.extension.osgi.application;

import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.blueprint.BundleDelegatingClassLoader;
import org.camunda.bpm.extension.osgi.blueprint.ClassLoaderWrapper;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 *
 */
public class ProcessApplicationDeployer implements ServiceTrackerCustomizer<ProcessApplicationInterface, ProcessApplicationInterface> {

  @Override
  public ProcessApplicationInterface addingService(ServiceReference<ProcessApplicationInterface> reference) {

    Bundle bundle = reference.getBundle();
    BundleContext bundleContext = bundle.getBundleContext();

    ClassLoader previous = Thread.currentThread().getContextClassLoader();

    try {
      ClassLoader cl = new BundleDelegatingClassLoader(bundle);

      Thread.currentThread().setContextClassLoader(
          new ClassLoaderWrapper(cl, ProcessEngineFactory.class.getClassLoader(), ProcessEngineConfiguration.class.getClassLoader(), previous));

      ProcessApplicationInterface app = bundleContext.getService(reference);

      app.deploy();
      return app;

    } finally {
      Thread.currentThread().setContextClassLoader(previous);
    }

  }

  @Override
  public void modifiedService(ServiceReference<ProcessApplicationInterface> reference, ProcessApplicationInterface service) {

  }

  @Override
  public void removedService(ServiceReference<ProcessApplicationInterface> reference, ProcessApplicationInterface service) {
    service.undeploy();
  }

}
