package org.camunda.bpm.extension.osgi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to extract name, version and type from a jar file.
 * 
 * @author Ronny Bräunlich
 *
 */
public class NameVersionTypeExtractor {

	private static final String DEFAULT_VERSION = "0.0.0";

	/**
	 * I think the ARTIFACT_MATCHER needs explanation:
	 * (.+) - an arbitrary sign one or more times (e.g. package name)</br>
	 * (?:  - start non capturing group</br>
	 * - 	- single dash</br>
	 * (\\d+) - one or more digits (major)</br>
	 * (?: - start non capturing group</br>
	 * \\. - dot</br>
	 * (\\d+) - one or more digits (minor)</br>
	 * (?: - start non capturing group</br>
	 * \\. - dot</br>
	 * (\\d+) - one or more digits (bugfix)</br>
	 * )? - end non capturing group, once or never</br>
	 * )? - end non capturing group, once or never</br>
	 * (?: - start non capturing group</br>
	 * [^a-zA-Z0-9] - no sign, no digit (separator qualifier)</br>
	 * (.*) - any sign (qualifier)</br>
	 * )? - end non capturing group, once or never</br>
	 * ) - end group</br>
	 * (?: - start non capturing group</br>
	 * \\. - dot</br>
	 * ([^\\.]+) - anything but dot</br>
	 * ) end group
	 */
	private static final Pattern ARTIFACT_MATCHER = Pattern
			.compile(
					"(.+)(?:-(\\d+)(?:\\.(\\d+)(?:\\.(\\d+))?)?(?:[^a-zA-Z0-9](.*))?)(?:\\.([^\\.]+))",
					Pattern.DOTALL);
	private static final Pattern FUZZY_MODIFIDER = Pattern.compile(
			"(?:\\d+[.-])*(.*)", Pattern.DOTALL);
	private static Pattern SIMPLE_FILENAME_MATCHER = Pattern.compile("(.+)\\.(.+)", Pattern.DOTALL);
	
	/**
	 * Tries to extract name, version and type from a jar name.
	 * The return String-array contains either
	 * <p>
	 * name and version
	 * <p>or<p>
	 * name, version and type
	 * <p>
	 * @param jarName
	 * @return
	 */
	public static String[] extractNameVersionType(String jarName) {
		Matcher m = ARTIFACT_MATCHER.matcher(jarName);
		if (!m.matches()) {
			m = SIMPLE_FILENAME_MATCHER.matcher(jarName);
			if(m.matches()) {
				return new String[] { m.group(1), DEFAULT_VERSION, m.group(2) };
			} else {
				//we definitely cannot find a meaningful name
				return new String[] { jarName, DEFAULT_VERSION };
			}
		} else {
			StringBuffer v = new StringBuffer();
			String d1 = m.group(1); //name
			String d2 = m.group(2); //major
			String d3 = m.group(3); //minor
			String d4 = m.group(4); //bugfix
			String d5 = m.group(5); //qualifier
			String d6 = m.group(6); //file extension
			if (d2 != null) {
				v.append(d2);
				if (d3 != null) {
					v.append('.');
					v.append(d3);
					if (d4 != null) {
						v.append('.');
						v.append(d4);
						if (d5 != null) {
							v.append(".");
							cleanupModifier(v, d5);
						}
					} else if (d5 != null) {
						v.append(".0.");
						cleanupModifier(v, d5);
					}
				} else if (d5 != null) {
					v.append(".0.0.");
					cleanupModifier(v, d5);
				}
			}
			return new String[] { d1, v.toString(), d6 };
		}
	}

	private static void cleanupModifier(StringBuffer result, String modifier) {
		Matcher m = FUZZY_MODIFIDER.matcher(modifier);
		if (m.matches()) {
			modifier = m.group(1);
		}
		for (int i = 0; i < modifier.length(); i++) {
			char c = modifier.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z') || c == '_' || c == '-') {
				result.append(c);
			}
		}
	}
}
