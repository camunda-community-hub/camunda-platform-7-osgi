package org.camunda.bpm.extension.osgi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

public class BundleScriptEngineResolver implements ScriptEngineResolver {

	private static final Logger LOGGER = Logger
			.getLogger(BundleScriptEngineResolver.class.getName());

	private final Bundle bundle;
	private ServiceRegistration reg;
	private final URL configFile;

	public BundleScriptEngineResolver(Bundle bundle, URL configFile) {
		this.bundle = bundle;
		this.configFile = configFile;
	}

	public void register() {
		if (bundle.getBundleContext() != null) {
			reg = bundle.getBundleContext().registerService(
					ScriptEngineResolver.class.getName(), this, null);
		}
	}

	public void unregister() {
		if (reg != null) {
			reg.unregister();
		}
	}

	public ScriptEngine resolveScriptEngine(String name) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					configFile.openStream()));
			String className = in.readLine();
			in.close();
			Class<?> cls = bundle.loadClass(className);
			if (!ScriptEngineFactory.class.isAssignableFrom(cls)) {
				throw new IllegalStateException("Invalid ScriptEngineFactory: "
						+ cls.getName());
			}
			ScriptEngineFactory factory = (ScriptEngineFactory) cls
					.newInstance();
			List<String> names = factory.getNames();
			for (String test : names) {
				if (test.equals(name)) {
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

	@Override
	public String toString() {
		return "OSGi script engine resolver for " + bundle.getSymbolicName();
	}
}