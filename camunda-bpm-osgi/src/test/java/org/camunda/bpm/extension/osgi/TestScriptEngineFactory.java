package org.camunda.bpm.extension.osgi;

import java.io.Reader;
import java.util.Collections;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

public class TestScriptEngineFactory implements ScriptEngineFactory {

	@Override
	public String getEngineName() {
		return "Test-Engine";
	}

	@Override
	public String getEngineVersion() {
		return "0.0.1";
	}

	@Override
	public List<String> getExtensions() {
		return Collections.emptyList();
	}

	@Override
	public String getLanguageName() {
		return "Uber-language";
	}

	@Override
	public String getLanguageVersion() {
		return "0.0.1";
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		return null;
	}

	@Override
	public List<String> getMimeTypes() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getNames() {
		return Collections.singletonList(getLanguageName());
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return null;
	}

	@Override
	public Object getParameter(String key) {
		return null;
	}

	@Override
	public String getProgram(String... statements) {
		return null;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return new TestScriptEngine();
	}
	
	public static class TestScriptEngine implements ScriptEngine{

		@Override
		public Bindings createBindings() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(String script) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(Reader reader) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(String script, ScriptContext context)
				throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(Reader reader, ScriptContext context)
				throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(String script, Bindings n) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(Reader reader, Bindings n) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object get(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bindings getBindings(int scope) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ScriptContext getContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ScriptEngineFactory getFactory() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void put(String key, Object value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setBindings(Bindings bindings, int scope) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setContext(ScriptContext context) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
