package org.camunda.bpm.extension.osgi.classloading;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * This class wraps several classloaders and uses all of them to find a class or resource.
 * It's a workaround if some classloading issues due to class visibility
 * inside the OSGi environment occur.
 * @author gnodet
 * @author Ronny Bräunlich
 *
 */
public class ClassLoaderWrapper extends ClassLoader {

    private ClassLoader[] parents;

    public ClassLoaderWrapper(ClassLoader... parents) {
        this.parents = parents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        //
        // Check if class is in the loaded classes cache
        //
        Class<?> cachedClass = findLoadedClass(name);
        if (cachedClass != null) {
            if (resolve) {
                resolveClass(cachedClass);
            }
            return cachedClass;
        }

        //
        // Check parent class loaders
        //
        for (int i = 0; i < parents.length; i++) {
            ClassLoader parent = parents[i];
            try {
                Class<?> clazz = parent.loadClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } catch (ClassNotFoundException ignored) {
                // this parent didn't have the class; try the next one
            }
        }

        throw new ClassNotFoundException(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(String name) {
        //
        // Check parent class loaders
        //
        for (int i = 0; i < parents.length; i++) {
            ClassLoader parent = parents[i];
            URL url = parent.getResource(name);
            if (url != null) {
                return url;
            }
        }

        return null;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        List<URL> resources = new ArrayList<URL>();

        //
        // Add parent resources
        //
        for (int i = 0; i < parents.length; i++) {
            ClassLoader parent = parents[i];
            List<URL> parentResources = Collections.list(parent.getResources(name));
            resources.addAll(parentResources);
        }

        return Collections.enumeration(resources);
    }

}
