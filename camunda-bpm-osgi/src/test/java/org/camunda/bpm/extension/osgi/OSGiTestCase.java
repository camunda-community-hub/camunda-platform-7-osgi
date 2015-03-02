package org.camunda.bpm.extension.osgi;

import static org.ops4j.pax.exam.CoreOptions.*;

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
 * <p/>
 * This class is also referenced as default configuration in<br/>
 * <code>
 * src/test/resources/META-INF/services/org.ops4j.pax.exam.ConfigurationFactory
 * </code>
 *
 * @author Ronny Bräunlich
 */
public class OSGiTestCase implements ConfigurationFactory {

  public static final String CAMUNDA_VERSION = "7.2.0";
  @Inject
  protected BundleContext ctx;

  @Override
  public Option[] createConfiguration() {
    Option[] camundaBundles = options(

      mavenBundle("org.camunda.bpm", "camunda-engine").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-bpmn-model").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-cmmn-model").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-xml-model").versionAsInProject(),

//      mavenBundle("org.camunda.commons", "camunda-commons-logging", "1.0.6"),
//      mavenBundle("org.camunda.commons", "camunda-commons-utils", "1.0.6"),
//
//      mavenBundle("org.camunda.spin", "camunda-spin-dataformat-all", "1.0.0"),
//      mavenBundle("net.minidev", "json-smart", "1.3"),
//      mavenBundle("org.codehaus.jackson", "jackson-core-asl", "1.9.13"),
//      mavenBundle("org.codehaus.jackson", "jackson-mapper-asl", "1.9.13"),
//
//      mavenBundle("org.camunda.connect", "camunda-connect-connectors-all", "1.0.0"),
//
      mavenBundle("joda-time", "joda-time").versionAsInProject(),
      mavenBundle("com.h2database", "h2").versionAsInProject(),
      mavenBundle("org.mybatis", "mybatis").versionAsInProject(),
      mavenBundle("com.fasterxml.uuid", "java-uuid-generator").versionAsInProject(),

//      mavenBundle("org.slf4j", "slf4j-api", "1.7.7"),
//      mavenBundle("ch.qos.logback", "logback-core", "1.1.2"),
//      mavenBundle("ch.qos.logback", "logback-classic", "1.1.2"),
//      mavenBundle("org.apache.logging.log4j", "log4j-api", "2.0-beta9"),
//      mavenBundle("org.apache.logging.log4j", "log4j-core", "2.0-beta9")
//        .noStart(),

      // make sure compiled classes from src/main are included
      bundle("reference:file:target/classes"));
    return OptionUtils.combine(
      camundaBundles,
      CoreOptions.junitBundles()
    );
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

  protected Bundle startBundle(String bundleSymbolicName) throws BundleException {
    Bundle bundle = getBundle(bundleSymbolicName);
    bundle.start();
    return bundle;
  }
}
