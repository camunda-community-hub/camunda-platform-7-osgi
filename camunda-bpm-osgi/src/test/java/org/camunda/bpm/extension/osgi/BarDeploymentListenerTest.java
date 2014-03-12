package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

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

/**
 * Simple test to check the {@link BarDeploymentListener}. Unfortunately we have
 * to start the whole OSGi-environment to get the {@link BarURLHandler}
 * registered.
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BarDeploymentListenerTest extends OSGiTestCase {

	private BarDeploymentListener listener;

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
		listener = new BarDeploymentListener();
	}

	@Test
	public void transformValidURL() throws MalformedURLException {
		URL url = new URL("file://path/to/bar");
		try {
			URL transformed = listener.transform(url);
			assertThat(transformed.toString(),
					is(equalTo("bar:" + url.toString())));
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	@Test
	public void transformURLReturnsNullWhenReceivesNull() {
		try {
			URL transformed = listener.transform(null);
			assertThat(transformed, is(nullValue()));
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	@Test
	public void canHandleBarfileWithoutManifest() {
		File jar = createBar(null);
		assertThat(listener.canHandle(jar), is(true));
	}

	@Test
	public void canHandleBarfileWithEmptyManifest() {
		File jar = createBar("");
		assertThat(listener.canHandle(jar), is(true));
	}

	@Test
	public void canHandleBarfileWithJustSymbolicNameManifest()
			throws IOException {
		File jar = createBar("Manifest-Version: 1.0\nBundle-SymbolicName: bar.foo");
		assertThat(listener.canHandle(jar), is(true));
	}

	@Test
	public void cannotHandleBarfileWithSymbolicNameAndVersionManifest()
			throws IOException {
		File jar = createBar("Manifest-Version: 1.0\nBundle-SymbolicName: bar.foo\nBundle-Version: 1.0.0\n");
		assertThat(listener.canHandle(jar), is(false));
	}

	@Test
	public void cannotHandleJar() {
		File jar = createJar("foo.jar", "");
		assertThat(listener.canHandle(jar), is(false));
	}
	
	@Test
	public void cannotHandleNonexistantBarfile() {
		File jar = new File("./test.bar");
		assertThat(listener.canHandle(jar), is(false));
	}

	private File createBar(String manifest) {
		return createJar("foo.bar", manifest);
	}

	private File createJar(String filename, String manifest) {
		try {
			File file = new File(filename);
			file.deleteOnExit();
			FileOutputStream fout = new FileOutputStream(file);
			JarOutputStream jarOut = new JarOutputStream(fout);
			jarOut.putNextEntry(new ZipEntry("META-INF"));
			jarOut.closeEntry();
			if (manifest != null) {
				jarOut.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
				jarOut.write(manifest.getBytes());
				jarOut.closeEntry();
			}
			jarOut.close();
			fout.close();
			return file;
		} catch (IOException ioe) {
			fail(ioe.toString());
		}
		return null;
	}
}
