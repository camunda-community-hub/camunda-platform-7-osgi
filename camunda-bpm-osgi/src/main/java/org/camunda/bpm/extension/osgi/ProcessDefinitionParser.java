package org.camunda.bpm.extension.osgi;

import static org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINITIONS_HEADER;
import static org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.extension.osgi.HeaderParser.PathElement;
import org.osgi.framework.Bundle;

public final class ProcessDefinitionParser {

	private ProcessDefinitionParser() {
	}

	private static final Logger LOGGER = Logger
			.getLogger(ProcessDefinitionParser.class.getName());

	public static List<URL> scanForProcesses(Bundle bundle) {
		LOGGER.log(Level.FINE, "Scanning bundle {} for process",
				bundle.getSymbolicName());
		String processDefHeader = (String) bundle.getHeaders().get(
				BUNDLE_PROCESS_DEFINITIONS_HEADER);
		if (processDefHeader == null) {
			processDefHeader = BUNDLE_PROCESS_DEFINTIONS_DEFAULT;
		}
		List<PathElement> paths = HeaderParser.parseHeader(processDefHeader);
		List<URL> pathList = parsePaths(paths, bundle);
		return pathList;
	}

	private static List<URL> parsePaths(List<PathElement> paths, Bundle bundle) {
		List<URL> pathList = new ArrayList<URL>();
		for (PathElement path : paths) {
			String name = path.getName();
			if (name.endsWith("/")) {
				// we have a directory
				addEntries(bundle, name, "*.*", pathList);
			} else {
				// we have a file name
				String baseName;
				String filePattern;
				int pos = name.lastIndexOf('/');
				if (pos < 0) {
					baseName = "/";
					filePattern = name;
				} else {
					baseName = name.substring(0, pos + 1);
					filePattern = name.substring(pos + 1);
				}
				if (hasWildcards(filePattern)) {
					addEntries(bundle, baseName, filePattern, pathList);
				} else {
					pathList.add(bundle.getEntry(name));
				}
			}
		}
		return pathList;
	}

	private static boolean hasWildcards(String path) {
		return path.indexOf("*") >= 0;
	}

	private static void addEntries(Bundle bundle, String path,
			String filePattern, List<URL> pathList) {
		Enumeration<?> e = bundle.findEntries(path, filePattern, false);
		while (e != null && e.hasMoreElements()) {
			URL u = (URL) e.nextElement();
			pathList.add(u);
		}
	}
}
