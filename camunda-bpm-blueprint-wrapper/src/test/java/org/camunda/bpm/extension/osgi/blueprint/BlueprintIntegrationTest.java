package org.camunda.bpm.extension.osgi.blueprint;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
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
public class BlueprintIntegrationTest {

  @Inject
  private BundleContext ctx;
  @Inject
  @Filter(timeout = 20000L)
  private ProcessEngine engine;
  @Inject
  @Filter(timeout = 20000L)
  private RepositoryService repositoryService;
  @Inject
  @Filter(timeout = 20000L)
  private RuntimeService runtimeService;
  @Inject
  @Filter(timeout = 20000L)
  private TaskService taskService;
  @Inject
  @Filter(timeout = 20000L)
  private IdentityService identityService;
  @Inject
  @Filter(timeout = 20000L)
  private FormService formService;
  @Inject
  @Filter(timeout = 20000L)
  private HistoryService historyService;
  @Inject
  @Filter(timeout = 20000L)
  private ManagementService managementService;
  /**
   * to make sure the {@link BlueprintELResolver} found the JavaDelegate
   */
  private static boolean delegateVisited = false;

  public static final String CAMUNDA_VERSION = "7.2.0";

  @Configuration
  public Option[] createConfiguration() {
    Option[] camundaBundles = options(
      mavenBundle("org.camunda.bpm", "camunda-engine", CAMUNDA_VERSION),
      mavenBundle("org.camunda.bpm.model", "camunda-bpmn-model", CAMUNDA_VERSION),
      mavenBundle("org.camunda.bpm.model", "camunda-cmmn-model", CAMUNDA_VERSION),
      mavenBundle("org.camunda.bpm.model", "camunda-xml-model", CAMUNDA_VERSION),
      mavenBundle("org.camunda.commons", "camunda-commons-logging", "1.0.6"),
      mavenBundle("org.camunda.commons", "camunda-commons-utils", "1.0.6"),

      mavenBundle("joda-time", "joda-time", "2.1"),
      mavenBundle("com.h2database", "h2", "1.3.168"),
      mavenBundle("org.mybatis", "mybatis", "3.2.8"),

      mavenBundle("org.apache.logging.log4j", "log4j-api", "2.0-beta9"),
      mavenBundle("org.apache.logging.log4j", "log4j-core", "2.0-beta9").
        noStart(),

      mavenBundle("org.camunda.bpm.extension.osgi", "camunda-bpm-osgi", "1.1.0-SNAPSHOT"),

      mavenBundle("org.slf4j", "slf4j-api", "1.7.7"),
      mavenBundle("ch.qos.logback", "logback-core", "1.1.2"),
      mavenBundle("ch.qos.logback", "logback-classic", "1.1.2"),
      mavenBundle("org.assertj", "assertj-core", "1.5.0"),
      mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.core", "1.0.0"),
      mavenBundle("org.apache.aries.proxy", "org.apache.aries.proxy", "1.0.0"),
      mavenBundle("org.apache.aries", "org.apache.aries.util", "1.0.0"),

      // make sure compiled classes from src/main are included
      bundle("reference:file:target/classes"));

    return OptionUtils.combine(
      camundaBundles,
      CoreOptions.junitBundles(),
      provision(createTestBundleWithProcessDefinition())
    );
  }

  private InputStream createTestBundleWithProcessDefinition() {
    try {
      return TinyBundles
          .bundle()
          .add(org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT + "testProcess.bpmn",
              new FileInputStream(new File("src/test/resources/testProcess.bpmn")))
          .set(Constants.BUNDLE_SYMBOLICNAME, "org.camunda.bpm.extension.osgi.example")
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

  @Test
  public void exportJavaDelegate() throws InterruptedException {
    Properties properties = new Properties();
    properties.setProperty("osgi.service.blueprint.compname", "testDelegate");
    ctx.registerService(JavaDelegate.class.getName(), new TestDelegate(), properties);
    // wait a little bit
    Thread.sleep(3000L);
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1");
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
