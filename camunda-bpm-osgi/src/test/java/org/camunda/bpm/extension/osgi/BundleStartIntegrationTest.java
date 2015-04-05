package org.camunda.bpm.extension.osgi;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * Integration test to check that the camunda-engine and camunda-engine-osgi
 * bundles will start in the configured environment. This test also checks that
 * the expected services will be registered.
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BundleStartIntegrationTest extends OSGiTestCase {


  @Test
  public void bundleStarted() {
    try {
      Bundle bundle = startBundle("org.camunda.bpm");
      assertThat(bundle.getState(), is(equalTo(Bundle.ACTIVE)));
      Bundle bundle2 = startBundle("org.camunda.bpm.extension.osgi");
      assertThat(bundle2.getState(), is(equalTo(Bundle.ACTIVE)));
    } catch (BundleException be) {
      fail(be.toString());
    }
  }

}
