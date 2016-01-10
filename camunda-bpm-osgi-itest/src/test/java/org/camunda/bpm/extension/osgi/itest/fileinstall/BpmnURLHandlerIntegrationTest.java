package org.camunda.bpm.extension.osgi.itest.fileinstall;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.url.URLStreamHandlerService;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BpmnURLHandlerIntegrationTest extends OSGiFileInstallTestEnvironment {
  
  @Inject
  @Filter("(url.handler.protocol=bpmn)")
	private URLStreamHandlerService handler;

	@Test(expected=NullPointerException.class)
	public void getInitialBpmnXmlURL() throws IOException {
		assertThat(handler.openConnection(null).getURL(), is(nullValue()));
	}

	@Test
	public void getSetBpmnXmlURL() throws Exception {
		URLConnection connection = handler.openConnection(new URL("bpmn:file:."));
		assertThat(connection.getURL(), is(notNullValue()));
		assertThat(connection.getURL().toString(), is(equalTo("bpmn:file:.")));
	}

	@Test(expected = MalformedURLException.class)
	public void openConnectionWithNullPath() throws Exception {
		handler.openConnection(new URL("bpmn:"));
	}

	@Test(expected = MalformedURLException.class)
	public void openConnectionWithEmptyPath() throws Exception {
		handler.openConnection(new URL("bpmn:   "));
	}

	@Test
	public void openConnection() throws Exception {
		URLConnection connection = handler.openConnection(new URL(
				"bpmn:file:src/test/resources/testprocess.bpmn"));
		assertThat(connection, is(notNullValue()));
		InputStream inputStream = connection.getInputStream();
		assertThat(inputStream, is(notNullValue()));
	}
}
