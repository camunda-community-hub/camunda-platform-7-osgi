package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.camunda.bpm.extension.osgi.BpmnDeploymentListener;
import org.camunda.bpm.extension.osgi.BpmnURLHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
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
public class BpmnDeploymentListenerIntegrationTest extends OSGiTestCase {
	private BpmnDeploymentListener listener;

	@Override
	@Configuration
	public Option[] createConfiguration() {
		MavenArtifactProvisionOption felixFileinstall = mavenBundle()
				.groupId("org.apache.felix.")
				.artifactId("org.apache.felix.fileinstall").version("3.0.2");
		return OptionUtils.combine(super.createConfiguration(),
				felixFileinstall);
	}

	@Before
	public void setUp() {
		listener = new BpmnDeploymentListener();
	}

	@Test
	public void transform() throws MalformedURLException {
		File file = new File("src/test/resources/testprocess.bpmn");
		URL url = listener.transform(file.toURI().toURL());
		assertThat(url.toString(), is(equalTo("bpmn:" + file.toURI().toURL())));
	}

	@Test
	public void transformNull() {
		URL url = listener.transform(null);
		assertThat(url, is(nullValue()));
	}
}
