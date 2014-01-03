# OSGi integration for camunda BPM platform

camunda BPM community extension providing support for camunda BPM platform inside OSGi containers

```
Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: camunda BPM Platform OSGi
Bundle-SymbolicName: org.camunda.bpm.osgi
Bundle-Version: 
Export-Package: [...]
Import-Package: [...]
```

## Get started

Build the project and drop it into an OSGi-environment. Add camunda-bpm-engine 7.1.0-alpha1 or a higher version and add mybatis and yodatime.
Alternatively you may use Apache Karaf and install camunda-bpm-osgi with the camunda-engine-karaf-feature. 

## Resources

* [Issue Tracker](https://github.com/camunda/camunda-bpm-platform-osgi/issues)
* [Contributing](https://github.com/camunda/camunda-bpm-platform-osgi/blob/master/CONTRIBUTING.md)


## Roadmap

_a short list of things that yet need to be done (until we organize it elsewhere ;) )_

**todo**
- QA, integration tests (resolve engine-bundle)
- create example for configuring engine using Blueprint
- adapt Process Application API for OSGi
- camunda webapp WAB (cockpit, tasklist, admin)
- create example for configuring engine using PAX-CDI

**done**
- nothing yet


## Maintainers:

* [@rbraeunlich ](https://github.com/rbraeunlich)

## License

Apache License, Version 2.0
