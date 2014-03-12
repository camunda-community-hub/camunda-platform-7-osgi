package org.camunda.bpm.extension.osgi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.junit.Assert.assertThat;

import org.camunda.bpm.extension.osgi.NameVersionTypeExtractor;
import org.junit.Test;

/**
 * 
 * @author Ronny Bräunlich
 * 
 */
public class NameVersionTypeExtractorTest {

	@Test
	public void basicJarName() {
		String jarName = "foo.jar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "foo", "0.0.0", "jar");
	}

	@Test
	public void basicJarNameWithVersion() {
		String jarName = "foo-1.0.0.jar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "foo", "1.0.0", "jar");
	}

	@Test
	public void basicJarNameWithVersionAndDashQualifier() {
		String jarName = "foo-1.2.0-SNAPSHOT.bar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "foo", "1.2.0.SNAPSHOT", "bar");
	}

	@Test
	public void basicJarNameWithVersionAndDotQualifier() {
		String jarName = "foo-1.0.3.SNAPSHOT.war";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "foo", "1.0.3.SNAPSHOT", "war");
	}

	@Test
	public void packageJarName() {
		String jarName = "org.camunda.bpm.bar.foo.jar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "org.camunda.bpm.bar.foo", "0.0.0", "jar");
	}

	@Test
	public void packageJarNameWithVersionAndDashQualifier() {
		String jarName = "org.camunda.bpm.bar.foo-1.2.4-RC1.ear";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "org.camunda.bpm.bar.foo", "1.2.4.RC1", "ear");
	}

	@Test
	public void packageJarNameWithVersionAndDotQualifier() {
		String jarName = "org.camunda.bpm.bar.foo-1.51.67.RC1.eab";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "org.camunda.bpm.bar.foo", "1.51.67.RC1" ,"eab");
	}

	@Test
	public void packageJarNameWithVersion() {
		String jarName = "org.camunda.bpm.bar.foo-1.100.1001.jar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "org.camunda.bpm.bar.foo", "1.100.1001" ,"jar");
	}

	@Test
	public void basicJarNameWithStrangeQualifier() {
		String jarName = "foo-1.0.0_$NAP§H0T.bar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "foo", "1.0.0.NAPH0T", "bar");
	}

	@Test
	public void basicJarNameWithMediumVersion() {
		String jarName = "foo-1.0.bar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "foo", "1.0", "bar");
	}

	@Test
	public void basicJarNameWithMediumVersionAndQualifier() {
		String jarName = "foo-1.0-EA.bar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "foo", "1.0.0.EA", "bar");
	}
	
	@Test
	public void basicJarNameWithShortVersion() {
		String jarName = "foo-1-RC2.bar";
		String[] strings = NameVersionTypeExtractor
				.extractNameVersionType(jarName);
		checkStrings(strings, "foo", "1.0.0.RC2", "bar");
	}
	
	private void checkStrings(String[] toCheck, String expectedName,
			String expectedVersion, String expectedType) {
		if (expectedType == null) {
			assertThat(toCheck,
					is(arrayContaining(expectedName, expectedVersion)));
		} else {
			assertThat(
					toCheck,
					is(arrayContaining(expectedName, expectedVersion,
							expectedType)));
		}
	}
}
