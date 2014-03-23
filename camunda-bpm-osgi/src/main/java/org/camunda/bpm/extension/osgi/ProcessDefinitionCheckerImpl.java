package org.camunda.bpm.extension.osgi;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;

public class ProcessDefinitionCheckerImpl implements ProcessDefintionChecker {

	private static final Logger LOGGER = Logger
			.getLogger(ProcessDefinitionCheckerImpl.class.getName());

	private ProcessDefinitionDeployer deployer;

	public ProcessDefinitionCheckerImpl(ProcessDefinitionDeployer deployer) {
		this.deployer = deployer;
	}

	@Override
	public void checkBundle(Bundle bundle) {
		List<URL> pathList = ProcessDefinitionParser.scanForProcesses(bundle);
		if (!pathList.isEmpty()) {
			deployer.deployProcessDefinitions(bundle, pathList);
		} else {
			LOGGER.log(Level.FINE, "No process found in bundle {}",
					bundle.getSymbolicName());
		}
	}

}
