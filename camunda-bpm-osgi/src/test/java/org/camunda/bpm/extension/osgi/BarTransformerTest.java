package org.camunda.bpm.extension.osgi;

import static org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINITIONS_HEADER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.camunda.bpm.extension.osgi.BarTransformer;
import org.junit.Test;
import org.osgi.framework.Constants;

public class BarTransformerTest {

	@Test
	public void transformSimpleJar() throws Exception {
		JarFileBuilder builder = new JarFileBuilder("foo.jar");
		builder.addDirEntries("com/foo/bar/");
		File jar = builder.createJarFile();
		File result = getResultFromTransformer(jar);
		// FIXME why should the symbolic name of foo.jar be foo.jar?
		checkResultJar(result, "0.0.0", "com/foo/bar/", "");
	}

	@Test
	public void transformJarWithVersionInName() throws Exception {
		JarFileBuilder builder = new JarFileBuilder("foo-1.0.0.jar");
		String packageName = "com/foo/bar/";
		File jar = builder
				.addDirEntries(packageName)
				.addFileEntry("com/foo/bar/testprocess.bpmn",
						new File("src/test/resources/testprocess.bpmn"))
				.createJarFile();
		File result = getResultFromTransformer(jar);
		checkResultJar(result, "1.0.0", packageName, packageName);
	}

	@Test
	public void transformJarWithVersionAndModifierInName() throws Exception {
		File jar = new JarFileBuilder("foo-1.0.0-SNAPSHOT.jar").addDirEntries(
				"com/foo/bar/").createJarFile();
		File result = getResultFromTransformer(jar);
		checkResultJar(result, "1.0.0.SNAPSHOT", "com/foo/bar", "");
	}

	@Test
	public void transformJarWithoutPackage() throws Exception {
		String packageName = "";
		File jar = new JarFileBuilder("foo-1.0.0.jar").addDirEntry(packageName)
				.createJarFile();
		File result = getResultFromTransformer(jar);
		checkResultJar(result, "1.0.0", packageName, "/");
	}

	@Test
	public void transformJarWithManifest() throws Exception {
		File jar = new JarFileBuilder("foo-1.0.0.jar")
				.addDirEntries("com/foo/bar")
				.addManifest(
						"Manifest-Version: 1.0\nBundle-SymbolicName: com.foo\nBundle-Version: 1.0.0\n")
				.createJarFile();
		File result = getResultFromTransformer(jar);
		checkResultJar(result, "1.0.0", "com/foo/bar", "", "com.foo");
	}

	private void checkResultJar(File result, String expectedVersion,
			String expectedPackage, String expectedProcessDefHeader)
			throws Exception {
		checkResultJar(result, expectedVersion, expectedPackage,
				expectedProcessDefHeader, "foo");
	}

	private void checkResultJar(File result, String expectedVersion,
			String expectedPackage, String expectedProcessDefHeader,
			String expectedSymbolicName) throws Exception {
		JarInputStream jarInputStream = new JarInputStream(new FileInputStream(
				result));
		Manifest manifest = jarInputStream.getManifest();
		assertThat(manifest, is(notNullValue()));
		Attributes attributes = manifest.getMainAttributes();
		assertThat(attributes.getValue(Constants.BUNDLE_MANIFESTVERSION),
				is("2"));
		assertThat(attributes.getValue(Constants.BUNDLE_SYMBOLICNAME),
				is(expectedSymbolicName));
		assertThat(attributes.getValue(Constants.BUNDLE_VERSION),
				is(expectedVersion));
		assertThat(attributes.getValue(BUNDLE_PROCESS_DEFINITIONS_HEADER),
				is(expectedProcessDefHeader));
		jarInputStream.close();

	}

	private File getResultFromTransformer(File jar) {
		File result = new File("result.jar");
		result.deleteOnExit();
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(result);
			BarTransformer.transform(jar.toURI().toURL(), fos);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		assertThat(result.exists(), is(true));
		return result;
	}

}
