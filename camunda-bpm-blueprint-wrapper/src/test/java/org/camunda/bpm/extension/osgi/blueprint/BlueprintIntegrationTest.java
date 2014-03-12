package org.camunda.bpm.extension.osgi.blueprint;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

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
import org.camunda.bpm.engine.osgi.blueprint.BlueprintELResolver;
import org.camunda.bpm.engine.runtime.ProcessInstance;
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
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BlueprintIntegrationTest {

	@Inject
	private BundleContext ctx;
	@Inject
	private ProcessEngine engine;
	@Inject
	private RepositoryService repoService;
	@Inject
	private RuntimeService runService;
	@Inject
	private TaskService taskService;
	@Inject
	private IdentityService identSerivce;
	@Inject
	private FormService formService;
	@Inject
	private HistoryService histService;
	@Inject
	private ManagementService manaSerivce;
	/**
	 * to make sure the {@link BlueprintELResolver} found the JavaDelegate
	 */
	private static boolean delegateVisited = false;

	@Configuration
	public Option[] createConfiguration() {
		Option[] camundaBundles = options(
				mavenBundle().groupId("org.camunda.bpm")
						.artifactId("camunda-engine").version("7.1.0-SNAPSHOT"),				 
				mavenBundle().groupId("org.camunda.bpm.model")
						 .artifactId("camunda-xml-model")
						 .version("7.1.0-SNAPSHOT"),
				mavenBundle().groupId("org.camunda.bpm.model")
						 .artifactId("camunda-bpmn-model")
						 .version("7.1.0-SNAPSHOT"),
				mavenBundle().groupId("org.camunda.bpm.extension.osgi")
						.artifactId("camunda-bpm-osgi")
						.version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("joda-time").artifactId("joda-time")
						.version("2.1"),
				mavenBundle().groupId("com.h2database").artifactId("h2")
						.version("1.2.143"),
				// FIXME this Mybatis version doesn't match camunda's
				mavenBundle().groupId("org.mybatis").artifactId("mybatis")
						.version("3.2.3"),
				mavenBundle().groupId("org.apache.logging.log4j")
						.artifactId("log4j-api").version("2.0-beta9"),
				mavenBundle().groupId("org.apache.logging.log4j")
						.artifactId("log4j-core").version("2.0-beta9")
						.noStart(),
				mavenBundle().groupId("org.assertj")
						.artifactId("assertj-core").version("1.5.0"),
				mavenBundle().groupId("org.apache.aries.blueprint")
						.artifactId("org.apache.aries.blueprint.core")
						.version("1.0.0"),
				mavenBundle().groupId("org.apache.aries.proxy")
						.artifactId("org.apache.aries.proxy").version("1.0.0"),
				mavenBundle().groupId("org.apache.aries")
						.artifactId("org.apache.aries.util").version("1.0.0"),
				// make sure compiled classes from src/main are included
				bundle("reference:file:target/classes"));
		return OptionUtils.combine(camundaBundles, CoreOptions.junitBundles(),
				provision(createTestBundleWithProcessDefinition()));
	}

	private InputStream createTestBundleWithProcessDefinition() {
		try {
			return TinyBundles
					.bundle()
					.add(org.camunda.bpm.engine.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT
							+ "testProcess.bpmn",
							new FileInputStream(new File(
									"src/test/resources/testProcess.bpmn")))
					.set(Constants.BUNDLE_SYMBOLICNAME,
							"org.camunda.bpm.extension.osgi.example").build();
		} catch (FileNotFoundException fnfe) {
			fail(fnfe.toString());
			return null;
		}
	}

	@Test
	public void exportedServices() {
		assertThat(engine, is(notNullValue()));
		assertThat(formService, is(notNullValue()));
		assertThat(histService, is(notNullValue()));
		assertThat(identSerivce, is(notNullValue()));
		assertThat(manaSerivce, is(notNullValue()));
		assertThat(repoService, is(notNullValue()));
		assertThat(runService, is(notNullValue()));
		assertThat(taskService, is(notNullValue()));
	}

	@Test
	public void exportJavaDelegate() throws InterruptedException {
		Properties properties = new Properties();
		properties.setProperty("osgi.service.blueprint.compname",
				"testDelegate");
		ctx.registerService(JavaDelegate.class.getName(), new TestDelegate(),
				properties);
		//wait a little bit
		Thread.sleep(2000L);
		ProcessInstance processInstance = runService.startProcessInstanceByKey("Process_1");
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
