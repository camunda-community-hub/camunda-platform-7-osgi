package org.camunda.bpm.extension.osgi.configadmin.impl;

/**
 * @author Ronny Bräunlich
 */
public enum ProcessEngineConfigurationProperties {

		AUTHORIZATION_ENABLED("authorizationEnabled") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setAuthorizationEnabled(Boolean.valueOf(property));
				}
		},

		AUTO_STORE_SCRIPT_VARIABLES("autoStoreScriptVariables") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setAutoStoreScriptVariables(Boolean.valueOf(property));
				}
		},

		CMMN_ENABLED("cmmnEnabled") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setCmmnEnabled(Boolean.valueOf(property));
				}
		},
		CREATE_DIAGRAM_ON_DEPLOY("createDiagramOnDeploy") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setCreateDiagramOnDeploy(Boolean.valueOf(property));
				}
		},
		CREATE_INCIDENT_ON_FAIL("createIncidentOnFail") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setCreateIncidentOnFailedJobEnabled(Boolean.valueOf(property));
				}
		},
		DATABASE_SCHEMA("databaseSchema") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDatabaseSchema(String.valueOf(property));
				}
		},
		DATABASE_SCHEMA_UPDATE("databaseSchemaUpdate") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDatabaseSchemaUpdate(String.valueOf(property));
				}
		},
		DATABASE_TABLE_PREFIX("databaseTablePrefix") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDatabaseTablePrefix(String.valueOf(property));
				}
		},
		DATABASE_TYPE("databaseType") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDatabaseType(String.valueOf(property));
				}
		},
		DATA_SOURCE_JNDI_NAME("dataSourceJndiName") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDataSourceJndiName(String.valueOf(property));
				}
		},
		DB_ENTITY_CACHE_REUSE_ENABLED("dbEntityCacheReuseEnabled") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDbEntityCacheReuseEnabled(Boolean.valueOf(property));
				}
		},
		DB_HISTORY_USED("dbHistoryUsed") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDbHistoryUsed(Boolean.valueOf(property));
				}
		},
		DB_IDENTITY_USED("dbIdentityUsed") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDbIdentityUsed(Boolean.valueOf(property));
				}
		},
		DEFAULT_CHARSET_NAME("defaultCharsetName") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDefaultCharsetName(String.valueOf(property));
				}
		},
		DEFAULT_NUMBER_OF_RETRIES("defaultNumberOfRetries") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDefaultNumberOfRetries(Integer.valueOf(property));
				}
		},
		DEFAULT_SERIALIZATION_FORMAT("defaultSerializationFormat") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDefaultSerializationFormat(String.valueOf(property));
				}
		},
		DEPLOYMENT_LOCK_USED("deploymentLockUsed") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setDeploymentLockUsed(Boolean.valueOf(property));
				}
		},
		ENABLE_SCRIPT_COMPILATION("enableScriptCompilation") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setEnableScriptCompilation(Boolean.valueOf(property));
				}
		},
		EXECUTION_TREE_PREFETCH_ENABLED("executionTreePrefetchEnabled") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setExecutionTreePrefetchEnabled(Boolean.valueOf(property));
				}
		},
		HINT_JOB_EXECUTOR("hintJobExecutor") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setHintJobExecutor(Boolean.valueOf(property));
				}
		},
		HISTORY("history") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setHistory(String.valueOf(property));
				}
		},
		ID_BLOCK_SIZE("idBlockSize") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setIdBlockSize(Integer.valueOf(property));
				}
		},
		ID_GENERATOR_DATASOURCE_NAME("idGeneratorDataSourceJndiName") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setIdGeneratorDataSourceJndiName(property);
				}
		},
		INVOKE_CUSTOM_VARIABLE_LISTENERS("invokeCustomVariableListeners") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setInvokeCustomVariableListeners(Boolean.valueOf(property));
				}
		},
		JDBC_DRIVER("jdbcDriver") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcDriver(String.valueOf(property));
				}
		},

		JDBC_MAX_ACTIVE_CONNECTIONS("jdbcMaxActiveConnections") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcMaxActiveConnections(Integer.valueOf(property));
				}
		},

		JDBC_MAX_CHECKOUT_TIME("jdbcMaxCheckoutTime") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcMaxCheckoutTime(Integer.valueOf(property));
				}
		},

		JDBC_MAX_IDLE_CONNECTIONS("jdbcMaxIdleConnections") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcMaxIdleConnections(Integer.valueOf(property));
				}
		},

		JDBC_MAX_WAIT_TIME("jdbcMaxWaitTime") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcMaxWaitTime(Integer.valueOf(property));
				}
		},

		JDBC_PASSWORD("jdbcPassword") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcPassword(String.valueOf(property));
				}
		},

		JDBC_PING_CONNECTION_NOT_USED_FOR("jdbcPingConnectionNotUsedFor") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcPingConnectionNotUsedFor(Integer.valueOf(property));
				}
		},

		JDBC_PING_ENABLED("jdbcPingEnabled") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcPingEnabled(Boolean.valueOf(property));
				}
		},

		JDBC_PING_QUERY("jdbcPingQuery") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcPingQuery(property);
				}
		},

		JDBC_URL("jdbcUrl") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcUrl(String.valueOf(property));
				}
		},

		JDBC_USERNAME("jdbcUsername") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJdbcUsername(String.valueOf(property));
				}
		},

		JOB_EXECUTOR_ACTIVATE("jobExecutorActivate") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJobExecutorActivate(Boolean.valueOf(property));
				}
		},

		JOB_EXECUTOR_DEPLOYMENT_AWARE("jobExecutorDeploymentAware") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJobExecutorDeploymentAware(Boolean.valueOf(property));
				}
		},

		JPA_CLOSE_ENTITY_MANAGER("jpaCloseEntityManager") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJpaCloseEntityManager(Boolean.valueOf(property));
				}
		},

		JPA_HANDLE_TRANSACTION("jpaHandleTransaction") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJpaHandleTransaction(Boolean.valueOf(property));
				}
		},

		JPA_PERSISTENCE_UNIT_NAME("jpaPersistenceUnitName") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setJpaPersistenceUnitName(property);
				}
		},

		MAIL_SERVER_DEFAULT_FROM("mailServerDefaultFrom") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setMailServerDefaultFrom(property);
				}
		},

		MAIL_SERVER_HOST("mailServerHost") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setMailServerHost(property);
				}
		},

		MAIL_SERVER_PASSWORD("mailServerPassword") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setMailServerPassword(property);
				}
		},

		MAIL_SERVER_PORT("mailServerPort") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setMailServerPort(Integer.valueOf(property));
				}
		},

		MAIL_SERVER_USERNAME("mailServerUsername") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setMailServerUsername(property);
				}
		},

		MAIL_SERVER_USE_TLS("mailServerUseTLS") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setMailServerUseTLS(Boolean.valueOf(property));
				}
		},

		PROCESS_ENGINE_NAME("processEngineName") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setProcessEngineName(String.valueOf(property));
				}
		},

		TRANSACTION_EXTERNALLY_MANAGED("transactionExternallyManaged") {
				@Override
				public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property) {
						config.setTransactionsExternallyManaged(Boolean.valueOf(property));
				}
		};

		private final String propertyKey;


		ProcessEngineConfigurationProperties(String propertyKey) {
				this.propertyKey = propertyKey;
		}

		abstract public void setPropertyOnConfiguration(ProcessEngineConfigurationFromProperties config, String property);

		public static ProcessEngineConfigurationProperties getPropertyByKey(String key) {
				for (ProcessEngineConfigurationProperties prop : ProcessEngineConfigurationProperties.values()) {
						if (prop.propertyKey.equals(key)) {
								return prop;
						}
				}
				return null;
		}
}
