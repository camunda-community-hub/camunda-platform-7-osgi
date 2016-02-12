package org.camunda.bpm.extension.osgi.itest;

import static org.ops4j.pax.exam.CoreOptions.*;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.ops4j.pax.exam.ConfigurationFactory;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * This class sets up the common environment for all integration tests.
 * <p/>
 * This class is also referenced as default configuration in<br/>
 * <code>
 * src/test/resources/META-INF/services/org.ops4j.pax.exam.ConfigurationFactory
 * </code>
 *
 * @author Ronny Bräunlich
 */
public class OSGiTestEnvironment implements ConfigurationFactory {

  @Inject
  protected BundleContext ctx;
  
  @Override
  public Option[] createConfiguration() {
    Option[] camundaBundles = options(

      // camunda core
      mavenBundle("org.camunda.bpm", "camunda-engine").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-bpmn-model").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-cmmn-model").versionAsInProject(),
      mavenBundle("org.camunda.bpm.model", "camunda-xml-model").versionAsInProject(),
      // camunda core dependencies
      mavenBundle("joda-time", "joda-time").versionAsInProject(),
      mavenBundle("com.h2database", "h2").versionAsInProject(),
      mavenBundle("org.mybatis", "mybatis").versionAsInProject(),
      mavenBundle("com.fasterxml.uuid", "java-uuid-generator").versionAsInProject(),
      
      //camunda osgi
      mavenBundle("org.camunda.bpm.extension.osgi", "camunda-bpm-osgi").versionAsInProject(),
      
      //camunda osgi dependencies
      mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager").versionAsInProject(),

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
  
  /**
   * Creates a h2 in memory datasource for the tests.
   */
  public DataSource createDatasource(){
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    dataSource.setUser("sa");
    dataSource.setPassword("");
    return dataSource;
  }
}
