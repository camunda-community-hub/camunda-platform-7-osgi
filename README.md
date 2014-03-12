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

There are several ways to get started. Probably the easiest one is to use the Apache Karaf feature.xml. How to use it can be found [here](https://github.com/camunda/camunda-bpm-platform-osgi/blob/master/camunda-engine-karaf-feature/README.md).

Another way is to build the project, drop it into an OSGi-environment, add camunda-bpm-engine 7.1.0-SNAPSHOT or a higher version, add [MyBatis](http://search.maven.org/#search|ga|1|org.mybatis) and [joda-time](http://search.maven.org/#search|ga|1|joda-time).
That's the setup for a minimal environment. Most likely you'll also need [H2 Database](http://search.maven.org/#search|ga|1|com.h2database)

To use the camunda BPM platform in an enterprise OSGi environment you can use the camunda-engine-blueprint-wrapper, which already exports services.

## Resources

* [Issue Tracker](https://github.com/camunda/camunda-bpm-platform-osgi/issues)
* [Contributing](https://github.com/camunda/camunda-bpm-platform-osgi/blob/master/CONTRIBUTING.md)


## Roadmap

_a short list of things that yet need to be done (until we organize it elsewhere ;) )_

**todo**
- QA, integration tests (resolve engine-bundle)
- adapt Process Application API for OSGi
- camunda webapp WAB (cockpit, tasklist, admin)
- create example for configuring engine using PAX-CDI

**done**
- first set of integration tests
- example for configuring engine using Blueprint


## Maintainers:

* [@rbraeunlich ](https://github.com/rbraeunlich)

## License

Apache License, Version 2.0
