package org.camunda.bpm.extension.osgi;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.script.ScriptEngineFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * Customized tracker, which searches the {@link Bundle}s for
 * {@link ScriptEngineFactory}s.
 * 
 * @author Ronny Bräunlich
 * 
 */
public class ScriptEngineBundleTrackerCustomizer implements
		BundleTrackerCustomizer {

	private static final String META_INF_SERVICES_DIR = "META-INF/services";
	private static final String SCRIPT_ENGINE_SERVICE_FILE = "javax.script.ScriptEngineFactory";
	private static final Logger LOGGER = Logger
			.getLogger(ScriptEngineBundleTrackerCustomizer.class.getName());
	private Map<Long, List<BundleScriptEngineResolver>> resolvers = new ConcurrentHashMap<Long, List<BundleScriptEngineResolver>>();
	private ProcessDefintionChecker checker;

	public ScriptEngineBundleTrackerCustomizer(ProcessDefintionChecker checker) {
		this.checker = checker;
	}

	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		if (event == null) {
			// existing bundles first added to the tracker with no event change
			checkInitialBundle(bundle);
		} else {
			bundleChanged(event);
		}
		List<BundleScriptEngineResolver> r = new ArrayList<BundleScriptEngineResolver>();
		registerScriptEngines(bundle, r);
		for (BundleScriptEngineResolver service : r) {
			service.register();
		}
		resolvers.put(bundle.getBundleId(), r);

		return bundle;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		if (event == null) {
			// cannot think of why we would be interested in a modified bundle
			// with no bundle event
			return;
		}
		bundleChanged(event);
	}

	// don't think we would be interested in removedBundle, as that is
	// called when bundle is removed from the tracker
	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
		List<BundleScriptEngineResolver> r = resolvers.remove(bundle
				.getBundleId());
		if (r != null) {
			for (BundleScriptEngineResolver service : r) {
				service.unregister();
			}
		}
	}

	/**
	 * this method checks the initial bundle that are installed/active before
	 * bundle tracker is opened.
	 * 
	 * @param b
	 *            the bundle to check
	 */
	private void checkInitialBundle(Bundle b) {
		// TODO do we have to check the state? This method only gets called from
		// addingBundle()
		// If the bundle is active, check it
		if (b.getState() == Bundle.RESOLVED || b.getState() == Bundle.STARTING
				|| b.getState() == Bundle.ACTIVE) {
			checker.checkBundle(b);
		}
	}

	/**
	 * Did the bundle get resolved?
	 * 
	 * @param event
	 */
	private void bundleChanged(BundleEvent event) {
		Bundle bundle = event.getBundle();
		if (event.getType() == BundleEvent.RESOLVED) {
			checker.checkBundle(bundle);
		}
	}

	protected void registerScriptEngines(Bundle bundle,
			List<BundleScriptEngineResolver> resolvers) {
		@SuppressWarnings("unchecked")
		Enumeration<URL> scriptEnginesUrls = bundle.findEntries(
				META_INF_SERVICES_DIR, SCRIPT_ENGINE_SERVICE_FILE, false);
		if (scriptEnginesUrls == null) {
			return;
		} else {
			for (URL configURL : Collections.list(scriptEnginesUrls)) {
				LOGGER.info("Found ScriptEngineFactory in "
						+ bundle.getSymbolicName());
				resolvers
						.add(new BundleScriptEngineResolver(bundle, configURL));
			}
		}
	}

	public Map<Long, List<BundleScriptEngineResolver>> getResolvers() {
		return Collections.unmodifiableMap(resolvers);
	}
}
