package org.camunda.bpm.extension.osgi.itest.fileinstall;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import org.camunda.bpm.extension.osgi.itest.OSGiTestEnvironment;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;

public class OSGiFileInstallTestEnvironment extends OSGiTestEnvironment {

  @Configuration
  @Override
  public Option[] createConfiguration() {
    return OptionUtils.combine(
          super.createConfiguration(), 
          mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.fileinstall").versionAsInProject(),
          mavenBundle("org.camunda.bpm.extension.osgi", "camunda-bpm-osgi-fileinstall").versionAsInProject()
        );
  }
}
