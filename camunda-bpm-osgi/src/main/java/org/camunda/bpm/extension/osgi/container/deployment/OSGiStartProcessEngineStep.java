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

import org.camunda.bpm.container.impl.deployment.StartProcessEngineStep;
import org.camunda.bpm.container.impl.jmx.services.JmxManagedProcessEngineController;
import org.camunda.bpm.container.impl.metadata.spi.ProcessEngineXml;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.util.ReflectUtil;
import org.osgi.framework.BundleContext;

/**
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 *
 */
public class OSGiStartProcessEngineStep extends StartProcessEngineStep {

  protected BundleContext context;

  public OSGiStartProcessEngineStep(ProcessEngineXml processEngineXml, BundleContext context) {
    super(processEngineXml);
    this.context = context;
  }

  protected JmxManagedProcessEngineController createProcessEngineControllerInstance(ProcessEngineConfigurationImpl configuration) {
    return new BundleClassloaderAwareProcessEngineController(configuration, context);
  }

  @SuppressWarnings("unchecked")
  protected <T> Class<? extends T> loadClass(String className, ClassLoader customClassloader, Class<T> clazz) {

    Class<? extends T> configurationClass = null;

    // give process applicaiton the opportunity to provide a custom process engine configuration class
    if(customClassloader != null) {
      try {
        configurationClass = (Class<? extends T>) customClassloader.loadClass(className);
      } catch(ClassNotFoundException e) {
        // ok
      }
    }

    if(customClassloader == null || configurationClass == null) {
      configurationClass = (Class<? extends T>) ReflectUtil.loadClass(className);
    }

    return configurationClass;
  }

}
