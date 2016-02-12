package org.camunda.bpm.extension.osgi.blueprint;

import javax.sql.DataSource;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;

/**
 * @deprecated this class is only needed if the Enterprise OSGi framework
 *             doesn't support non void setters. You should create your
 *             {@link StandaloneProcessEngineConfiguration} somewhere else and
 *             pass it to the engine.
 */
public class ConfigurationFactory {

	DataSource dataSource;
	String databaseSchemaUpdate;
	boolean jobExecutorActivate = true;

	public StandaloneProcessEngineConfiguration getConfiguration() {
		StandaloneProcessEngineConfiguration conf = new StandaloneProcessEngineConfiguration();
		conf.setDataSource(dataSource);
		conf.setDatabaseSchemaUpdate(databaseSchemaUpdate);
		conf.setJobExecutorActivate(jobExecutorActivate);
		return conf;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDatabaseSchemaUpdate(String databaseSchemaUpdate) {
		this.databaseSchemaUpdate = databaseSchemaUpdate;
	}

	public void setJobExecutorActivate(boolean jobExecutorActivate) {
		this.jobExecutorActivate = jobExecutorActivate;
	}
}
