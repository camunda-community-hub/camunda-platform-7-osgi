package org.camunda.bpm.extension.osgi.karaf;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFileExtend;
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
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class KarafFullFeatureIntegrationTest {

  @Inject
  protected BundleContext ctx;
  
  @Configuration
  public Option[] config() {
    MavenArtifactUrlReference karafUrl =
      maven()
        .groupId("org.apache.karaf")
        .artifactId("apache-karaf")
        .type("zip")
        .versionAsInProject();
    return new Option[] {
        karafDistributionConfiguration().frameworkUrl(karafUrl)
          .unpackDirectory(new File("target/exam")),
        // We use this option to allow the container to use artifacts found in private / local repo.
        editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg",
          "org.ops4j.pax.url.mvn.repositories",
          "file:${maven.repo.local}@id=mavenlocalrepo@snapshots"),
        editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg",
          "org.ops4j.pax.url.mvn.localRepository",
          "${maven.repo.local}"),
        editConfigurationFileExtend("etc/system.properties",
          "maven.repo.local",
          System.getProperty("maven.repo.local", "")), keepRuntimeFolder(),
        features(new File("target/classes/features.xml").toURI().toString(),
          "camunda-bpm-karaf-feature-full") };
  }

  @Test
  public void startCamundaOsgiBundle() throws BundleException {
    assertThat(ctx, is(notNullValue()));
    Bundle[] bundles = ctx.getBundles();
    boolean found = false;
    for (Bundle b : bundles) {
      assertThat("Bundle " + b.getSymbolicName() + " is in wrong state.",b.getState(), is(either(equalTo(Bundle.RESOLVED)).or(equalTo(Bundle.ACTIVE)).or(equalTo(Bundle.STARTING))));
      if (b.getSymbolicName().equals("org.camunda.bpm.extension.osgi")) {
        b.start();
        found = true;
      }
    }
    if (!found) {
      fail("Couldn't find bundle");
    }
  }
}
