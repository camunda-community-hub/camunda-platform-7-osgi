package org.camunda.bpm.extension.osgi.itest.el;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import javax.sql.DataSource;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.extension.osgi.el.OSGiExpressionManager;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.camunda.bpm.extension.osgi.itest.OSGiTestEnvironment;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;

public abstract class AbstractOSGiELResolverIntegrationTest extends
		OSGiTestEnvironment {

	protected ProcessEngine processEngine;

	public AbstractOSGiELResolverIntegrationTest() {
		super();
	}

	@Override
	@Configuration
	public Option[] createConfiguration() {
	  Option[] beanUtils = options(
    mavenBundle().groupId("commons-beanutils")
        .artifactId("commons-beanutils").version("1.9.1"),
    mavenBundle().groupId("commons-collections")
        .artifactId("commons-collections").version("3.2.2"));
		return OptionUtils.combine(beanUtils, super.createConfiguration());
	}

	@Before
	public void setUpAbstractDeploymentListenerTest() {
		createProcessEngine();
		deployProcessDefinition();
	}

	@After
	public void tearDownAbstractDeploymentListenerTest() {
		processEngine.close();
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

	private void deployProcessDefinition() {
		File processDef = getProcessDefinition();
		DeploymentBuilder builder = processEngine.getRepositoryService()
				.createDeployment();
		builder.name(getClass().getName());
		try {
			builder.addInputStream(processDef.getName(),
					new FileInputStream(processDef)).deploy();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract File getProcessDefinition();

}
