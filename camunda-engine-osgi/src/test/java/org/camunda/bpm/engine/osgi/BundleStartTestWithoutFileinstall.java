package org.camunda.bpm.engine.osgi;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.url.URLStreamHandlerService;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BundleStartTestWithoutFileinstall extends OSGiTestCase {

	@Test
	public void bundleStarted() {
		try {
			Bundle bundle = startBundle("org.camunda.bpm");
			assertThat(bundle.getState(), is(equalTo(Bundle.ACTIVE)));
			Bundle bundle2 = startBundle("org.camunda.bpm.engine.osgi");
			assertThat(bundle2.getState(), is(equalTo(Bundle.ACTIVE)));
		} catch (BundleException be) {
			fail(be.toString());
		}
	}

	@Test
	public void checkServices() {
		try {
			startBundle("org.camunda.bpm.engine.osgi");
			ServiceReference[] service = ctx.getServiceReferences(
					URLStreamHandlerService.class.getName(),
					"(url.handler.protocol=bpmn)");
			assertThat(service.length, is(1));
			assertThat(ctx.getService(service[0]),
					is(instanceOf(BpmnURLHandler.class)));
			ServiceReference[] service2 = ctx.getServiceReferences(
					URLStreamHandlerService.class.getName(),
					"(url.handler.protocol=bar)");
			assertThat(service2.length, is(1));
			assertThat(ctx.getService(service2[0]),
					is(instanceOf(BarURLHandler.class)));
		} catch (InvalidSyntaxException e) {
			fail(e.toString());
		} catch (BundleException e) {
			fail(e.toString());
		}

	}

}
