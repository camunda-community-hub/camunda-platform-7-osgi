package org.camunda.bpm.extension.osgi.el;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.provision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.osgi.OSGiTestCase;
import org.camunda.bpm.extension.osgi.TestActivityBehaviour;
import org.camunda.bpm.extension.osgi.blueprint.ProcessEngineFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Constants;

/**
 * Integration test to check if the OSGiELResolver finds a ActivityBehavior via its
 * class name.
 * 
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
@Ignore(value="Until CAM-1481 got fixed")
public class OSGiELResolverBehaviorIntegrationTest extends OSGiTestCase {

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

	protected InputStream createTestBundleWithProcessDefinition() {
		try {
			return TinyBundles
					.bundle()
					.add(org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT
							+ "testprocess.bpmn",
							new FileInputStream(
									new File(
											"src/test/resources/org/camunda/bpm/extension/osgi/el/behaviortestprocess.bpmn")))
					.set(Constants.BUNDLE_SYMBOLICNAME,
							"org.camunda.bpm.osgi.example").build();
		} catch (FileNotFoundException fnfe) {
			fail(fnfe.toString());
			return null;
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
					new Properties());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	private DataSource createDatasource() {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
		dataSource.setUser("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	@Test
	public void runProcess() throws Exception {
		startBundle("org.camunda.bpm.osgi.example");
		// wait, so the engine can process the bpmn-file
		Thread.sleep(3000L);
		TestActivityBehaviour behaviour = new TestActivityBehaviour();
		ctx.registerService(ActivityBehavior.class.getName(), behaviour, null);
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey("delegate");
		assertThat(behaviour.getCalled(), is(true));
		assertThat(processInstance.isEnded(), is(true));
	}
}
