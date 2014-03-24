package org.camunda.bpm.extension.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.osgi.framework.Bundle;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Basic implementation to deploy a processes to the repository by using a
 * {@link ServiceTracker} that tracks a {@link ProcessEngine}.
 * 
 * @author Ronny Bräunlich
 * 
 */
public class ProcessDefinitionDeployerImpl implements ProcessDefinitionDeployer {

	private static final Logger LOGGER = Logger
			.getLogger(ProcessDefinitionDeployerImpl.class.getName());

	private ServiceTracker engineServiceTracker;
	private long timeout = 5000;

	public ProcessDefinitionDeployerImpl(ServiceTracker engineServiceTracker) {
		this.engineServiceTracker = engineServiceTracker;
	}

	@Override
	public void deployProcessDefinitions(Bundle bundle, List<URL> pathList) {
		try {
			LOGGER.log(Level.FINE,
					"Found process in bundle " + bundle.getSymbolicName()
							+ " with paths: " + pathList);

			ProcessEngine engine = (ProcessEngine) engineServiceTracker
					.waitForService(timeout);
			if (engine == null) {
				throw new IllegalStateException(
						"Unable to find a ProcessEngine service");
			}

			RepositoryService service = engine.getRepositoryService();
			DeploymentBuilder builder = service.createDeployment();
			builder.name(bundle.getSymbolicName());
			for (URL url : pathList) {
				InputStream is = url.openStream();
				if (is == null) {
					throw new IOException("Error opening url: " + url);
				}
				try {
					builder.addInputStream(getPath(url), is);
				} finally {
					is.close();
				}
			}
			builder.enableDuplicateFiltering();
			builder.deploy();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Unable to deploy bundle", e);
		}
	}

	/**
	 * remove bundle protocol specific part, so that resource can be accessed by
	 * path relative to bundle root
	 */
	private String getPath(URL url) {
		String path = url.toExternalForm();
		return path.replaceAll("bundle://[^/]*/", "");
	}

}
