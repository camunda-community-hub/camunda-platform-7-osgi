package org.camunda.bpm.extension.osgi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;

public class JarFileBuilder {

	private File file;
	private JarOutputStream jarOut;
	private List<String> dirEntries = new ArrayList<String>();
	private String manifest;
	private Map<String, File> files = new HashMap<String, File>();

	public JarFileBuilder(String jarFileName) {
		this.file = new File(jarFileName);
		file.deleteOnExit();
		try {
			FileOutputStream fout = new FileOutputStream(file);
			jarOut = new JarOutputStream(fout);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public JarFileBuilder addDirEntry(String dirName) {
		dirEntries.add(dirName);
		try {
			jarOut.putNextEntry(new ZipEntry(dirName));
			jarOut.closeEntry();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * Splits the dirNames string at the slashes
	 * 
	 * @param dirNames
	 * @return
	 */
	public JarFileBuilder addDirEntries(String dirNames) {
		dirEntries.add(dirNames);

		return this;
	}

	public JarFileBuilder addFileEntry(String fileNameInJar, File file) {
		files.put(fileNameInJar, file);
		return this;
	}

	/**
	 * Adds a MANIFEST.MF to the jar in the default location (META-INF). The
	 * Manifest-String can be one line, separated by \n but no spaces, e.g.
	 * <p>
	 * <code>
	 * Manifest-Version: 1.0\nBundle-SymbolicName: bar.foo\nBundle-Version: 1.0.0\n
	 * </code>
	 * 
	 * @param manifest
	 * @return
	 */
	public JarFileBuilder addManifest(String manifest) {
		this.manifest = manifest;
		return this;
	}

	public File createJarFile() {
		// I have to wait till the end to assemble the jar because
		// java.util.jar.JarInputStream expects META-INF/MANIFEST.MF
		// to be the first directory in the jar
		createManifest();
		createDirectories();
		createFiles();
		try {
			jarOut.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return file;
	}

	private void createFiles() {
		try {
			for (Entry<String, File> entry : files.entrySet()) {
				jarOut.putNextEntry(new ZipEntry(entry.getKey()));
				jarOut.write(FileUtils.readFileToByteArray(entry.getValue()));
				jarOut.closeEntry();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void createDirectories() {
		try {
			for (String dirName : dirEntries) {
				String dirsConcatenated = "";
				for (String s : dirName.split("/")) {
					dirsConcatenated += s + "/";
					jarOut.putNextEntry(new ZipEntry(dirsConcatenated));
					jarOut.closeEntry();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void createManifest() {
		if (manifest != null) {
			try {
				jarOut.putNextEntry(new ZipEntry("META-INF/"));
				jarOut.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
				jarOut.write(manifest.getBytes());
				jarOut.closeEntry();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
