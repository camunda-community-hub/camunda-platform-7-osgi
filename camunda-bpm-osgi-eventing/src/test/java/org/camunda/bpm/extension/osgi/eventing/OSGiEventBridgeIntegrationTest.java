package org.camunda.bpm.extension.osgi.eventing;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.extension.osgi.el.OSGiExpressionManager;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactoryWithELResolver;
import org.camunda.bpm.extension.osgi.eventing.api.OSGiEventBridgeActivator;
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
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * @author Ronny Bräunlich
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OSGiEventBridgeIntegrationTest {

  @Inject
  private BundleContext bundleContext;

  @Inject
  private EventAdmin eventAdmin;

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
    TestEventHandler eventHandler = new TestEventHandler();
    registerEventHandler(eventHandler);
    ProcessEngine processEngine = createProcessEngine();
    DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
    deploymentBuilder.name("testProcess").addInputStream("testProcess.bpmn", new FileInputStream(new File(
      "src/test/resources/testProcess.bpmn"))).deploy();
    processEngine.getRuntimeService().startProcessInstanceByKey("Process_1");
    assertThat(eventHandler.isCalled(), is(true));
  }

  private ProcessEngine createProcessEngine() {
    StandaloneInMemProcessEngineConfiguration configuration = new StandaloneInMemProcessEngineConfiguration();
    configuration.setCustomPreBPMNParseListeners(Collections.<BpmnParseListener>singletonList(eventBridgeActivator));
    ProcessEngineFactoryWithELResolver engineFactory = new ProcessEngineFactoryWithELResolver();
    engineFactory.setProcessEngineConfiguration(configuration);
    engineFactory.setBundle(bundleContext.getBundle());
    engineFactory.setExpressionManager(new OSGiExpressionManager());
    engineFactory.init();
    return engineFactory.getObject();
  }

  private void registerEventHandler(TestEventHandler eventHandler) {
    Dictionary props = new Hashtable();
    props.put(EventConstants.EVENT_TOPIC, Execution.class.getName().replace('.', '/'));
    bundleContext.registerService(EventHandler.class.getName(), eventHandler, props);
  }
}
