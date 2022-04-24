# OSGi integration for Camunda Platform 7

[![Build Status](https://travis-ci.org/camunda/camunda-bpm-platform-osgi.svg?branch=master)](https://travis-ci.org/camunda/camunda-bpm-platform-osgi)
[![Community Extension Badge](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
[![Lifecycle: Abandoned Badge](https://img.shields.io/badge/Lifecycle-Abandoned-lightgrey)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#abandoned-)
[![Lifecycle: Needs Maintainer](https://img.shields.io/badge/Lifecycle-Needs%20Maintainer%20-ff69b4)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#abandoned-)
![Compatible with: Camunda Platform](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%207-26d07c)

Camunda Platform community extension providing support for Camunda Platform inside OSGi containers

```
Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: camunda BPM Platform OSGi
Bundle-SymbolicName: org.camunda.bpm.extension.osgi
Bundle-Version: 
Export-Package: [...]
Import-Package: [...]
```

## Project structure

Every module is supposed to fullfil a single purpose and to be used independently of the others.
Three modules are an exception from this rule, namely
- camunda-bpm-karaf-assembly, which is used to provide a Karaf distribution that you can download [here](https://github.com/camunda/camunda-bpm-platform-osgi/releases)
- camunda-bpm-osgi-itest, which contains all the integration tests
- camunda-bpm-osgi, which contains some core components and is the only one required by all the modules

## Compatability Matrix

This matrix shows the compatibilities of the different camunda OSGi version and camunda BPM platform versions.
The information is based on the MANIFEST files and not directly based on binary compatibility.

| camunda OSGi // camunda BPM  |  7.1.x  |  7.2.x  |  7.3.x  |  7.4.x  |  7.5.x  |  7.6.x  |  7.7.x  |  7.8.x  |  7.9.x  |  7.10.x |
|-----------------------------:|:-------:|:-------:|:-------:|:-------:|:-------:|:-------:|:-------:|:-------:|:-------:|:--------:
|                       1.0.0  |    X    |         |         |         |         |         |         |         |         |         |
|                       1.1.0  |         |    X    |         |         |         |         |         |         |         |         |
|                       1.2.0  |         |         |    X    |         |         |         |         |         |         |         |
|                       1.3.0  |         |         |         |    X    |         |         |         |         |         |         |
|                       2.0.0  |         |         |         |         |    X    |         |         |         |         |         |
|                       3.0.0  |         |         |         |         |         |    X    |         |         |         |         |
|                       4.x.0  |         |         |         |         |         |         |    X    |         |         |         |
|                       5.0.0  |         |         |         |         |         |         |         |         |    X    |         |
|                       5.1.0  |         |         |         |         |         |    X    |    X    |    X    |    X    |    X    |

Starting with version 5.1.0 the versioning scheme changed slightly. Like the 1.x.0 version, an update of the camunda BPM platform is back to being a minor version increase.
Additionally, as long as there are no binary incompatibilities camunda OSGi is kept backwards compatible as much as possible.

For more detailed descriptions please check the release notes of the individual releases.

## Get started

### Part 1 starting the camunda BPM OSGi bundle

Before you start you have to install all the required bundles into your OSGi runtime.
To see a list of the required bundles you can take a look at the Apache Karaf feature.xml.
It contains a list of the required dependencies and a list containing all the optional dependencies, too.

After you've done that you can drop the camunda-bpm-osgi bundle into the runtime.
It should then move to the resolved state and you could start it.

If you prefer to use Apache Karaf as your runtime you can use the feature.xml directly. The guide can be found [here](https://github.com/camunda/camunda-bpm-platform-osgi/blob/master/camunda-bpm-karaf-feature/README.md).

### Part 2 creating a process engine

After you successfully deployed the camunda BPM OSGi bundle your next step is to create a ProcessEngine.

#### Using the ProcessEngineFactory

To help a little bit with the creating of a process engine you can use the `ProcessEngineFactory` class. You'll have to pass a `ProcessEngineConfiguration` object and the current bundle to it by calling the setBundle() and setProcessEngineConfiguration() methods. Finally you'll have to call init(). After that you may call getObject() to get a reference to the process engine.
Please be aware that the order is mandatory or else getObject() will return null.

Please note also, that the process engine won't be exported automatically. If you want to share it, you can do that by yourself.

If you want to use a special ELResolver (see part 4) you'll have to use the `ProcessEngineFactoryWithELResolver`.

#### Using Blueprint

See [camunda BPM OSGi - Blueprint Wrapper](camunda-bpm-blueprint-wrapper)

#### Old school

If you wanna stay old school and use core OSGi you can do that, too.
Import the package `org.camunda.bpm.engine` and `org.camunda.bpm.engine.impl.cfg` and instantiate your own `StandaloneProcessEngineConfiguration`.

#### Using the ProcessApplication API

See [camunda BPMN OSGi - Process Application Integration](camunda-bpm-osgi-processapplication)

#### Using the ConfigurationAdmin service

See [camunda BPM OSGi ConfigAdmin](camunda-bpm-osgi-configadmin).

### Part 3 Deploying process definitions

After you created a `ProcessEngine` you can start to deploy process definitions.
The following steps only work when you exported a `ProcessEngine` as OSGi service.

#### Inside a bundle

When you deploy a bundle containing a process definition the process can be automatically added to the ProcessEngine.
For the process definition to be found, you'll have to do one of the following things:
- place it in the OSGI-INF/processes/ directory
- set the "Process-Definitions" header in the MANIFEST.MF and let it point to a file or directory

If you reference any `JavaDelegate`s or `ActivityBehavior`s from within your process defniition please take a look at Part 4

#### ProcessApplication API

See [camunda BPMN OSGi - Process Application Integration](camunda-bpm-osgi-processapplication)

### Part 4 Referencing inside processes

#### With the BlueprintELResolver

See [camunda BPM OSGi - Blueprint Wrapper](camunda-bpm-blueprint-wrapper)

#### With the OSGiELResolver

The `OSGiELResolver` uses a three step resolution to match expressions with classes

##### Step 1

The first step uses the LDAP filter property. You have to export a service with the filter property "processExpression=". The OSGiELResolver then matches the expression with the filter.

##### Step 2

If the LDAP search doesn't suceed the ELResolver will search the Service Registry for JavaDelegates. Then the class name will be compared to the expression. The comparison is similiar to the default CDI bean names. That means the expression has to match the class name starting with a lowercase character, e.g. org.foo.bar.MyClass would match "${myClass}".

##### Step 3

The third steps works likes the second one, only that it searches for exported ActivityBehaviours. 

#### ProcessApplication API

See [camunda BPMN OSGi - Process Application Integration](camunda-bpm-osgi-processapplication)

## OSGi Event Bridge

See [camunda BPM OSGi - Eventing API](https://github.com/camunda/camunda-bpm-platform-osgi/tree/messaging/camunda-bpm-osgi-eventing-api).

## Resources

* [Issue Tracker](https://github.com/camunda/camunda-bpm-platform-osgi/issues)
* [Contributing](CONTRIBUTING.md)


## Roadmap

_a short list of things that yet need to be done (until we organize it elsewhere ;) )_

**todo**
- camunda webapp WAB (cockpit, tasklist, admin)
- create example for configuring engine using PAX-CDI

**done**
- QA, integration tests (resolve engine-bundle)
- example for configuring engine using Blueprint


## Maintainers:

* [@rbraeunlich ](https://github.com/rbraeunlich)

## License

Apache License, Version 2.0
