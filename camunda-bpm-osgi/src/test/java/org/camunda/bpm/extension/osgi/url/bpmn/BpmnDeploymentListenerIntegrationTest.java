package org.camunda.bpm.extension.osgi.url.bpmn;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import org.apache.felix.fileinstall.ArtifactUrlTransformer;
import org.camunda.bpm.extension.osgi.OSGiTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleException;

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
public class BpmnDeploymentListenerIntegrationTest extends OSGiTestCase {

  @Inject
  private ArtifactUrlTransformer transformer;

  @Override
  @Configuration
  public Option[] createConfiguration() {
    MavenArtifactProvisionOption felixFileinstall = mavenBundle().groupId("org.apache.felix.").artifactId("org.apache.felix.fileinstall").version("3.0.2");
    return OptionUtils.combine(super.createConfiguration(), felixFileinstall);
  }

  @Before
  public void setUp() throws BundleException {
    startBundle("org.camunda.bpm.extension.osgi");
  }

  /**
   * To make sure there isn't another {@link ArtifactUrlTransformer} registered
   * we check the injected class.
   */
  @Test
  public void checkCorrectClassInjected() {
    assertThat(transformer.getClass().getName(), is("org.camunda.bpm.extension.osgi.url.bpmn.BpmnDeploymentListener"));
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
