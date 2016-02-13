package org.camunda.bpm.extension.osgi.itest.deploy;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.provision;

import java.io.InputStream;
import java.util.Hashtable;

import javax.sql.DataSource;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.camunda.bpm.extension.osgi.itest.OSGiTestEnvironment;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.osgi.framework.BundleException;

/**
 * Abstract superclass for the DeploymentListener-tests. This class creates the
 * in-memory {@link DataSource} and initializes the {@link ProcessEngine} for
 * the tests.
 * 
 * @author Ronny Bräunlich
 * 
 */
public abstract class AbstractDeploymentListenerIntegrationTest extends OSGiTestEnvironment {
  
	protected ProcessEngine processEngine;

	@Override
	@Configuration
	public Option[] createConfiguration() {
		Option[] basicConfiguration = super.createConfiguration();
		Option testBundle = provision(createTestBundleWithProcessDefinition());
		return OptionUtils.combine(basicConfiguration, testBundle);
	}

	@Before
	public void setUpAbstractDeploymentListenerTest() {
		createProcessEngine();
	}

	@After
	public void tearDownAbstractDeploymentListenerTest() {
		processEngine.close();
	}

	protected abstract InputStream createTestBundleWithProcessDefinition();

	@Test
	public void processDefinitionFound() throws InterruptedException {
		try {
			startBundle("org.camunda.bpm.osgi.example");
			// wait, so the engine can process the bpmn-file
			Thread.sleep(3000L);
			assertThat(
					processEngine.getRepositoryService()
							.createProcessDefinitionQuery()
							.processDefinitionKey("Process_1").singleResult(),
					is(notNullValue()));
		} catch (BundleException e) {
			fail(e.toString());
		}
	}

	private void createProcessEngine() {
		StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
		configuration.setDatabaseSchemaUpdate("create-drop").setDataSource(createDatasource()).setJobExecutorActivate(false);
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
}
