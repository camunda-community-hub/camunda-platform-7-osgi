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

import java.util.Properties;

import javax.inject.Inject;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.extension.osgi.application.impl.OSGiProcessApplication;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.BundleContext;

/**
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 *
 */
@RunWith(PaxExam.class)
public class ProcessApplicationDeployerTest extends OSGiTestCase {

  @Inject
  protected BundleContext bundleContext;

  @Test
  public void shouldDeployProcessApp() {
    OSGiProcessApplication osgiProcessApplication = new MyProcessApplication();
    bundleContext.registerService(ProcessApplicationInterface.class.getName(), osgiProcessApplication, new Properties());
  }

  @ProcessApplication("yo!")
  public static class MyProcessApplication extends OSGiProcessApplication {

    @PostDeploy
    public void sayHello(ProcessEngine processEngine) {

      processEngine.getRuntimeService()
        .startProcessInstanceByKey("foo");

    }

    public void createDeployment(String processArchiveName, DeploymentBuilder deploymentBuilder) {
      BpmnModelInstance bpmnModelInstance = Bpmn.createExecutableProcess("foo")
        .startEvent()
        .scriptTask()
          .scriptText("println('Yoyo');")
          .scriptFormat("Javascript")
        .endEvent()
      .done();

      deploymentBuilder.addModelInstance("process.bpmn", bpmnModelInstance);
    }

  }

}
