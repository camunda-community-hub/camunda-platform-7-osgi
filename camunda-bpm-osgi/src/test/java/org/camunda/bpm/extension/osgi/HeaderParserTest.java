package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.camunda.bpm.extension.osgi.HeaderParser;
import org.camunda.bpm.extension.osgi.HeaderParser.PathElement;
import org.junit.Test;

public class HeaderParserTest {

	@Test
	public void parseNull() {
		List<PathElement> list = HeaderParser.parseHeader(null);
		assertThat(list.isEmpty(), is(true));
	}

	@Test
	public void parseEmptyString() {
		List<PathElement> list = HeaderParser.parseHeader("");
		assertThat(list.isEmpty(), is(true));
	}

	@Test
	public void parseBlankString() {
		List<PathElement> list = HeaderParser.parseHeader(" ");
		assertThat(list.isEmpty(), is(true));
	}

	@Test
	public void parseSimplePath() {
		String header = "/foo/bar";
		List<PathElement> list = HeaderParser.parseHeader(header);
		assertThat(list.size(), is(1));
		PathElement element = list.get(0);
		assertThat(element.getName(), is(equalTo(header)));
	}

	@Test
	public void parsePathWithDirective() {
		String header = "/bar/foo;attribute:=value";
		List<PathElement> list = HeaderParser.parseHeader(header);
		assertThat(list.size(), is(1));
		PathElement element = list.get(0);
		assertThat(element.getName(), is("/bar/foo"));
		assertThat(element.getDirective("attribute"), is("value"));
	}

	@Test
	public void parsePathWithAttribute() {
		String header = "/bar/foo;version=1.0.0";
		List<PathElement> list = HeaderParser.parseHeader(header);
		assertThat(list.size(), is(1));
		PathElement element = list.get(0);
		assertThat(element.getName(), is("/bar/foo"));
		assertThat(element.getAttribute("version"), is("1.0.0"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseInvalidHeader() {
		HeaderParser.parseHeader(";");
	}

	@Test
	public void parseMultiplePaths() {
		String header = "/bar/foo, /dev/null;version=1.1.1, C:\\windows\\;unix:=false";
		List<PathElement> list = HeaderParser.parseHeader(header);
		assertThat(list.size(), is(3));
		for (PathElement elem : list) {
			if (elem.getName().equals("/bar/foo")) {
				assertThat(elem.getAttributes().isEmpty(), is(true));
				assertThat(elem.getDirectives().isEmpty(), is(true));
			} else if (elem.getName().equals("/dev/null")) {
				assertThat(elem.getAttribute("version"), is("1.1.1"));
				assertThat(elem.getDirectives().isEmpty(), is(true));
			} else if (elem.getName().equals("C:\\windows\\")) {
				assertThat(elem.getAttributes().isEmpty(), is(true));
				assertThat(elem.getDirective("unix"), is("false"));
			} else {
				fail("Element " + elem.getName() + " didn't have expected name");
			}
		}
	}
	
	@Test
	public void parseHeaderWithSemicolon(){
		String header = "/foo/bar;/dev/null";
		List<PathElement> list = HeaderParser.parseHeader(header);
		assertThat(list.size(), is(2));
		assertThat(list.get(0).getName(), is(anyOf(is("/foo/bar"),is( "/dev/null"))));
		assertThat(list.get(1).getName(), is(anyOf(is("/foo/bar"),is( "/dev/null"))));
	}
}
