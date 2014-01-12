package org.camunda.bpm.engine.osgi;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.url.URLStreamHandlerService;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BundleStartTestWithoutFileinstall extends OSGiTestCase {

	@Inject
	@Filter("(url.handler.protocol=bpmn)")
	private URLStreamHandlerService bpmnUrlHandler;
	
	@Inject
	@Filter("(url.handler.protocol=bar)")
	private URLStreamHandlerService barUrlHandler;
	
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
		assertThat(bpmnUrlHandler, is(notNullValue()));
		assertThat(bpmnUrlHandler, is(instanceOf(BpmnURLHandler.class)));
		assertThat(barUrlHandler, is(notNullValue()));
		assertThat(barUrlHandler, is(instanceOf(BarURLHandler.class)));
	}

}
