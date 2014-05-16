package org.camunda.bpm.extension.osgi.application.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.camunda.bpm.extension.osgi.OSGiTestCase;
import org.camunda.bpm.extension.osgi.application.TestBlueprintBundleLocalELResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BlueprintBundleLocalELResolverIntegrationTest extends OSGiTestCase {

  @Inject
  private TestBlueprintBundleLocalELResolver elResolver;

  @Configuration
  @Override
  public Option[] createConfiguration() {
    Option[] blueprintEnv = options(mavenBundle().groupId("org.assertj").artifactId("assertj-core").version("1.5.0"),
        mavenBundle().groupId("org.apache.aries.blueprint").artifactId("org.apache.aries.blueprint.core").version("1.0.0"),
        mavenBundle().groupId("org.apache.aries.proxy").artifactId("org.apache.aries.proxy").version("1.0.0"), mavenBundle().groupId("org.apache.aries")
            .artifactId("org.apache.aries.util").version("1.0.0"));
    return OptionUtils.combine(OptionUtils.combine(super.createConfiguration(), blueprintEnv));
  }

  @Test
  public void getValue() throws Exception {
    Object object = elResolver.getValue(null, null, "myBean");
    assertThat(object, is(notNullValue()));
  }

}
