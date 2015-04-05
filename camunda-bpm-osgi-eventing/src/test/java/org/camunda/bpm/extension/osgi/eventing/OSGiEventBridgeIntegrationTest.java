package org.camunda.bpm.extension.osgi.eventing;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.extension.osgi.el.OSGiExpressionManager;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactoryWithELResolver;
import org.camunda.bpm.extension.osgi.eventing.api.OSGiEventBridgeActivator;
import org.camunda.bpm.extension.osgi.eventing.api.Topics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * @author Ronny Bräunlich
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiEventBridgeIntegrationTest {

  public static final String BUNDLE_SYMBOLIC_NAME = "org.camunda.bpm.extension.osgi.eventing";
  @Inject
  private BundleContext bundleContext;

  @Inject
  private LogReaderService logReaderService;

  @Inject
  @Filter(timeout = 30000L)
  private OSGiEventBridgeActivator eventBridgeActivator;

  @Configuration
  public Option[] createConfiguration() {
    Option[] camundaBundles = options(
      mavenBundle("org.camunda.bpm", "camunda-engine").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-bpmn-model").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-cmmn-model").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-xml-model").versionAsInProject(),

      mavenBundle("joda-time", "joda-time").versionAsInProject(),
      mavenBundle("com.h2database", "h2").versionAsInProject(),
      mavenBundle("org.mybatis", "mybatis").versionAsInProject(),
      mavenBundle("com.fasterxml.uuid", "java-uuid-generator").versionAsInProject(),


      mavenBundle("org.camunda.bpm.extension.osgi", "camunda-bpm-osgi")
        .versionAsInProject(),
      mavenBundle("org.camunda.bpm.extension.osgi", "camunda-bpm-osgi-eventing-api")
        .versionAsInProject(),
      mavenBundle("org.apache.felix", "org.apache.felix.eventadmin")
        .versionAsInProject(),
      mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager")
        .versionAsInProject(),
      mavenBundle("org.apache.felix", "org.apache.felix.log")
        .version("1.0.1"),
      // make sure compiled classes from src/main are included
      bundle("reference:file:target/classes"));
    return OptionUtils.combine(camundaBundles, CoreOptions.junitBundles());
  }

  @Test
  public void shouldRegisterService() {
    assertThat(eventBridgeActivator, is(notNullValue()));
  }

  @Test
  public void testEventBrigde() throws FileNotFoundException {
    // we have to use a LogListener to find Errors in the log
    ErrorLogListener logListener = new ErrorLogListener();
    logReaderService.addLogListener(logListener);
    TestEventHandler eventHandler = new TestEventHandler();
    registerEventHandler(eventHandler);
    ProcessEngine processEngine = createProcessEngine();
    DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
    deploymentBuilder.name("testProcess").addInputStream("testProcess.bpmn", new FileInputStream(new File(
      "src/test/resources/testProcess.bpmn"))).deploy();
    processEngine.getRuntimeService().startProcessInstanceByKey("Process_1");
    processEngine.close();
    if (logListener.getErrorMessage() != null) {
      fail(logListener.getErrorMessage());
    }
    assertThat(eventHandler.isCalled(), is(true));
  }

  /**
   * We don't want to receive any more events after shutting the bundle down.
   */
  @Test
  public void shutdownDuringRunningProcess() throws FileNotFoundException, BundleException, InterruptedException {
    // we have to use a LogListener to find Errors in the log
    ErrorLogListener logListener = new ErrorLogListener();
    logReaderService.addLogListener(logListener);
    TestEventHandler eventHandler = new TestEventHandler();
    registerEventHandler(eventHandler);
    final ProcessEngine processEngine = createProcessEngine();
    DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
    deploymentBuilder.name("longRunningTestProcess").addInputStream("longRunningTestProcess.bpmn", new FileInputStream(new File(
      "src/test/resources/longRunningTestProcess.bpmn"))).deploy();
    stopEventingBundle();
    processEngine.getRuntimeService().startProcessInstanceByKey("slowProcess");
    processEngine.close();
    if (logListener.getErrorMessage() != null) {
      fail(logListener.getErrorMessage());
    }
    assertThat(eventHandler.endCalled(), is(false));
  }

  private void stopEventingBundle() throws BundleException {
    for (Bundle bundle : bundleContext.getBundles()) {
      if (bundle.getSymbolicName().equals(BUNDLE_SYMBOLIC_NAME)) {
        bundle.stop();
        break;
      }
    }
  }

  @Test
  public void restartBundleAfterShutdown() throws FileNotFoundException, BundleException, InterruptedException {
    // we have to use a LogListener to find Errors in the log
    ErrorLogListener logListener = new ErrorLogListener();
    logReaderService.addLogListener(logListener);
    final ProcessEngine processEngine = createProcessEngine();
    DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
    deploymentBuilder.name("longRunningTestProcess").addInputStream("longRunningTestProcess.bpmn", new FileInputStream(new File(
      "src/test/resources/longRunningTestProcess.bpmn"))).deploy();
    stopEventingBundle();
    processEngine.getRuntimeService().startProcessInstanceByKey("slowProcess");
    TestEventHandler eventHandler = new TestEventHandler();
    registerEventHandler(eventHandler);
    startEventingBundle();
    processEngine.getRuntimeService().startProcessInstanceByKey("slowProcess");
    if (logListener.getErrorMessage() != null) {
      fail(logListener.getErrorMessage());
    }
    processEngine.close();
    assertThat(eventHandler.endCalled(), is(true));
  }

  private void startEventingBundle() throws BundleException {
    for (Bundle bundle : bundleContext.getBundles()) {
      if (bundle.getSymbolicName().equals(BUNDLE_SYMBOLIC_NAME)) {
        bundle.start();
        break;
      }
    }
  }

  private ProcessEngine createProcessEngine() {
    StandaloneInMemProcessEngineConfiguration configuration = new StandaloneInMemProcessEngineConfiguration();
    configuration.setCustomPreBPMNParseListeners(Collections.<BpmnParseListener>singletonList(eventBridgeActivator));
    configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
    ProcessEngineFactoryWithELResolver engineFactory = new ProcessEngineFactoryWithELResolver();
    engineFactory.setProcessEngineConfiguration(configuration);
    engineFactory.setBundle(bundleContext.getBundle());
    engineFactory.setExpressionManager(new OSGiExpressionManager());
    engineFactory.init();
    return engineFactory.getObject();
  }

  private void registerEventHandler(TestEventHandler eventHandler) {
    Dictionary<String, String> props = new Hashtable<String, String>();
    props.put(EventConstants.EVENT_TOPIC, Topics.EXECUTION_EVENT_TOPIC);
    bundleContext.registerService(EventHandler.class.getName(), eventHandler, props);
  }

  /**
   * If an exception happens during event distribution the EventAdmin will log this
   * with OSGi logging if present, so we need a listener to make sure no exceptions
   * were thrown.
   */
  private static class ErrorLogListener implements LogListener {
    private String errorMessage;

    @Override
    public void logged(LogEntry entry) {
      if (entry.getMessage().contains("EventAdmin") && entry.getException() != null) {
        this.errorMessage = entry.getMessage();
        entry.getException().printStackTrace();
      }
    }

    public String getErrorMessage() {
      return errorMessage;
    }
  }

  ;
}
