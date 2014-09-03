package org.camunda.bpm.extension.osgi;

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

/**
 * Superclass for OSGi-Integration tests. It contains a field for the
 * {@link BundleContext}, creates the basic Pax Exam configuration for the
 * environment and defines two helper methods.
 * <p>
 * This class is also referenced as default configuration in<br/>
 * <code>
 * src/test/resources/META-INF/services/org.ops4j.pax.exam.ConfigurationFactory
 * </code>
 *
 * @author Ronny Bräunlich
 *
 */
public class OSGiTestCase implements ConfigurationFactory {

	@Inject
	protected BundleContext ctx;

	@Override
	public Option[] createConfiguration() {
		Option[] camundaBundles = options(
				mavenBundle().groupId("org.camunda.bpm")
						.artifactId("camunda-engine").version("7.2.0-SNAPSHOT"),
				mavenBundle().groupId("org.camunda.bpm.model")
				 .artifactId("camunda-xml-model")
				 .version("7.2.0-SNAPSHOT"),
				mavenBundle().groupId("org.camunda.bpm.model")
				 .artifactId("camunda-bpmn-model")
				 .version("7.2.0-SNAPSHOT"),
	        mavenBundle().groupId("org.camunda.bpm.model")
	         .artifactId("camunda-cmmn-model")
	         .version("7.2.0-SNAPSHOT"),
	          mavenBundle().groupId("org.camunda.spin")
	           .artifactId("camunda-spin-all")
	           .version("1.0.0-SNAPSHOT"),
         mavenBundle().groupId("org.camunda.commons")
         .artifactId("camunda-commons-logging")
         .version("1.0.1"),
         mavenBundle().groupId("org.camunda.commons")
         .artifactId("camunda-commons-utils")
         .version("1.0.3"),
	        mavenBundle().groupId("org.camunda.connect")
	         .artifactId("camunda-connect-all")
	         .version("1.0.0-SNAPSHOT"),
	      mavenBundle().groupId("net.minidev").artifactId("json-smart")
	      .version("1.3"),
	      mavenBundle().groupId("org.codehaus.jackson").artifactId("jackson-core-asl")
	      .version("1.9.13"),
        mavenBundle().groupId("org.codehaus.jackson").artifactId("jackson-mapper-asl")
        .version("1.9.13"),
				mavenBundle().groupId("joda-time").artifactId("joda-time")
						.version("2.1"),
				mavenBundle().groupId("com.h2database").artifactId("h2")
						.version("1.2.143"),
				// FIXME this Mybatis version doesn't match camunda's
				mavenBundle().groupId("org.mybatis").artifactId("mybatis")
						.version("3.2.3"),
		    mavenBundle().groupId("com.fasterxml.uuid").artifactId("java-uuid-generator")
            .version("3.1.2"),
        mavenBundle().groupId("org.slf4j").artifactId("slf4j-api")
            .version("1.7.7"),
        mavenBundle().groupId("ch.qos.logback")
            .artifactId("logback-core").version("1.1.2"),
        mavenBundle().groupId("ch.qos.logback")
            .artifactId("logback-classic").version("1.1.2"),
				mavenBundle().groupId("org.apache.logging.log4j")
						.artifactId("log4j-api").version("2.0-beta9"),
				mavenBundle().groupId("org.apache.logging.log4j")
						.artifactId("log4j-core").version("2.0-beta9")
						.noStart(),
				// make sure compiled classes from src/main are included
				bundle("reference:file:target/classes"));
		return OptionUtils.combine(camundaBundles, CoreOptions.junitBundles());
	}

	protected Bundle getBundle(String bundleSymbolicName) {
		for (Bundle bundle : ctx.getBundles()) {
			if (bundle.getSymbolicName() != null
					&& bundle.getSymbolicName().equals(bundleSymbolicName)) {
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
