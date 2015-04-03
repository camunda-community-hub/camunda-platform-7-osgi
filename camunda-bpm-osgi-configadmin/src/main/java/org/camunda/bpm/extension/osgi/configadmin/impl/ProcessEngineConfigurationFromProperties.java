package org.camunda.bpm.extension.osgi.configadmin.impl;

import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;

import java.util.Collections;
import java.util.Dictionary;
import java.util.logging.Logger;

public class ProcessEngineConfigurationFromProperties extends StandaloneProcessEngineConfiguration {

		public void configure(Dictionary<String, String> configuration) {
				for (String key : Collections.list(configuration.keys())) {
						ProcessEngineConfigurationProperties configurationProperty = ProcessEngineConfigurationProperties.getPropertyByKey(key);
						if (configurationProperty == null) {
								Logger.getLogger(this.getClass().getName()).info("Couldn't set value " + configuration.get(key) + " for " + key);
						} else {
								configurationProperty.setPropertyOnConfiguration(this, configuration.get(key));
						}
				}
		}
}
