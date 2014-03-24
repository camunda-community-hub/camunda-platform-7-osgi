package org.camunda.bpm.extension.osgi;

import java.net.URL;
import java.util.List;

import org.osgi.framework.Bundle;

/**
 * A {@link ProcessDefinitionDeployer} deploys process definitions into the
 * repository.
 * 
 * @author Ronny Bräunlich
 * 
 */
public interface ProcessDefinitionDeployer {

	/**
	 * Takes the list of URLs and deploys them into the repository with the help
	 * of the bundle.
	 * 
	 * @param bundle
	 * @param pathList
	 */
	public abstract void deployProcessDefinitions(Bundle bundle,
			List<URL> pathList);

}