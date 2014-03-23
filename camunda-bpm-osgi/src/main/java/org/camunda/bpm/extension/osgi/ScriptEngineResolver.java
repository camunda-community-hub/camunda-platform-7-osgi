package org.camunda.bpm.extension.osgi;

import javax.script.ScriptEngine;

public interface ScriptEngineResolver {
	ScriptEngine resolveScriptEngine(String name);
}