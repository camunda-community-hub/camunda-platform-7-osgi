package org.camunda.bpm.extension.osgi;

import javax.script.ScriptEngine;

/**
 * A {@link ScriptEngineResolver} tries to find a {@link ScriptEngine} according
 * to the given criteria.
 * 
 * @author Ronny Bräunlich
 * 
 */
public interface ScriptEngineResolver {

	/**
	 * Tries to find a script engine matching the given name.s
	 * 
	 * @param name
	 * @return a {@link ScriptEngine} which matches the name or null if none
	 *         could be found.
	 */
	ScriptEngine resolveScriptEngine(String name);
}