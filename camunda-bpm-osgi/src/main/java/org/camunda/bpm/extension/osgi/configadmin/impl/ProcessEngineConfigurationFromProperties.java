package org.camunda.bpm.extension.osgi.configadmin.impl;

import java.util.Collections;
import java.util.Dictionary;
import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;

public class ProcessEngineConfigurationFromProperties extends StandaloneProcessEngineConfiguration {

  private static final String AUTHORIZATION_ENABLED = "authorizationEnabled";
  private static final String AUTO_STORE_SCRIPT_VARIABLES = "autoStoreScriptVariables";
  private static final String CMMN_ENABLED = "cmmnEnabled";
  private static final String CREATE_DIAGRAM_ON_DEPLOY = "createDiagramOnDeploy";
  private static final String CREATE_INCIDENT_ON_FAIL = "createIncidentOnFail";
  private static final String DATABASE_SCHEMA = "databaseSchema";
  private static final String DATABASE_SCHEMA_UPDATE = "databaseSchemaUpdate";
  private static final String DATABASE_TABLE_PREFIX = "databaseTablePrefix";
  private static final String DATABASE_TYPE = "databaseType";
  private static final String DATA_SOURCE_JNDI_NAME = "dataSourceJndiName";
  private static final String DB_HISTORY_USED = "dbHistoryUsed";
  private static final String DB_IDENTITY_USED = "dbIdentityUsed";
  private static final String DEFAULT_CHARSET_NAME = "defaultCharsetName";
  private static final String DEFAULT_NUMBER_OF_RETRIES = "defaultNumberOfRetries";
  private static final String DEFAULT_SERIALIZATION_FORMAT = "defaultSerializationFormat";
  private static final String DEPLOYMENT_LOCK_USED = "deploymentLockUsed";
  private static final String ENABLE_SCRIPT_COMPILATION = "enableScriptCompilation";
  private static final String HISTORY = "history";
  private static final String ID_BLOCK_SIZE = "idBlockSize";
  private static final String JDBC_DRIVER = "jdbcDriver";
  private static final String JDBC_MAX_ACTIVE = "jdbcMaxActive";
  private static final String JDBC_PASSWORD = "jdbcPassword";
  private static final String JDBC_URL = "jdbcUrl";
  private static final String JDBC_USERNAME = "jdbcUsername";
  private static final String JOB_EXECUTOR_ACTIVATE = "jobExecutorActivate";
  private static final String JOB_EXECUTOR_DEPLOYMENT_AWARE = "jobExecutorDeploymentAware";
  private static final String JPA_CLOSE_ENTITY_MANAGER = "jpaCloseEntityManager";
  private static final String JPA_HANDLE_TRANSCATION = "jpaHandleTransaction";
  private static final String PROCESS_ENGINE_NAME = "processEngineName";

  public void configure(Dictionary<String, String> configuration) {
    for (String key : Collections.list(configuration.keys())) {
      if (key.equals(AUTHORIZATION_ENABLED)) {
        this.setAuthorizationEnabled(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(AUTO_STORE_SCRIPT_VARIABLES)) {
        this.setAutoStoreScriptVariables(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(CMMN_ENABLED)) {
        this.setCmmnEnabled(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(CREATE_DIAGRAM_ON_DEPLOY)) {
        this.setCreateDiagramOnDeploy(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(CREATE_INCIDENT_ON_FAIL)) {
        this.setCreateIncidentOnFailedJobEnabled(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(DATABASE_SCHEMA)) {
        this.setDatabaseSchema(String.valueOf(configuration.get(key)));
      } else if (key.equals(DATABASE_SCHEMA_UPDATE)) {
        this.setDatabaseSchemaUpdate(String.valueOf(configuration.get(key)));
      } else if (key.equals(DATABASE_TABLE_PREFIX)) {
        this.setDatabaseTablePrefix(String.valueOf(configuration.get(key)));
      } else if (key.equals(DATABASE_TYPE)) {
        this.setDatabaseType(String.valueOf(configuration.get(key)));
      } else if (key.equals(DATA_SOURCE_JNDI_NAME)) {
        this.setDataSourceJndiName(String.valueOf(configuration.get(key)));
      } else if (key.equals(DB_HISTORY_USED)) {
        this.setDbHistoryUsed(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(DB_IDENTITY_USED)) {
        this.setDbIdentityUsed(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(DEFAULT_CHARSET_NAME)) {
        this.setDefaultCharsetName(String.valueOf(configuration.get(key)));
      } else if (key.equals(DEFAULT_NUMBER_OF_RETRIES)) {
        this.setDefaultNumberOfRetries(Integer.valueOf(configuration.get(key)));
      } else if (key.equals(DEFAULT_SERIALIZATION_FORMAT)) {
        this.setDefaultSerializationFormat(String.valueOf(configuration.get(key)));
      } else if (key.equals(DEPLOYMENT_LOCK_USED)) {
        this.setDeploymentLockUsed(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(ENABLE_SCRIPT_COMPILATION)) {
        this.setEnableScriptCompilation(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(HISTORY)) {
        this.setHistory(String.valueOf(configuration.get(key)));
      } else if (key.equals(ID_BLOCK_SIZE)) {
        this.setIdBlockSize(Integer.valueOf(configuration.get(key)));
      } else if (key.equals(JDBC_DRIVER)) {
        this.setJdbcDriver(String.valueOf(configuration.get(key)));
      } else if (key.equals(JDBC_MAX_ACTIVE)) {
        this.setJdbcMaxActiveConnections(Integer.valueOf(configuration.get(key)));
      } else if (key.equals(JDBC_PASSWORD)) {
        this.setJdbcPassword(String.valueOf(configuration.get(key)));
      } else if (key.equals(JDBC_URL)) {
        this.setJdbcUrl(String.valueOf(configuration.get(key)));
      } else if (key.equals(JDBC_USERNAME)) {
        this.setJdbcUsername(String.valueOf(configuration.get(key)));
      } else if (key.equals(JOB_EXECUTOR_ACTIVATE)) {
        this.setJobExecutorActivate(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(JOB_EXECUTOR_DEPLOYMENT_AWARE)) {
        this.setJobExecutorDeploymentAware(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(JPA_CLOSE_ENTITY_MANAGER)) {
        this.setJpaCloseEntityManager(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(JPA_HANDLE_TRANSCATION)) {
        this.setJpaHandleTransaction(Boolean.valueOf(configuration.get(key)));
      } else if (key.equals(PROCESS_ENGINE_NAME)) {
        this.setProcessEngineName(String.valueOf(configuration.get(key)));
      } else {
        Logger.getLogger(this.getClass().getName()).info("Couldn't set value " + configuration.get(key) + " for " + key);
      }
    }
  }
}
