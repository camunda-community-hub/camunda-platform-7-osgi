package org.camunda.bpm.extension.osgi.configadmin.impl;

import static org.camunda.bpm.extension.osgi.configadmin.impl.ProcessEngineConfigurationProperties.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;

public class ProcessEngineConfigurationPropertiesTest {

  private static final String TRUE = "true";
  private static final String FALSE = "false";
  private ProcessEngineConfigurationFromProperties config;

  @Before
  public void setUp() {
    config = new ProcessEngineConfigurationFromProperties();
  }

  @Test
  public void setAuthorizationEnabled() {
    AUTHORIZATION_ENABLED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isAuthorizationEnabled(), is(true));
  }

  @Test
  public void setAuthorizationDisabled() {
    AUTHORIZATION_ENABLED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isAuthorizationEnabled(), is(false));
  }

  @Test
  public void setAutoStoreVariablesEnabled() {
    AUTO_STORE_SCRIPT_VARIABLES.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isAutoStoreScriptVariables(), is(true));
  }

  @Test
  public void setAutoStoreVariablesDisabled() {
    AUTO_STORE_SCRIPT_VARIABLES.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isAutoStoreScriptVariables(), is(false));
  }

  @Test
  public void setCmmnEnabled() {
    CMMN_ENABLED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isCmmnEnabled(), is(true));
  }

  @Test
  public void setCmmnDisabled() {
    CMMN_ENABLED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isCmmnEnabled(), is(false));
  }

  @Test
  public void setCreateDiagramOnDeployEnabled() {
    CREATE_DIAGRAM_ON_DEPLOY.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isCreateDiagramOnDeploy(), is(true));
  }

  @Test
  public void setCreateDiagramOnDeployDisabled() {
    CREATE_DIAGRAM_ON_DEPLOY.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isCreateDiagramOnDeploy(), is(false));
  }

  @Test
  public void setCreateIncidentOnFailEnabled() {
    CREATE_INCIDENT_ON_FAIL.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isCreateIncidentOnFailedJobEnabled(), is(true));
  }

  @Test
  public void setCreateIncidentOnFailDisabled() {
    CREATE_INCIDENT_ON_FAIL.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isCreateIncidentOnFailedJobEnabled(), is(false));
  }

  @Test
  public void setDatasourceJndiName() {
    String name = "foobar";
    DATA_SOURCE_JNDI_NAME.setPropertyOnConfiguration(config, name);

    assertThat(config.getDataSourceJndiName(), is(name));
  }

  @Test
  public void setDatabaseSchema() {
    String schema = "h2";
    DATABASE_SCHEMA.setPropertyOnConfiguration(config, schema);

    assertThat(config.getDatabaseSchema(), is(schema));
  }

  @Test
  public void setDatabaseSchemaUpdate() {
    String update = "create";
    DATABASE_SCHEMA_UPDATE.setPropertyOnConfiguration(config, update);

    assertThat(config.getDatabaseSchemaUpdate(), is(update));
  }

  @Test
  public void setDatabaseTablePrefix() {
    String prefix = "abc";
    DATABASE_TABLE_PREFIX.setPropertyOnConfiguration(config, prefix);

    assertThat(config.getDatabaseTablePrefix(), is(prefix));
  }

  @Test
  public void setDatabaseType() {
    String type = "H-Base";
    DATABASE_TYPE.setPropertyOnConfiguration(config, type);

    assertThat(config.getDatabaseType(), is(type));
  }

  @Test
  public void setDbEntityCacheReuseEnabled() {
    DB_ENTITY_CACHE_REUSE_ENABLED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isDbEntityCacheReuseEnabled(), is(true));
  }

  @Test
  public void setDbEntityCacheReuseDisabled() {
    DB_ENTITY_CACHE_REUSE_ENABLED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isDbEntityCacheReuseEnabled(), is(false));
  }

  @Test
  public void setDbHistoryUsedEnabled() {
    DB_HISTORY_USED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isDbHistoryUsed(), is(true));
  }

  @Test
  public void setDbHistoryUsedDisabled() {
    DB_HISTORY_USED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isDbHistoryUsed(), is(false));
  }

  @Test
  public void setDbIdentityUsedEnabled() {
    DB_IDENTITY_USED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isDbIdentityUsed(), is(true));
  }

  @Test
  public void setDbIdentityUsedDisabled() {
    DB_IDENTITY_USED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isDbIdentityUsed(), is(false));
  }

  @Test
  public void setDefaultCharsetName() {
    String charset = "ISO-8859-1";
    DEFAULT_CHARSET_NAME.setPropertyOnConfiguration(config, charset);
    JDBC_URL.setPropertyOnConfiguration(config, "jdbc:h2:mem:camunda");
    DATABASE_SCHEMA_UPDATE.setPropertyOnConfiguration(config, "true");
    // we have to build the engine or else the charset won't be initiated
    config.buildProcessEngine();

    assertThat(config.getDefaultCharset(), is(Charset.forName(charset)));
  }

  @Test
  public void setDefaultNumberOfRetries() {
    String number = String.valueOf(Integer.MAX_VALUE);
    DEFAULT_NUMBER_OF_RETRIES.setPropertyOnConfiguration(config, number);

    assertThat(config.getDefaultNumberOfRetries(), is(Integer.MAX_VALUE));
  }

  @Test
  public void setDefaultSerializationFormat() {
    String format = "json2";
    DEFAULT_SERIALIZATION_FORMAT.setPropertyOnConfiguration(config, format);

    assertThat(config.getDefaultSerializationFormat(), is(format));
  }

  @Test
  public void setDeploymentLockEnabled() {
    DEPLOYMENT_LOCK_USED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isDeploymentLockUsed(), is(true));
  }

  @Test
  public void setDeploymentLockDisabled() {
    DEPLOYMENT_LOCK_USED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isDeploymentLockUsed(), is(false));
  }

  @Test
  public void setScriptCompilationEnabled() {
    ENABLE_SCRIPT_COMPILATION.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isEnableScriptCompilation(), is(true));
  }

  @Test
  public void setScriptCompilationDisabled() {
    ENABLE_SCRIPT_COMPILATION.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isEnableScriptCompilation(), is(false));
  }

  @Test
  public void setExecutionTreePrefatchEnabled() {
    EXECUTION_TREE_PREFETCH_ENABLED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isExecutionTreePrefetchEnabled(), is(true));
  }

  @Test
  public void setExecutionTreePrefatchDisabled() {
    EXECUTION_TREE_PREFETCH_ENABLED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isExecutionTreePrefetchEnabled(), is(false));
  }

  @Test
  public void setHintJobExecutorEnabled() {
    HINT_JOB_EXECUTOR.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isHintJobExecutor(), is(true));
  }

  @Test
  public void setHintJobExecutorDisabled() {
    HINT_JOB_EXECUTOR.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isHintJobExecutor(), is(false));
  }

  @Test
  public void setHistory() {
    String history = "audit";
    HISTORY.setPropertyOnConfiguration(config, history);

    assertThat(config.getHistory(), is(history));
  }

  @Test
  public void setIdBlockSize() {
    String size = String.valueOf(Integer.MAX_VALUE);
    ID_BLOCK_SIZE.setPropertyOnConfiguration(config, size);

    assertThat(config.getIdBlockSize(), is(Integer.MAX_VALUE));
  }

  @Test
  public void setIdGeneratorDatasourceName() {
    String name = "name";
    ID_GENERATOR_DATASOURCE_NAME.setPropertyOnConfiguration(config, name);

    assertThat(config.getIdGeneratorDataSourceJndiName(), is(name));
  }

  @Test
  public void setInvokeCustomVariableListenersEnabled() {
    INVOKE_CUSTOM_VARIABLE_LISTENERS.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isInvokeCustomVariableListeners(), is(true));
  }

  @Test
  public void setInvokeCustomVariableListenersDisabled() {
    INVOKE_CUSTOM_VARIABLE_LISTENERS.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isInvokeCustomVariableListeners(), is(false));
  }

  @Test
  public void setJdbcDriver() {
    String driver = "H-Base";
    JDBC_DRIVER.setPropertyOnConfiguration(config, driver);

    assertThat(config.getJdbcDriver(), is(driver));
  }

  @Test
  public void setJdbcMaxActiveConnections() {
    String max = String.valueOf(Integer.MAX_VALUE);
    JDBC_MAX_ACTIVE_CONNECTIONS.setPropertyOnConfiguration(config, max);

    assertThat(config.getJdbcMaxActiveConnections(), is(Integer.MAX_VALUE));
  }

  @Test
  public void setJdbcMaxCheckoutTime() {
    String time = String.valueOf(Integer.MAX_VALUE);
    JDBC_MAX_CHECKOUT_TIME.setPropertyOnConfiguration(config, time);

    assertThat(config.getJdbcMaxCheckoutTime(), is(Integer.MAX_VALUE));
  }

  @Test
  public void setJdbcMaxIdleConnections() {
    String max = String.valueOf(Integer.MAX_VALUE);
    JDBC_MAX_IDLE_CONNECTIONS.setPropertyOnConfiguration(config, max);

    assertThat(config.getJdbcMaxIdleConnections(), is(Integer.MAX_VALUE));
  }

  @Test
  public void setJdbcMaxWaitTime() {
    String max = String.valueOf(Integer.MAX_VALUE);
    JDBC_MAX_WAIT_TIME.setPropertyOnConfiguration(config, max);

    assertThat(config.getJdbcMaxWaitTime(), is(Integer.MAX_VALUE));
  }

  @Test
  public void setJdbcPassword() {
    String password = "secret";
    JDBC_PASSWORD.setPropertyOnConfiguration(config, password);

    assertThat(config.getJdbcPassword(), is(password));
  }

  @Test
  public void setJdbcPingConnectionNotUsedFor() {
    String notUsedFor = String.valueOf(Integer.MAX_VALUE);
    JDBC_PING_CONNECTION_NOT_USED_FOR.setPropertyOnConfiguration(config, notUsedFor);

    assertThat(config.getJdbcPingConnectionNotUsedFor(), is(Integer.MAX_VALUE));
  }

  @Test
  public void setJdbcPingEnabled() {
    JDBC_PING_ENABLED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isJdbcPingEnabled(), is(true));
  }

  @Test
  public void setJdbcPingDisabled() {
    JDBC_PING_ENABLED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isJdbcPingEnabled(), is(false));
  }

  @Test
  public void setJdbcPingQuery() {
    String query = "SELECT ping FROM table";
    JDBC_PING_QUERY.setPropertyOnConfiguration(config, query);

    assertThat(config.getJdbcPingQuery(), is(query));
  }

  @Test
  public void setJdbcUrl() {
    String url = "jdbc:h2:mem:camunda";
    JDBC_URL.setPropertyOnConfiguration(config, url);

    assertThat(config.getJdbcUrl(), is(url));
  }

  @Test
  public void setJdbcUsername() {
    String username = "name";
    JDBC_USERNAME.setPropertyOnConfiguration(config, username);

    assertThat(config.getJdbcUsername(), is(username));
  }

  @Test
  public void setJobExecutorActivateEnabled() {
    JOB_EXECUTOR_ACTIVATE.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isJobExecutorActivate(), is(true));
  }

  @Test
  public void setJobExecutorActivateDisabled() {
    JOB_EXECUTOR_ACTIVATE.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isJobExecutorActivate(), is(false));
  }

  @Test
  public void setJobExecutorDeploymentAwareEnabled() {
    JOB_EXECUTOR_DEPLOYMENT_AWARE.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isJobExecutorDeploymentAware(), is(true));
  }

  @Test
  public void setJobExecutorDeploymentAwareDisabled() {
    JOB_EXECUTOR_DEPLOYMENT_AWARE.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isJobExecutorDeploymentAware(), is(false));
  }

  @Test
  public void setJpaCloseEntityManagerEnabled() {
    JPA_CLOSE_ENTITY_MANAGER.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isJpaCloseEntityManager(), is(true));
  }

  @Test
  public void setJpaCloseEntityManagerDisabled() {
    JPA_CLOSE_ENTITY_MANAGER.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isJpaCloseEntityManager(), is(false));
  }

  @Test
  public void setJpaHandleTransactionEnabled() {
    JPA_HANDLE_TRANSACTION.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isJpaHandleTransaction(), is(true));
  }

  @Test
  public void setJpaHandleTransactionDisabled() {
    JPA_HANDLE_TRANSACTION.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isJpaHandleTransaction(), is(false));
  }

  @Test
  public void setJpaPersistenceUnitName() {
    String jpaName = "datasource";
    JPA_PERSISTENCE_UNIT_NAME.setPropertyOnConfiguration(config, jpaName);

    assertThat(config.getJpaPersistenceUnitName(), is(jpaName));
  }

  @Test
  public void setMailServerDefaultFrom() {
    String from = "test@foo.bar";
    MAIL_SERVER_DEFAULT_FROM.setPropertyOnConfiguration(config, from);

    assertThat(config.getMailServerDefaultFrom(), is(from));
  }

  @Test
  public void setMailServerHost() {
    String host = "localhost";
    MAIL_SERVER_HOST.setPropertyOnConfiguration(config, host);

    assertThat(config.getMailServerHost(), is(host));
  }

  @Test
  public void setMailServerPassword() {
    String password = "secret";
    MAIL_SERVER_PASSWORD.setPropertyOnConfiguration(config, password);

    assertThat(config.getMailServerPassword(), is(password));
  }

  @Test
  public void setMailServerPort() {
    String port = "8080";
    MAIL_SERVER_PORT.setPropertyOnConfiguration(config, port);

    assertThat(config.getMailServerPort(), is(Integer.parseInt(port)));
  }

  @Test
  public void setMailServerUseTLSEnabled() {
    MAIL_SERVER_USE_TLS.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.getMailServerUseTLS(), is(true));
  }

  @Test
  public void setMailServerUseTLSDisabled() {
    MAIL_SERVER_USE_TLS.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.getMailServerUseTLS(), is(false));
  }

  @Test
  public void setMailServerUsername() {
    String username = "user";
    MAIL_SERVER_USERNAME.setPropertyOnConfiguration(config, username);

    assertThat(config.getMailServerUsername(), is(username));
  }

  @Test
  public void setProcessEngineName() {
    String name = "engine";
    PROCESS_ENGINE_NAME.setPropertyOnConfiguration(config, name);

    assertThat(config.getProcessEngineName(), is(name));
  }

  @Test
  public void setTransactionExternallyManagedEnabled() {
    TRANSACTION_EXTERNALLY_MANAGED.setPropertyOnConfiguration(config, TRUE);

    assertThat(config.isTransactionsExternallyManaged(), is(true));
  }

  @Test
  public void setTransactionExternallyManagedDisabled() {
    TRANSACTION_EXTERNALLY_MANAGED.setPropertyOnConfiguration(config, FALSE);

    assertThat(config.isTransactionsExternallyManaged(), is(false));
  }
}
