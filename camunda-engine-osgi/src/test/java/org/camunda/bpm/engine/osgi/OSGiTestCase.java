package org.camunda.bpm.engine.osgi;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.ops4j.pax.exam.ConfigurationFactory;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class OSGiTestCase implements ConfigurationFactory {

	@Inject
	protected BundleContext ctx;

	@Override
	public Option[] createConfiguration() {
		Option[] camundaBundles = options(
				mavenBundle().groupId("org.camunda.bpm")
						.artifactId("camunda-engine").version("7.1.0-alpha1"),
				mavenBundle().groupId("joda-time").artifactId("joda-time")
						.version("2.1"),
				mavenBundle().groupId("com.h2database").artifactId("h2")
						.version("1.2.143"),
				mavenBundle().groupId("org.mybatis").artifactId("mybatis")
						.version("3.1.0"),
				// make sure compiled classes from src/main are included
				bundle("reference:file:target/classes"));
		return OptionUtils.combine(camundaBundles, CoreOptions.junitBundles());
	}

	protected Bundle getBundle(String bundleSymbolicName) {
		for (Bundle bundle : ctx.getBundles()) {
			System.out.println("Checking bundle: " + bundle);
			if (bundle.getSymbolicName().equals(bundleSymbolicName)) {
				return bundle;
			}
		}
		return null;
	}

	protected Bundle startBundle(String bundleSymbolicName)
			throws BundleException {
		Bundle bundle = getBundle(bundleSymbolicName);
		bundle.start();
		return bundle;
	}
}
