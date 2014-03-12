package org.camunda.bpm.extension.osgi;

import static org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINITIONS_HEADER;
import static org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import org.camunda.bpm.extension.osgi.BpmnTransformer;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Constants;

/**
 * 
 * @author Ronny Bräunlich
 * 
 */
public class BpmnTransformerTest {

	private BpmnTransformer transformer;

	@Before
	public void setUp() {
		transformer = new BpmnTransformer();
	}

	@Test
	public void transformSimpleTestBpmn() throws Exception {
		File file = new File("result.jar");
		file.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(file);
		File bpmn = new File("src/test/resources/testprocess.bpmn");
		transformer.transform(bpmn.toURI().toURL(), fos);
		JarInputStream jis = new JarInputStream(new FileInputStream(file));
		Attributes attributes = jis.getManifest().getMainAttributes();
		assertThat(attributes.getValue("Manifest-Version"), is("2"));
		assertThat(attributes.getValue(Constants.BUNDLE_VERSION), is("0.0.0"));
		assertThat(attributes.getValue(Constants.BUNDLE_SYMBOLICNAME),
				is("testprocess"));
		assertThat(attributes.getValue(Constants.BUNDLE_MANIFESTVERSION),
				is("2"));
		assertThat(attributes.getValue(BUNDLE_PROCESS_DEFINITIONS_HEADER),
				is(BUNDLE_PROCESS_DEFINTIONS_DEFAULT));
		ZipEntry entry;
		while ((entry = jis.getNextEntry()) != null) {
			assertThat(
					entry.getName(),
					is(anyOf(is("OSGI-INF/"),
							is(BUNDLE_PROCESS_DEFINTIONS_DEFAULT),
							is(BUNDLE_PROCESS_DEFINTIONS_DEFAULT
									+ "testprocess.bpmn"))));
		}
		jis.close();
	}

	@Test
	public void transformTestBpmnWithManifestInformation() throws Exception {
		File file = new File("result.jar");
		file.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(file);
		File bpmn = new File("src/test/resources/testprocessWithManifest.bpmn");
		transformer.transform(bpmn.toURI().toURL(), fos);
		JarInputStream jis = new JarInputStream(new FileInputStream(file));
		Attributes attributes = jis.getManifest().getMainAttributes();
		assertThat(attributes.getValue("Manifest-Version"), is("2"));
		assertThat(attributes.getValue(Constants.BUNDLE_VERSION), is("1.2.3"));
		assertThat(attributes.getValue(Constants.BUNDLE_SYMBOLICNAME),
				is("org.camunda.test.processWithManifest"));
		assertThat(attributes.getValue(Constants.BUNDLE_MANIFESTVERSION),
				is("2"));
		assertThat(attributes.getValue(BUNDLE_PROCESS_DEFINITIONS_HEADER),
				is("OSGI-INF/my-processes/"));
		ZipEntry entry;
		while ((entry = jis.getNextEntry()) != null) {
			assertThat(
					entry.getName(),
					is(anyOf(
							is("OSGI-INF/"),
							is("OSGI-INF/my-processes/"),
							is("OSGI-INF/my-processes/testprocessWithManifest.bpmn"))));
		}
		jis.close();
	}

}
