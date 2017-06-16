package org.camunda.bpm.extension.osgi.itest.el;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.extension.osgi.el.OSGiExpressionManager;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Integration test to check that the business key is correctly passed to a subprocess.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiBusinessKeyIntegrationTest extends AbstractOSGiELResolverIntegrationTest {


  protected void deployProcessDefinition() {
    File subProcess = new File("src/test/resources/testprocess.bpmn");
    File processDef = new File("src/test/resources/el/businessKey.bpmn");
    DeploymentBuilder builder = processEngine.getRepositoryService()
      .createDeployment();
    builder.name(getClass().getName());
    try {
      builder
        .addInputStream(processDef.getName(), new FileInputStream(processDef))
        .addInputStream(subProcess.getName(), new FileInputStream(subProcess))
        .deploy();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected File getProcessDefinition() {
    return null; // null here because we override deployProcessDefinition
  }

  private void createProcessEngine() {
    StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
    configuration.setDatabaseSchemaUpdate("create-drop")
      .setDataSource(createDatasource())
      .setJobExecutorActivate(false);
    configuration.setExpressionManager(new OSGiExpressionManager());
    ProcessEngineFactory processEngineFactory = new ProcessEngineFactory();
    processEngineFactory.setProcessEngineConfiguration(configuration);
    processEngineFactory
      .setBundle(getBundle("org.camunda.bpm.extension.osgi"));
    try {
      processEngineFactory.init();
      processEngine = processEngineFactory.getObject();
      ctx.registerService(ProcessEngine.class.getName(), processEngine,
        new Hashtable<String, String>());
    } catch (Exception e) {
      fail(e.toString());
    }
  }

  @Test
  public void businessKeyShouldBeAccessible() throws InterruptedException {
    String businessKey = "datKey";
    RuntimeService runtimeService = processEngine.getRuntimeService();
    String processInstanceId = runtimeService.startProcessInstanceByKey("Process_123", businessKey).getId();

    ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    assertThat(processInstance.isEnded(), is(false));
    Task task = processEngine.getTaskService().createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
    assertThat(task.getName(), is("Test"));
  }


}
