package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.camunda.bpm.extension.osgi.BpmnURLHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BpmnURLHandlerTest extends OSGiTestCase {

	private BpmnURLHandler handler;

	@Before
	public void setUp() {
		handler = new BpmnURLHandler();
	}

	@Test
	public void getInitialBpmnXmlURL() {
		assertThat(handler.getBpmnXmlURL(), is(nullValue()));
	}

	@Test
	public void getSetBpmnXmlURL() throws Exception {
		handler.openConnection(new URL("bpmn:file:."));
		assertThat(handler.getBpmnXmlURL(), is(notNullValue()));
		assertThat(handler.getBpmnXmlURL().toString(), is(equalTo("file:.")));
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
