# camunda BPM OSGi - Karaf Feature
This project contains a feature.xml to install the camunda-engine and camunda-engine-osgi project into your Karaf installation.

Check out this project and run mvm install on it. Then start Karaf and type the following depending on your Karaf version:
(Hint: The full version contains bundles for all the optional imports.)

## Karaf 3

To add the features:
```
feature:repo-add mvn:org.camunda.bpm.extension.osgi/camunda-bpm-karaf-feature/{version}/xml/features
```

To install the different features:
```
feature:install camunda-bpm-karaf-feature-minimal
```
or
```
feature:install camunda-bpm-karaf-feature-full
```

##Karaf 2

To add the features:
```
features:addurl mvn:org.camunda.bpm.extension.osgi/camunda-bpm-karaf-feature/{version}/xml/features

```
To install the different features:
```
features:install camunda-bpm-karaf-feature-minimal
```
or
```
features:install camunda-bpm-karaf-feature-full
```
