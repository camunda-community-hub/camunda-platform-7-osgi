# OSGi integration for camunda BPM platform

camunda BPM community extension providing support for camunda BPM platform inside OSGi containers

```
Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: camunda BPM Platform OSGi
Bundle-SymbolicName: org.camunda.bpm.extension.osgi
Bundle-Version: 
Export-Package: [...]
Import-Package: [...]
```

## Get started

### Part 1 starting the camunda BPM OSGi bundle

Before you start you have to install all the required bundles into your OSGi runtime.
To see a list of the required bundles you can take a look at the Apache Karaf feature.xml.
It contains a list of the required dependencies and a list containing all the optional dependencies, too.

After you've done that you can drop the camunda-bpm-osgi bundle into the runtime.
It should then move to the resolved state and you could start it.

If you prefer to use Apache Karaf as your runtime you can use the feature.xml directly. The guide can be found [here](https://github.com/camunda/camunda-bpm-platform-osgi/blob/master/camunda-engine-karaf-feature/README.md).

### Part 2 creating a process engine

After you successfully deployed the camunda BPM OSGi bundle your next step is to create a ProcessEngine.

#### Using the ProcessEngineFactory

To help a little bit with the creating of a process engine you can use the `ProcessEngineFactory` class. You'll have to pass a `ProcessEngineConfiguration` object and the current bundle to it by calling the setBundle() and setProcessEngineConfiguration() methods. Finally you'll have to call init(). After that you may call getObject() to get a reference to the process engine.
Please be aware that the order is mandatory or else getObject() will return null.

Please note also, that the process engine won't be exported automatically. If you want to share it, you can do that by yourself.

If you want to use a special ELResolver (see part 4) you'll have to use the `ProcessEngineFactoryWithELResolver`.

#### Using the camunda BPM Blueprint wrapper (deprecated)

There is already a project with a pre-filled Blueprint context.xml. Basically you'll only have to edit the Datasource properties or you use the pre-defined in memory H2 database.

This approach uses the deprecated `ConfigurationFactory` right now, so please be careful. To see the reason why the `ConfigurationFactory` is deprecated see [here](https://groups.google.com/forum/#!topic/camunda-bpm-dev/toZEYMzUJpQ)

If your Blueprint implementation supports non-void setters you can replace the `ConfigurationFactory` by directly configuring a `StandaloneProcessEngineConfiguration`. 

#### Old school

If you wanna stay old school and use core OSGi you can do that, too.
Import the package `org.camunda.bpm.engine` and `org.camunda.bpm.engine.impl.cfg` and instantiate your own `StandaloneProcessEngineConfiguration`.

#### Using the ProcessApplication API

You can also use the [ProcessApplication API](http://docs.camunda.org/latest/guides/user-guide/#process-applications) in camunda BPM OSGi. This only requires you to use Blueprint.
Simple create a subclass of `OSGiProcessApplication`, pass the bundle and the BlueprintContext in the constructor and export it as OSGi service (`interface=ProcessApplicationInterface`).
You can configure your `OSGiProcessApplication` just like a normal ProcessApplication via processes.xml. The engine will be automatically exported as a service for others to use it.

### Part 3 Deploying process definitions

After you created a `ProcessEngine` you can start to deploy process definitions.
The following steps only work when you exported a `ProcessEngine` as OSGi service.

#### Inside a bundle

When you deploy a bundle containing a process definition the process can be automatically added to the ProcessEngine.
For the process definition to be found, you'll have to do one of the following things:
- place it in the OSGI-INF/processes/ directory
- set the "Process-Definitions" header in the MANIFEST.MF and let it point to a file or directory

If you reference any `JavaDelegate`s or `ActivityBehavior`s from within your process defniition please take a look at Part 4

#### BPMN-XML file

If your OSGi runtime supports Apache Felix Fileinstall you can drop a single process definition in the directory watched by Fileinstall. It will be parsed and automatically transformed into an OSGi bundle.

#### ProcessApplication API

Like mentioned in the [User Guide](http://docs.camunda.org/latest/guides/user-guide/#process-applications) you can deploy the processes using the processes.xml. Alternatively you can do it manually inside your OSGiProcessApplication class in the createDeployment() method.

### Part 4 referencing inside processes

#### With the BlueprintELResolver

The `BlueprintELResolver` can be used with `JavaDelegates`. You'll have to use the `BlueprintELResolver` as ELResolver and register it to listen for `JavaDelegates`.
If you use the camunda BPM Blueprint wrapper this will be done for you automatically.
The `BlueprintELResolver` then tries to match the expression with the Blueprint component name (the id in the context.xml).

#### With the OSGiELResolver

The `OSGiELResolver` uses a three step resolution to match expressions with classes

##### Step 1

The first step uses the LDAP filter property. You have to export a service with the filter property "processExpression=". The OSGiELResolver then matches the expression with the filter.

##### Step 2

If the LDAP search doesn't suceed the ELResolver will search the Service Registry for JavaDelegates. Then the class name will be compared to the expression. The comparison is similiar to the default CDI bean names. That means the expression has to match the class name starting with a lowercase character, e.g. org.foo.bar.MyClass would match "${myClass}".

##### Step 3

The third steps works likes the second one, only that it searches for exported ActivityBehaviours. 

#### ProcessApplication API

If you used a ProcessApplication to start you engine and deploy your processes the EL resolution is limited to beans in your context.xml (that's why you need Blueprint). Every bean will be matched by its id in the context.xml. The `BlueprintBundleLocalELResolver` won't find any classes/services outside of it. 

## Resources

* [Issue Tracker](https://github.com/camunda/camunda-bpm-platform-osgi/issues)
* [Contributing](https://github.com/camunda/camunda-bpm-platform-osgi/blob/master/CONTRIBUTING.md)


## Roadmap

_a short list of things that yet need to be done (until we organize it elsewhere ;) )_

**todo**
- adapt Process Application API for OSGi
- camunda webapp WAB (cockpit, tasklist, admin)
- create example for configuring engine using PAX-CDI

**done**
- QA, integration tests (resolve engine-bundle)
- example for configuring engine using Blueprint


## Maintainers:

* [@rbraeunlich ](https://github.com/rbraeunlich)

## License

Apache License, Version 2.0
