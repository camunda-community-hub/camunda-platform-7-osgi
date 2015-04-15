package org.camunda.bpm.extension.osgi.configadmin.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;

public class ProcessEngineConfigurationFromPropertiesTest {

  @Test
  public void configureNonExistingProperty() {
    ProcessEngineConfigurationFromProperties config = new ProcessEngineConfigurationFromProperties();
    Dictionary<String, String> props = new Hashtable<String, String>();
    props.put("Foo", "bar");
    config.configure(props);
    // since we can't add a listener to the logger we only can test that we
    // don't crash
  }

  @Test
  public void configureExistingProperty() {
    ProcessEngineConfigurationFromProperties config = new ProcessEngineConfigurationFromProperties();
    Dictionary<String, String> props = new Hashtable<String, String>();
    String username = "bar";
    // just to be sure for future changes
    assertThat(config.getJdbcUsername(), is(not(username)));
    props.put("jdbcUsername", username);
    config.configure(props);

    assertThat(config.getJdbcUsername(), is(username));
  }

}
