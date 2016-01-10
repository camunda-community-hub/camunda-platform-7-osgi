package org.camunda.bpm.extension.osgi.itest.fileinstall;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import org.apache.felix.fileinstall.ArtifactUrlTransformer;
import org.camunda.bpm.extension.osgi.fileinstall.impl.BpmnURLHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class BpmnDeploymentListenerIntegrationTest extends OSGiFileInstallTestEnvironment {

  @Inject
  private ArtifactUrlTransformer transformer;

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
