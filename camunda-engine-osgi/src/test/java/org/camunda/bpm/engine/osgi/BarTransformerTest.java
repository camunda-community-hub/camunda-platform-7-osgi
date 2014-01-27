package org.camunda.bpm.engine.osgi;

import static org.camunda.bpm.engine.osgi.Constants.BUNDLE_PROCESS_DEFINITIONS_HEADER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.Constants;

public class BarTransformerTest {

	@Test
	public void transformSimpleJar() throws Exception {
		String packageName = "com/foo/bar/";
		File jar = createJar("foo.jar", packageName);
		File result = new File("result.jar");
		result.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(result);
		BarTransformer.transform(jar.toURI().toURL(), fos);
		assertThat(result.exists(), is(true));
		// FIXME why should the symbolic name of foo.jar be foo.jar?
		checkResultJar(result, "0.0.0", packageName);
	}

	@Test
	public void transformJarWithVersionInName() throws Exception {
		String packageName = "com/foo/bar/";
		File jar = createJar("foo-1.0.0.jar", packageName);
		File result = new File("result.jar");
		result.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(result);
		BarTransformer.transform(jar.toURI().toURL(), fos);
		assertThat(result.exists(), is(true));
		checkResultJar(result, "1.0.0", packageName);
	}

	@Test
	public void transformJarWithVersionAndModifierInName() throws Exception {
		String packageName = "com/foo/bar/";
		File jar = createJar("foo-1.0.0-SNAPSHOT.jar", packageName);
		File result = new File("result.jar");
		result.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(result);
		BarTransformer.transform(jar.toURI().toURL(), fos);
		assertThat(result.exists(), is(true));
		checkResultJar(result, "1.0.0.SNAPSHOT", packageName);
	}

	@Test
	public void transformJarWithoutPackage() throws Exception {
		String packageName = "/";
		File jar = createJar("foo-1.0.0.jar", packageName);
		File result = new File("result.jar");
		result.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(result);
		BarTransformer.transform(jar.toURI().toURL(), fos);
		assertThat(result.exists(), is(true));
		checkResultJar(result, "1.0.0", packageName);
	}
	

	@Test
	@Ignore("to implement")
	public void transformJarWithManifest() throws Exception {
	}

	private void checkResultJar(File result, String expectedVersion,
			String expectedPackage) throws Exception {
		JarInputStream jarInputStream = new JarInputStream(new FileInputStream(
				result));
		Manifest manifest = jarInputStream.getManifest();
		assertThat(manifest, is(notNullValue()));
		Attributes attributes = manifest.getMainAttributes();
		assertThat(attributes.getValue(Constants.BUNDLE_MANIFESTVERSION),
				is("2"));
		assertThat(attributes.getValue(Constants.BUNDLE_SYMBOLICNAME),
				is("foo"));
		assertThat(attributes.getValue(Constants.BUNDLE_VERSION),
				is(expectedVersion));
		assertThat(attributes.getValue(BUNDLE_PROCESS_DEFINITIONS_HEADER),
				is(""));
		assertThat(jarInputStream.getNextJarEntry().getName(),
				is(expectedPackage));
		jarInputStream.close();

	}

	private File createJar(String jarName, String packageName) {
		try {
			File file = new File(jarName);
			file.deleteOnExit();
			FileOutputStream fout = new FileOutputStream(file);
			JarOutputStream jarOut = new JarOutputStream(fout);
			jarOut.putNextEntry(new ZipEntry(packageName));
			jarOut.closeEntry();
			jarOut.close();
			fout.close();
			return file;
		} catch (IOException ioe) {
			fail(ioe.toString());
		}
		return null;
	}
	
}
