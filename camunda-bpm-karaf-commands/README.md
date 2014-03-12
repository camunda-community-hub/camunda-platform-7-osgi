This project contains an extension to the Apache Karaf runtime environment which activate additional camunda related commands.

With the commands you can query data from the process engine of the actual Karaf container. For example you can:

* list of deployments
* list of process definitions
* list of historical process instances/runs
* list of activities/steps of a historical process instance/run

For more information about the Kafar console check the [official documetation](http://karaf.apache.org/manual/latest/users-guide/console.html)..

## Installation

To install from the (local or remote) maven repository, simple use the ```install``` karaf command:

```
instal -s mvn:org.camunda.bpm.extension.osgi/camunda-engine-karaf-commands/VERSION
```

Where VERSION string should be replaced with the latest version. For example:

```
install -s mvn:org.camunda.bpm.extension.osgi/camunda-engine-karaf-commands/1.0.0-SNAPSHOT
```

## Usage

If the bundle is installed (and started) you can see the new camunda commands from the console:

```
karaf@default> help
COMMANDS
	...
	camunda:activity-list             List activities.
        camunda:deployments-list          List camunda deployments.
        camunda:execution-list            List process executions by process definition.
        camunda:export-diagram            Export definition of a diagram
        camunda:instance-list             List process instances of a specific process definition.
        camunda:process-list              List process definitions.
        camunda:task-list                 List tasks for a specific process definition.
        camunda:variable-list             List variables on a specific instance.

	...
```

Now you can run the commands to display information and check the state of the current processEngine. For example:

```
karaf@default> camunda:deployments-list 
+----+-------------------+------------------------------+
| ID |        NAME       |        DEPLOYMENT_TIME       |
+----+-------------------+------------------------------+
| 1  | hello-world-proj1 | Thu Feb 06 10:43:46 CET 2014 |
+----+-------------------+------------------------------+

karaf@default> camunda:process-list

 Deployment 1 hello-world-proj1 6 Feb 2014 09:43:46 GMT
+---------------+-----------+--------------+------------+--------------------------+
|       ID      |    KEY    |     NAME     | DEPLOYMENT |         CATEGORY         |
+---------------+-----------+--------------+------------+--------------------------+
| Process_1:1:3 | Process_1 | Test-Process | 1          | http://activiti.org/bpmn |
+---------------+-----------+--------------+------------+--------------------------+
```
