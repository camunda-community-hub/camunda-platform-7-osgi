package org.camunda.bpm.extension.osgi.itest.fileinstall;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.apache.felix.fileinstall.ArtifactListener;
import org.apache.felix.fileinstall.ArtifactUrlTransformer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.URLStreamHandlerService;

/**
 * Integration test to check that the camunda-engine-osgi bundle will start in
 * the configured environment. Additionally this class adds the Apache Felix
 * Fileinstall bundle to check that the optional {@link ArtifactUrlTransformer}
 * and {@link ArtifactListener} services are registered after startup.
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BundleStartWithFileInstallIntegrationTest extends OSGiFileInstallTestEnvironment{

  @Inject
  protected BundleContext ctx;
  
  @Inject
  @Filter("(url.handler.protocol=bpmn)")
  private URLStreamHandlerService bpmnUrlHandler;
  
  @Inject
  private ArtifactUrlTransformer transformer;
  
  @Inject
  private ArtifactListener listener;

  @Test
  public void checkServices() {  
    assertThat(bpmnUrlHandler, is(notNullValue()));
    assertThat(transformer, is(notNullValue()));
    assertThat(listener, is(notNullValue()));
    assertThat(bpmnUrlHandler.getClass().getName(), is("org.camunda.bpm.extension.osgi.fileinstall.impl.BpmnURLHandler"));
    assertThat(transformer.getClass().getName(), is("org.camunda.bpm.extension.osgi.fileinstall.impl.BpmnDeploymentListener"));
    assertThat(listener.getClass().getName(), is("org.camunda.bpm.extension.osgi.fileinstall.impl.BpmnDeploymentListener"));
  }
  
}
