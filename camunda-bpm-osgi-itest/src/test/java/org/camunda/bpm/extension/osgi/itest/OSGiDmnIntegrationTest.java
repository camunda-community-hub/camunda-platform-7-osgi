package org.camunda.bpm.extension.osgi.itest;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.osgi.el.OSGiExpressionManager;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.junit.Before;
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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Integration test to check that the DMN engine runs within an OSGi environment.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiDmnIntegrationTest extends OSGiTestEnvironment {

  private ProcessEngine processEngine;

  @Before
  public void setUp() {
    createProcessEngine();
    deployDmnDefinition();
  }

  private void deployDmnDefinition() {
    File decisionDef = new File("src/test/resources/dmn/dinnerDecisions.dmn");
    DeploymentBuilder builder = processEngine.getRepositoryService()
      .createDeployment();
    builder.name(getClass().getName());
    try {
      builder.addInputStream(decisionDef.getName(),
        new FileInputStream(decisionDef)).deploy();
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
  public void shouldUseTheDmnTable(){
    DecisionService decisionService = processEngine.getDecisionService();
    VariableMap variableMap = Variables.createVariables().putValue("season", "Spring").putValue("guestCount", 10);
    DmnDecisionTableResult dishDecision = decisionService.evaluateDecisionTableByKey("dish", variableMap);
    String dish = dishDecision.getSingleEntry();

    assertThat(dish, is(equalTo("Stew")));
  }


}
