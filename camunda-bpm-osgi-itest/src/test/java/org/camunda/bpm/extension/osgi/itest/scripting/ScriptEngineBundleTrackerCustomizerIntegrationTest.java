package org.camunda.bpm.extension.osgi.itest.scripting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactoryWithELResolver;
import org.camunda.bpm.extension.osgi.itest.OSGiTestEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ScriptEngineBundleTrackerCustomizerIntegrationTest extends OSGiTestEnvironment {

  @Configuration
  @Override
  public Option[] createConfiguration() {
    return OptionUtils.combine(super.createConfiguration(), CoreOptions.provision(createTestBundle()));
  }

  protected ProcessEngine processEngine;

  @Before
  public void setUp() {
    createProcessEngine();
  }

  @After
  public void tearDown() {
    processEngine.close();
  }

  private void createProcessEngine() {
    StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
    configuration.setDatabaseSchemaUpdate("create-drop").setDataSource(createDatasource()).setJobExecutorActivate(false);
    ProcessEngineFactory processEngineFactory = new ProcessEngineFactoryWithELResolver();
    processEngineFactory.setProcessEngineConfiguration(configuration);
    processEngineFactory.setBundle(getBundle("org.camunda.bpm.extension.osgi"));
    try {
      processEngineFactory.init();
      processEngine = processEngineFactory.getObject();
      ctx.registerService(ProcessEngine.class.getName(), processEngine, new Hashtable<String, String>());
    } catch (Exception e) {
      fail(e.toString());
    }
  }

  private InputStream createTestBundle() {
    try {
      return TinyBundles
          .bundle()
          .add("META-INF/services/javax.script.ScriptEngineFactory",
              new FileInputStream(new File("src/test/resources/scripting/javax.script.ScriptEngineFactory")))
          .add(TestScriptEngineFactory.class)
          .add(org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT + "testScriptProcess.bpmn",
              new FileInputStream(new File("src/test/resources/scripting/testScriptProcess.bpmn"))).set(Constants.DYNAMICIMPORT_PACKAGE, "*")
          .set(Constants.BUNDLE_SYMBOLICNAME, "org.camunda.bpm.osgi.example").build();
    } catch (FileNotFoundException fnfe) {
      fail(fnfe.toString());
      return null;
    }
  }

  @Test
  public void scriptEngineHasBeenRegistered() throws BundleException {
    ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByKey("Process_1");
    assertThat(instance.isEnded(), is(true));
  }

  /**
   * The Engine should throw a {@link ProcessEngineException} when trying to
   * execute the script because the bundle with the ScriptEngine has been
   * uninstalled.
   */
  @Test(expected = ProcessEngineException.class)
  public void scriptEngineHasUnregisteredAfterBundleUninstall() throws BundleException {
    Bundle testBundle = getBundle("org.camunda.bpm.osgi.example");
    testBundle.uninstall();
    processEngine.getRuntimeService().startProcessInstanceByKey("Process_1");
  }
}
