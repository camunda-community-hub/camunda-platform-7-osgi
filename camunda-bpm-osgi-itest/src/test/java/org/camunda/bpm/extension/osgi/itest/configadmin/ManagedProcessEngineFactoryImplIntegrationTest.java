package org.camunda.bpm.extension.osgi.itest.configadmin;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.Hashtable;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.configadmin.ManagedProcessEngineFactory;
import org.camunda.bpm.extension.osgi.itest.OSGiTestEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedServiceFactory;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ManagedProcessEngineFactoryImplIntegrationTest extends OSGiTestEnvironment {
  
  public static final String CAMUNDA_VERSION = "7.2.0";
  
  @Inject
  @Filter(value="(service.pid=" + ManagedProcessEngineFactory.SERVICE_PID + ")", timeout=20000L)
  private ManagedServiceFactory serviceFactory;
  @Inject
  private BundleContext ctx;
  @Inject
  private ConfigurationAdmin configAdmin;

  @Override
  @Configuration
  public Option[] createConfiguration() {
    Option[] bundles = options(
        mavenBundle().groupId("commons-beanutils").artifactId("commons-beanutils").versionAsInProject(),
        mavenBundle().groupId("commons-collections").artifactId("commons-collections").versionAsInProject(),
        mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.configadmin").versionAsInProject(),
        mavenBundle().groupId("org.camunda.bpm.extension.osgi").artifactId("camunda-bpm-osgi-configadmin").versionAsInProject());
    return OptionUtils.combine(OptionUtils.combine(bundles, super.createConfiguration()));
  }

  @Test
  public void serviceGotRegistered() throws BundleException {
    assertThat(serviceFactory, is(notNullValue()));
  }

  @Test(timeout = 30000L)
  public void createProcessEngine() throws IOException, InterruptedException {
    Hashtable<String, Object> props = new Hashtable<String, Object>();
    props.put("databaseSchemaUpdate", ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP);
    props.put("jdbcUrl", "jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1");
    props.put("jobExecutorActivate", "true");
    props.put("processEngineName", "TestEngine");
    org.osgi.service.cm.Configuration config = configAdmin.createFactoryConfiguration(ManagedProcessEngineFactory.SERVICE_PID, null);
    config.update(props);
    ServiceReference<ProcessEngine> reference = null;
    do {
      Thread.sleep(500L);
      reference = ctx.getServiceReference(ProcessEngine.class);
    } while (reference == null);
    ProcessEngine engine = ctx.getService(reference);
    assertThat(engine, is(notNullValue()));
    assertThat(engine.getName(), is("TestEngine"));
  }
  
  @Test(timeout = 40000L)
  public void shutdownProcessEngine() throws IOException, InterruptedException {
    Hashtable<String, Object> props = new Hashtable<String, Object>();
    props.put("databaseSchemaUpdate", ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP);
    props.put("jdbcUrl", "jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1");
    props.put("jobExecutorActivate", "true");
    props.put("processEngineName", "TestEngine");
    org.osgi.service.cm.Configuration config = configAdmin.createFactoryConfiguration(ManagedProcessEngineFactory.SERVICE_PID, null);
    config.update(props);
    //give the engine some time to be created
    Thread.sleep(10000L);
    config.delete();
    Thread.sleep(5000L);
    ServiceReference<ProcessEngine> reference = null;
    do {
      Thread.sleep(500L);
      reference = ctx.getServiceReference(ProcessEngine.class);
    } while (reference != null);
  }
}
