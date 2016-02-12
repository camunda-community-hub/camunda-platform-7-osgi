package org.camunda.bpm.extension.osgi.scripting.impl;

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

/**
 * Searches {@link Bundle}s for {@link ScriptEngineFactory}s and if they are
 * found they are published as {@link BundleScriptEngineResolver} services.
 * 
 * @author Ronny Bräunlich
 * 
 */
public class ScriptEngineBundleScanner {

  private static final String META_INF_SERVICES_DIR = "META-INF/services";
  private static final String SCRIPT_ENGINE_SERVICE_FILE = "javax.script.ScriptEngineFactory";
  private static final Logger LOGGER = Logger.getLogger(ScriptEngineBundleScanner.class.getName());
  private Map<Long, List<BundleScriptEngineResolver>> resolvers = new ConcurrentHashMap<Long, List<BundleScriptEngineResolver>>();

  public ScriptEngineBundleScanner() {
  }

  public Bundle addBundle(Bundle bundle) {
    List<BundleScriptEngineResolver> r = new ArrayList<BundleScriptEngineResolver>();
    registerScriptEngines(bundle, r);
    for (BundleScriptEngineResolver service : r) {
      service.register();
    }
    resolvers.put(bundle.getBundleId(), r);
    return bundle;
  }

  @SuppressWarnings("unused")
  public void modifiedBundle(Bundle bundle) {
    // so far do nothing
  }

  public void removedBundle(Bundle bundle) {
    List<BundleScriptEngineResolver> r = resolvers.remove(bundle.getBundleId());
    if (r != null) {
      for (BundleScriptEngineResolver service : r) {
        service.unregister();
      }
    }
  }

  protected void registerScriptEngines(Bundle bundle, List<BundleScriptEngineResolver> resolvers) {
    Enumeration<URL> scriptEnginesUrls = bundle.findEntries(META_INF_SERVICES_DIR, SCRIPT_ENGINE_SERVICE_FILE, false);
    if (scriptEnginesUrls == null) {
      return;
    }
    for (URL configURL : Collections.list(scriptEnginesUrls)) {
      LOGGER.info("Found ScriptEngineFactory in " + bundle.getSymbolicName());
      resolvers.add(new BundleScriptEngineResolver(bundle, configURL));
    }
  }

  public Map<Long, List<BundleScriptEngineResolver>> getResolvers() {
    return Collections.unmodifiableMap(resolvers);
  }
}
