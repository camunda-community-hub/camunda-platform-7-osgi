package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.camunda.bpm.extension.osgi.BarDeploymentListener;
import org.camunda.bpm.extension.osgi.BarURLHandler;
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

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BarURLHandlerTest extends OSGiTestCase {

	private BarURLHandler handler;

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
		handler = new BarURLHandler();
	}

	@Test
	public void handlerOpenConnection() throws Exception {
		File file = new File(".");
		URL url = new BarDeploymentListener().transform(file.toURI().toURL());
		URLConnection connection = handler.openConnection(url);
		assertThat(connection, is(notNullValue()));
	}

	@Test
	public void getBarXmlUrl() throws Exception {
		File file = new File(".");
		URL url = new BarDeploymentListener().transform(file.toURI().toURL());
		handler.openConnection(url);
		assertThat(handler.getBarXmlURL().toString(),
				is(equalTo(url.getPath())));
	}

	@Test
	public void openConnection() throws Exception {
		File file = new File(".");
		URL url = new BarDeploymentListener().transform(file.toURI().toURL());
		URLConnection connection = handler.openConnection(url);
		InputStream inputStream = connection.getInputStream();
		assertThat(inputStream, is(notNullValue()));
	}
}
