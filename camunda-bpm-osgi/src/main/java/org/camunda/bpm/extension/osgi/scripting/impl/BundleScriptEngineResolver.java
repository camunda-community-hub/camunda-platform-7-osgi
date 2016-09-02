package org.camunda.bpm.extension.osgi.scripting.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.camunda.bpm.extension.osgi.scripting.ScriptEngineResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

public class BundleScriptEngineResolver implements ScriptEngineResolver {

	private static final Logger LOGGER = Logger
			.getLogger(BundleScriptEngineResolver.class.getName());

	private final Bundle bundle;
	private ServiceRegistration<ScriptEngineResolver> reg;
	private final URL configFile;

	public BundleScriptEngineResolver(Bundle bundle, URL configFile) {
		this.bundle = bundle;
		this.configFile = configFile;
	}

	public void register() {
		if (bundle.getBundleContext() != null) {
			reg = bundle.getBundleContext().registerService(
					ScriptEngineResolver.class, this, null);
		}
	}

	public void unregister() {
		if (reg != null) {
			reg.unregister();
		}
	}

	@Override
  public ScriptEngine resolveScriptEngine(String name) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					configFile.openStream()));
			String className = removeCommentsFromInput(in);
			in.close();
			Class<?> cls = bundle.loadClass(className);
			if (!ScriptEngineFactory.class.isAssignableFrom(cls)) {
				throw new IllegalStateException("Invalid ScriptEngineFactory: "
						+ cls.getName());
			}
			ScriptEngineFactory factory = (ScriptEngineFactory) cls
					.newInstance();
			List<String> names = factory.getNames();
			for (String n : names) {
				if (n.equals(name)) {
					ClassLoader old = Thread.currentThread()
							.getContextClassLoader();
					ScriptEngine engine;
					try {
						// JRuby seems to require the correct TCCL to call
						// getScriptEngine
						Thread.currentThread().setContextClassLoader(
								factory.getClass().getClassLoader());
						engine = factory.getScriptEngine();
					} finally {
						Thread.currentThread().setContextClassLoader(old);
					}
					LOGGER.finest("Resolved ScriptEngineFactory: " + engine
							+ " for expected name: " + name);
					return engine;
				}
			}
			LOGGER.fine("ScriptEngineFactory: " + factory.getEngineName()
					+ " does not match expected name: " + name);
			return null;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Cannot create ScriptEngineFactory: "
					+ e.getClass().getName(), e);
			return null;
		}
	}

	/**
	 * Takes the input stream and ignores lines starting with a # and everything after a #
	 */
	private String removeCommentsFromInput(BufferedReader in) throws IOException {
	  String l = in.readLine();
	  //remove lines that start with a comment
	  while(l.startsWith("#")){
	    l = in.readLine();
	  }
	  if(l.contains("#")){
	    l = l.substring(0, l.indexOf("#"));
	  }
	  return l.trim();
  }

  @Override
	public String toString() {
		return "OSGi script engine resolver for " + bundle.getSymbolicName();
	}
}