package org.camunda.bpm.extension.osgi.itest.blueprint;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.inject.Inject;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.osgi.blueprint.BlueprintELResolver;
import org.camunda.bpm.extension.osgi.itest.OSGiBlueprintTestEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BlueprintIntegrationTest extends OSGiBlueprintTestEnvironment {

  @Inject
  private BundleContext ctx;
  @Inject
  @Filter(timeout = 30000L)
  private ProcessEngine engine;
  @Inject
  @Filter(timeout = 30000L)
  private RepositoryService repositoryService;
  @Inject
  @Filter(timeout = 30000L)
  private RuntimeService runtimeService;
  @Inject
  @Filter(timeout = 30000L)
  private TaskService taskService;
  @Inject
  @Filter(timeout = 30000L)
  private IdentityService identityService;
  @Inject
  @Filter(timeout = 30000L)
  private FormService formService;
  @Inject
  @Filter(timeout = 30000L)
  private HistoryService historyService;
  @Inject
  @Filter(timeout = 30000L)
  private ManagementService managementService;
  /**
   * to make sure the {@link BlueprintELResolver} found the JavaDelegate
   */
  private static boolean delegateVisited = false;

  public static final String CAMUNDA_VERSION = "7.2.0";

  @Override
  @Configuration
  public Option[] createConfiguration() {
    return OptionUtils.combine(
        super.createConfiguration(),
        mavenBundle().groupId("org.camunda.bpm.extension.osgi").artifactId("camunda-bpm-blueprint-wrapper").versionAsInProject());
  }

  @Override
  protected InputStream createTestBundle() {
    try {
      return TinyBundles
        .bundle()
        .add(org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT + "testProcessSingleServiceTask.bpmn",
          new FileInputStream(new File("src/test/resources/testProcessSingleServiceTask.bpmn")))
        .add("OSGI-INF/blueprint/context.xml", new FileInputStream(new File("src/test/resources/blueprint/context.xml")))
        .set(Constants.BUNDLE_SYMBOLICNAME, "org.camunda.bpm.extension.osgi.example")
        .set(Constants.DYNAMICIMPORT_PACKAGE, "*")
        .build();
    } catch (FileNotFoundException fnfe) {
      fail(fnfe.toString());
      return null;
    }
  }

  @Test
  public void exportedServices() {
    assertThat(engine, is(notNullValue()));
    assertThat(formService, is(notNullValue()));
    assertThat(historyService, is(notNullValue()));
    assertThat(identityService, is(notNullValue()));
    assertThat(managementService, is(notNullValue()));
    assertThat(repositoryService, is(notNullValue()));
    assertThat(runtimeService, is(notNullValue()));
    assertThat(taskService, is(notNullValue()));
  }

  @Test(timeout = 35000L)
  public void exportJavaDelegate() throws InterruptedException {
    Hashtable<String, String> properties = new Hashtable<String, String>();
    properties.put("osgi.service.blueprint.compname", "testDelegate");
    ctx.registerService(JavaDelegate.class.getName(), new TestDelegate(), properties);
    // wait a little bit
    ProcessDefinition definition = null;
    do {
      Thread.sleep(500L);
      definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("Process_1").singleResult();
    } while (definition == null);
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(definition.getKey());
    assertThat(processInstance.isEnded(), is(true));
    assertThat(delegateVisited, is(true));
  }

  private static class TestDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
      delegateVisited = true;
    }
  }
}
