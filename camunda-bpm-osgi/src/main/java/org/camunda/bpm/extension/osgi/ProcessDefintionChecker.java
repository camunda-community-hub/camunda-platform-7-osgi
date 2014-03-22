package org.camunda.bpm.extension.osgi;

import org.osgi.framework.Bundle;

/**
 * Interface for a class, that scan a {@link Bundle} to check, if it contains
 * any process definitions.
 * 
 * @author Ronny Bräunlich
 * 
 */
public interface ProcessDefintionChecker {
	/**
	 * Checks a bundle if it contains process definitions (BPMN-XML-files) in the
	 * default location or a a location specified by a header.
	 * 
	 * @param bundle the bundle to check
	 */
	void checkBundle(Bundle bundle);
}
