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

import org.camunda.bpm.container.impl.deployment.ProcessesXmlStartProcessEnginesStep;
import org.camunda.bpm.container.impl.deployment.StartProcessEngineStep;
import org.camunda.bpm.container.impl.metadata.spi.ProcessEngineXml;
import org.osgi.framework.BundleContext;

/**
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 *
 */
public class OSGiProcessesXmlStartProcessEnginesStep extends ProcessesXmlStartProcessEnginesStep {

  protected BundleContext context;

  public OSGiProcessesXmlStartProcessEnginesStep(BundleContext context) {
    this.context = context;
  }

  protected StartProcessEngineStep createStartProcessEngineStep(ProcessEngineXml processEngineXml) {
    return new OSGiStartProcessEngineStep(processEngineXml, context);
  }

}
