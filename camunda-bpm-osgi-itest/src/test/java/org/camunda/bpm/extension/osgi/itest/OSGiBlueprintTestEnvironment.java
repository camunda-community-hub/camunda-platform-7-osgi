package org.camunda.bpm.extension.osgi.itest;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import java.io.InputStream;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;

public abstract class OSGiBlueprintTestEnvironment extends OSGiTestEnvironment {
  
  @Configuration
  @Override
  public Option[] createConfiguration() {
    Option[] blueprintEnv = options(
        mavenBundle().groupId("org.assertj").artifactId("assertj-core").versionAsInProject(),
        mavenBundle().groupId("org.apache.aries.blueprint").artifactId("org.apache.aries.blueprint.core").versionAsInProject(),
        mavenBundle().groupId("org.apache.aries.proxy").artifactId("org.apache.aries.proxy").versionAsInProject(), 
        mavenBundle().groupId("org.apache.aries").artifactId("org.apache.aries.util").versionAsInProject(),
        mavenBundle().groupId("org.camunda.bpm.extension.osgi").artifactId("camunda-bpm-osgi-processapplication").versionAsInProject());
    Option testBundle = provision(createTestBundle());
    return OptionUtils.combine(OptionUtils.combine(super.createConfiguration(), blueprintEnv), testBundle);
  }

  protected abstract InputStream createTestBundle();
}
