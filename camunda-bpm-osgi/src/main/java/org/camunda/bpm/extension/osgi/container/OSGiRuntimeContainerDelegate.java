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
package org.camunda.bpm.extension.osgi.container;

import java.util.logging.Logger;

import org.camunda.bpm.application.AbstractProcessApplication;
import org.camunda.bpm.container.impl.RuntimeContainerDelegateImpl;
import org.camunda.bpm.container.impl.deployment.Attachments;
import org.camunda.bpm.container.impl.deployment.PostDeployInvocationStep;
import org.camunda.bpm.container.impl.deployment.StartProcessApplicationServiceStep;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.extension.osgi.container.deployment.OSGiDeployProcessArchivesStep;
import org.camunda.bpm.extension.osgi.container.deployment.OSGiParseProcessesXmlStep;
import org.camunda.bpm.extension.osgi.container.deployment.OSGiProcessesXmlStartProcessEnginesStep;
import org.osgi.framework.BundleContext;

/**
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 *
 */
public class OSGiRuntimeContainerDelegate extends RuntimeContainerDelegateImpl {

  private final Logger LOGGER = Logger.getLogger(OSGiRuntimeContainerDelegate.class.getName());

  protected BundleContext context;

  /**
   * @param context
   */
  public OSGiRuntimeContainerDelegate(BundleContext context) {
    this.context = context;
  }

  public void deployProcessApplication(AbstractProcessApplication processApplication) {

    if(processApplication == null) {
      throw new ProcessEngineException("Process application cannot be null");
    }

    final String operationName = "Deployment of Process Application "+processApplication.getName();

    getServiceContainer().createDeploymentOperation(operationName)
      .addAttachment(Attachments.PROCESS_APPLICATION, processApplication)
      .addStep(new OSGiParseProcessesXmlStep())
      .addStep(new OSGiProcessesXmlStartProcessEnginesStep(context))
      .addStep(new OSGiDeployProcessArchivesStep())
      .addStep(new StartProcessApplicationServiceStep())
      .addStep(new PostDeployInvocationStep())
      .execute();

    LOGGER.info("Process Application "+processApplication.getName()+" successfully deployed.");
  }

}
