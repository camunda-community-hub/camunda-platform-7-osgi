# Camunda BPM - OSGi Process Application Integration

This module allows you to use the [Process Application mechanism](http://docs.camunda.org/7.3/guides/user-guide/#process-applications) in an OSGi environment. To use the module you have to use [Blueprint](http://wiki.osgi.org/wiki/Blueprint).

## Creating a process engine

Simply create a subclass of `OSGiProcessApplication`, pass the bundle and the BlueprintContext in the constructor and export it as OSGi service (`interface=ProcessApplicationInterface`).
You can configure your `OSGiProcessApplication` just like a normal ProcessApplication via processes.xml. The engine will be automatically exported as a service for others to use it.

## Deploying process definitions

Like mentioned in the [User Guide](http://docs.camunda.org/latest/guides/user-guide/#process-applications) you can deploy the processes using the processes.xml. Alternatively you can do it manually inside your OSGiProcessApplication class in the `createDeployment()` method.

## Referencing inside processes

If you used a ProcessApplication to start you engine and deploy your processes the EL resolution is limited to beans in your context.xml (that's why you need Blueprint). Every bean will be matched by its id in the context.xml. The `BlueprintBundleLocalELResolver` won't find any classes/services outside of it.
