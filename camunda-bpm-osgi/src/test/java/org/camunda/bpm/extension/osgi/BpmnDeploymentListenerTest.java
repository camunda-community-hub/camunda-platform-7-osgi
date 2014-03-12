package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.camunda.bpm.extension.osgi.BpmnDeploymentListener;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

public class BpmnDeploymentListenerTest {

	private BpmnDeploymentListener listener;

	@Before
	public void setUp() {
		listener = new BpmnDeploymentListener();
	}

	@Test
	public void canHandleBpmnFile() {
		File file = new File("src/test/resources/testprocess.bpmn");
		boolean canHandle = listener.canHandle(file);
		assertThat(canHandle, is(true));
	}

	@Test
	public void canHandleBpmnXmlFile() throws IOException {
		File bpmnXmlFile = new File("testprocess.bpmn20.xml");
		FileUtils.copyFile(new File("src/test/resources/testprocess.bpmn"),
				bpmnXmlFile);
		bpmnXmlFile.deleteOnExit();
		boolean canHandle = listener.canHandle(bpmnXmlFile);
		assertThat(canHandle, is(true));
	}

	@Test
	public void parseBpmnFile() throws Exception {
		Document doc = listener.parse(new File(
				"src/test/resources/testprocess.bpmn"));
		assertThat(doc, is(notNullValue()));
		assertThat(doc.getDocumentElement().toString(),
				containsString("bpmn2:definitions"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseNull() throws Exception {
		Document doc = listener.parse(null);
		assertThat(doc, is(notNullValue()));
	}

	@Test(expected=SAXParseException.class)
	public void parseWrongFile() throws Exception {
		File file = new File("test.txt");
		file.deleteOnExit();
		FileUtils.write(file, "bpmn xml process defintion task");
		Document doc = listener.parse(file);
		assertThat(doc, is(notNullValue()));
	}
}
