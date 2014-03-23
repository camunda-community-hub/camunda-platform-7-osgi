package org.camunda.bpm.extension.osgi;

import static org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINITIONS_HEADER;
import static org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.osgi.framework.Bundle;

public class ProcessDefinitionParserTest {

	@Test
	public void parseDirectoryPathFromHeader() throws MalformedURLException {
		Bundle bundle = mock(Bundle.class);
		Properties properties = new Properties();
		String path = "/foo/bar/";
		properties.put(BUNDLE_PROCESS_DEFINITIONS_HEADER, path);
		when(bundle.getHeaders()).thenReturn(properties);
		List<URL> urls = new ArrayList<URL>();
		urls.add(new URL("file://process1.bpmn"));
		urls.add(new URL("file://process2.bpmn20.xml"));
		when(bundle.findEntries(eq(path), eq("*.*"), eq(false))).thenReturn(
				Collections.enumeration(urls));
		List<URL> processes = ProcessDefinitionParser.scanForProcesses(bundle);
		assertThat(processes, hasItems(urls.toArray(new URL[] {})));
		verify(bundle).findEntries(eq(path), eq("*.*"), eq(false));
	}

	@Test
	public void parseFilePathFromHeader() throws MalformedURLException {
		Bundle bundle = mock(Bundle.class);
		Properties properties = new Properties();
		String path = "/foo/bar/file.bpmn";
		properties.put(BUNDLE_PROCESS_DEFINITIONS_HEADER, path);
		when(bundle.getHeaders()).thenReturn(properties);
		URL url = new URL("file://" + path);
		when(bundle.getEntry(eq(path))).thenReturn(url);
		List<URL> processes = ProcessDefinitionParser.scanForProcesses(bundle);
		assertThat(processes.size(), is(1));
		assertThat(processes, hasItem(url));
		verify(bundle).getEntry(path);
	}

	@Test
	public void parseWildcardFilePathFromHeader() throws MalformedURLException {
		Bundle bundle = mock(Bundle.class);
		Properties properties = new Properties();
		String path = "/foo/bar/";
		String filePattern = "fi*.bpmn";
		properties.put(BUNDLE_PROCESS_DEFINITIONS_HEADER, path + filePattern);
		when(bundle.getHeaders()).thenReturn(properties);
		URL url = new URL("file://" + path + filePattern);
		when(bundle.findEntries(eq(path), eq(filePattern), eq(false)))
				.thenReturn(
						Collections.enumeration(Collections.singletonList(url)));
		List<URL> processes = ProcessDefinitionParser.scanForProcesses(bundle);
		assertThat(processes.size(), is(1));
		assertThat(processes, hasItem(url));
	}

	@Test
	public void parseDirectFilePathFromHeader() throws MalformedURLException {
		Bundle bundle = mock(Bundle.class);
		Properties properties = new Properties();
		String path = "process.bpmn";
		properties.put(BUNDLE_PROCESS_DEFINITIONS_HEADER, path);
		when(bundle.getHeaders()).thenReturn(properties);
		URL url = new URL("file://" + path);
		when(bundle.getEntry(eq(path))).thenReturn(url);
		List<URL> processes = ProcessDefinitionParser.scanForProcesses(bundle);
		assertThat(processes.size(), is(1));
		assertThat(processes, hasItem(url));
	}

	@Test
	public void parseFilePathFromDefaultLocation() throws MalformedURLException {
		Bundle bundle = mock(Bundle.class);
		String path = "process.bpmn";
		when(bundle.getHeaders()).thenReturn(new Properties());
		URL url = new URL("file://" + path);
		when(
				bundle.findEntries(eq(BUNDLE_PROCESS_DEFINTIONS_DEFAULT),
						eq("*.*"), eq(false))).thenReturn(
				Collections.enumeration(Collections.singletonList(url)));
		List<URL> processes = ProcessDefinitionParser.scanForProcesses(bundle);
		assertThat(processes.size(), is(1));
		assertThat(processes, hasItem(url));
	}
}
