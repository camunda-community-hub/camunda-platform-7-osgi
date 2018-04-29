package org.camunda.bpm.extension.osgi.itest.el;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
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
import static org.junit.Assert.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiELTenantIntegrationTest extends AbstractOSGiELResolverIntegrationTest {

  protected void deployProcessDefinition() {
    File subProcess = new File("src/test/resources/testprocess.bpmn");
    File tenantSpecificSubprocess = new File("src/test/resources/el/tenantSpecificProcess.bpmn");
    File processDef = new File("src/test/resources/el/tenantParentProcess.bpmn");
    DeploymentBuilder builder = processEngine.getRepositoryService()
      .createDeployment();
    builder.name(getClass().getName());
    try {
      // deploy the default parent process  and the default subprocess (tenant = null)
      builder
        .addInputStream(processDef.getName(), new FileInputStream(processDef))
        .addInputStream(subProcess.getName(), new FileInputStream(subProcess))
        .deploy();

      // deploy the tenant specific subprocess in a separate deployment
      processEngine.getRepositoryService()
        .createDeployment()
        .addInputStream(tenantSpecificSubprocess.getName(), new FileInputStream(tenantSpecificSubprocess))
        .tenantId("t1")
        .deploy();

    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
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
  public void tenantSubprocessShouldBeResolved() throws InterruptedException {
    Hashtable<String, String> properties = new Hashtable<String, String>();
    properties.put("processExpression", "calledElementTenantIdProvider");
    MockedCalledElementTenantIdProvider service = new MockedCalledElementTenantIdProvider("t1");
    ctx.registerService(MockedCalledElementTenantIdProvider.class, service, properties);

    RuntimeService runtimeService = processEngine.getRuntimeService();
    runtimeService.startProcessInstanceByKey("tenantParentProcess");

    ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
      .processDefinitionKey("Process_1").singleResult();
    assertThat(processInstance.isEnded(), is(false));
    Task task = processEngine.getTaskService().createTaskQuery().singleResult();
    assertThat(task.getName(), is("specific tenant task"));
  }

  @Test
  public void defaultSubprocessShouldBeResolvedForNullTenant() throws InterruptedException {
    Hashtable<String, String> properties = new Hashtable<String, String>();
    properties.put("processExpression", "calledElementTenantIdProvider");
    MockedCalledElementTenantIdProvider service = new MockedCalledElementTenantIdProvider(null);
    ctx.registerService(MockedCalledElementTenantIdProvider.class, service, properties);

    RuntimeService runtimeService = processEngine.getRuntimeService();
    runtimeService.createProcessInstanceByKey("tenantParentProcess")
      .execute();

    // check if default sub process is called (tenant id = null)
    ProcessInstance subProcessInstance = runtimeService.createProcessInstanceQuery()
      .processDefinitionKey("Process_1").singleResult();

    assertNull(subProcessInstance.getTenantId());
  }

  @Override
  protected File getProcessDefinition() {
    return null; // null here because we override deployProcessDefinition
  }

  /**
   * mock in order to return a "calculated" tenantId at runtime - when calling the subprocess
   * inside tenantParentProcess.bpmn
   */
  private class MockedCalledElementTenantIdProvider {
    private String tenantToReturn;

    public MockedCalledElementTenantIdProvider(String tenantToReturn) {
      this.tenantToReturn = tenantToReturn;
    }

    public String resolveTenantId(DelegateExecution execution) {
      return this.tenantToReturn;
    }

    public String getTenantToReturn() {
      return tenantToReturn;
    }
  }
}
