package org.camunda.bpm.extension.osgi.itest.application;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.extension.osgi.application.OSGiProcessApplication;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@ProcessApplication("yo!")
public class MyProcessApplication extends OSGiProcessApplication {


  public MyProcessApplication(BundleContext ctx, BlueprintContainer blueprintContainer) {
    super(ctx.getBundle(), blueprintContainer);
  }

  /**
   * @param processEngine
   */
  @PostDeploy
  public void sayHello(ProcessEngine processEngine) {
  }

  @Override
  public void createDeployment(String processArchiveName, DeploymentBuilder deploymentBuilder) {
    BpmnModelInstance bpmnModelInstance = Bpmn.createExecutableProcess("foo")
      .startEvent()
       .serviceTask()
         .camundaExpression("${myBean}")
      .endEvent()
    .done();

    deploymentBuilder.addModelInstance("process.bpmn", bpmnModelInstance);
  }

}
