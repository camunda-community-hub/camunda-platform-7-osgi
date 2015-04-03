package org.camunda.bpm.extension.osgi.fileinstall.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import org.apache.felix.fileinstall.ArtifactUrlTransformer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

/**
 * Testclass to check that the BpmnDeploymentListener works in the OSGi
 * environment. The test is performed by testing the tranform() method. To be
 * able to tranform the URL the {@link BpmnURLHandler} is needed.
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BpmnDeploymentListenerIntegrationTest {

  @Inject
  private ArtifactUrlTransformer transformer;

  @Configuration
  public Option[] createConfiguration() {
    Option[] bundles = options(
        mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.fileinstall").versionAsInProject(),
        mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager").versionAsInProject(),
        bundle("reference:file:target/classes"));
    return OptionUtils.combine(bundles, CoreOptions.junitBundles());
  }

  /**
   * To make sure there isn't another {@link ArtifactUrlTransformer} registered
   * we check the injected class.
   */
  @Test
  public void checkCorrectClassInjected() {
    assertThat(transformer.getClass().getName(), is("org.camunda.bpm.extension.osgi.fileinstall.impl.BpmnDeploymentListener"));
  }

  @Test
  public void transform() throws Exception {
    File file = new File("src/test/resources/testprocess.bpmn");
    URL url = transformer.transform(file.toURI().toURL());
    assertThat(url.toString(), is(equalTo("bpmn:" + file.toURI().toURL())));
  }

  @Test
  public void transformNull() throws Exception {
    URL url = transformer.transform(null);
    assertThat(url, is(nullValue()));
  }
}
