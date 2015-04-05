# camunda BPM OSGi ConfigAdmin

The [Configuration Admin](http://wiki.osgi.org/wiki/Configuration_Admin) helps to manage services' configuration and makes it possible to provide those two separately.
A `ManagedServiceFactory` implementation is provided by this module, so that you can use the Configuration Admin to create process engines.
The factory is registered with the PID `org.camunda.bpm.extension.osgi.configadmin.ManagedProcessEngineFactory`.
You only have to find a way to provide your configuration to the admin. Most OSGi runtimes provide a way to directly add a configuration file. An alternative approach would be to create a configuration bundle which contains the configuration.
The `BundleActivator`could look like this:
```java
public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        ServiceReference ref = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin admin = (ConfigurationAdmin) context.getService(ref);
        String pid = "org.camunda.bpm.extension.osgi.configadmin.ManagedProcessEngineFactory";
        Configuration configuration = admin.createFactoryConfiguration(pid, null);
        Hashtable properties = new Hashtable();
        properties.put("databaseSchemaUpdate","false");
        properties.put("jobExecutorActivate","true");
        properties.put("processEngineName","TestEngine");
        properties.put("databaseType","mysql");
        properties.put("dataSourceJndiName", "osgi:service/jdbc/test");
        configuration.update(properties);
    }
```
Currently, the factory supports all primitive values that you can set on the `ProcessEngineConfiguration`.
The key is the name of the according set method with the "set" and starting with a lowercase letter.

A good starting point is the example from the Apache Felix project. See [here](http://felix.apache.org/documentation/subprojects/apache-felix-config-admin.html).
