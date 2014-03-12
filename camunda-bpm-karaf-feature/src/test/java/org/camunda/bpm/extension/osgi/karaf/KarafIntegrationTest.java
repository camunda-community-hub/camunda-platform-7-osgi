package org.camunda.bpm.extension.osgi.karaf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

import java.io.File;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class KarafIntegrationTest {

	@Inject
	protected BundleContext ctx;

	@Configuration
	public Option[] config() {
		MavenArtifactUrlReference karafUrl = maven()
				.groupId("org.apache.karaf").artifactId("apache-karaf")
				.version("3.0.0").type("zip");
		MavenUrlReference karafStandardRepo = maven()
				.groupId("org.apache.karaf.features").artifactId("standard")
				.classifier("features").type("xml").version("3.0.0");
		return new Option[] {
				// logLevel(LogLevel.TRACE),
				// debugConfiguration("5005", true),
				karafDistributionConfiguration().frameworkUrl(karafUrl)
						.unpackDirectory(new File("target/exam"))
				// .useDeployFolder(false)
				,
				keepRuntimeFolder(),
				features(karafStandardRepo, "scr"),
//				features(
//						maven().groupId("org.camunda.bpm.osgi")
//								.artifactId("camunda-engine-karaf-feature")
//								.version("1.0.0-SNAPSHOT")
//								.classifier("features").type("xml"),
//						"camunda-engine-karaf-feature-minimal")
				features(new File("target/classes/features.xml").toURI().toString(), "camunda-engine-karaf-feature-minimal")
						};
	}

	@Test
	public void startCamundaOsgiBundle() throws BundleException {
		assertThat(ctx, is(notNullValue()));
		Bundle[] bundles = ctx.getBundles();
		boolean found = false;
		for (Bundle b : bundles) {
			if (b.getSymbolicName().equals("org.camunda.bpm.extension.osgi")) {
				b.start();
				found = true;
			}
		}
		if(!found){
			fail("Couldn't find bundle");
		}
	}

}
