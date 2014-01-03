package org.camunda.bpm.engine.osgi;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BundleStartTest {

	@Inject
	private BundleContext ctx;

	@Configuration
	public static Option[] configuration() throws FileNotFoundException {
		Option[] camundaBundles = options(
				mavenBundle().groupId("org.camunda.bpm")
						.artifactId("camunda-engine").version("7.1.0-alpha1"),
				mavenBundle().groupId("joda-time").artifactId("joda-time")
						.version("2.1"),
				mavenBundle().groupId("com.h2database").artifactId("h2")
						.version("1.2.143"),
				mavenBundle().groupId("org.mybatis").artifactId("mybatis")
						.version("3.1.0"),
				//make sure compiled classes from src/main are included
				bundle("reference:file:target/classes"));
		return OptionUtils.combine(camundaBundles, CoreOptions.junitBundles());
	}

	@Test
	public void bundleStarted() {
		try {
			Bundle bundle = getBundle("org.camunda.bpm");
			bundle.start();
			assertThat(bundle.getState(), is(equalTo(Bundle.ACTIVE)));
			Bundle bundle2 = getBundle("org.camunda.bpm.osgi.camunda-engine-osgi");
			bundle2.start();
			assertThat(bundle2.getState(), is(equalTo(Bundle.ACTIVE)));
		} catch (BundleException be) {
			fail(be.toString());
		}
	}

	private Bundle getBundle(String bundleSymbolicName) {
		for (Bundle bundle : ctx.getBundles()) {
			System.out.println("Checking bundle: " + bundle);
			if (bundle.getSymbolicName().equals(bundleSymbolicName)) {
				return bundle;
			}
		}
		return null;
	}

}
