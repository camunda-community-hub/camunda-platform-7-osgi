package org.camunda.bpm.extension.osgi;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import org.apache.felix.fileinstall.ArtifactListener;
import org.apache.felix.fileinstall.ArtifactUrlTransformer;
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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

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
public class BundleStartWithFileinstallTest extends OSGiTestCase {

  @Override
  @Configuration
  public Option[] createConfiguration() {
    MavenArtifactProvisionOption felixFileinstall = mavenBundle().groupId("org.apache.felix.").artifactId("org.apache.felix.fileinstall").version("3.0.2");
    return OptionUtils.combine(super.createConfiguration(), felixFileinstall);
  }

  @Test
  public void checkServices() {
    try {
      startBundle("org.camunda.bpm.extension.osgi");
      ServiceReference[] services = ctx.getServiceReferences(ArtifactUrlTransformer.class.getName(), null);
      checkNumber(services);
      checkInstance(services);
      ServiceReference[] services2 = ctx.getServiceReferences(ArtifactListener.class.getName(), null);
      assertThat(services2.length, is(1));
      checkNumber(services2);
      checkInstance(services2);
    } catch (BundleException e) {
      fail(e.toString());
    } catch (InvalidSyntaxException e) {
      fail(e.toString());
    }
  }

  private void checkInstance(ServiceReference[] services) {
    for (ServiceReference ref : services) {
      Object service = ctx.getService(ref);
      assertThat(service.getClass().getName(), is("org.camunda.bpm.extension.osgi.url.bpmn.BpmnDeploymentListener"));
    }
  }

  private void checkNumber(ServiceReference[] services) {
    assertThat(services.length, is(1));
  }
}
