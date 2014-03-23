package org.camunda.bpm.extension.osgi;

import java.net.URL;
import java.util.List;

import org.osgi.framework.Bundle;

public interface ProcessDefinitionDeployer {

	public abstract void deployProcessDefinitions(Bundle bundle,
			List<URL> pathList);

}