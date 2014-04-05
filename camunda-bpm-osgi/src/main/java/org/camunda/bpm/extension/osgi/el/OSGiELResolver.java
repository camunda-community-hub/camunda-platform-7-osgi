package org.camunda.bpm.extension.osgi.el;

import java.beans.FeatureDescriptor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OSGiELResolver extends ELResolver {

	private static final String LDAP_FILTER_KEY = "processExpression";

	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return Object.class;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
			Object base) {
		return null;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		return Object.class;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		Object service = null;
		if (base == null) {
			String key = (String) property;
			try {
				String ldapFilter = "(" + LDAP_FILTER_KEY + "=" + key + ")";
				// start with LDAP filter
				service = checkRegisteredServicesByLdapFilter(ldapFilter);
				if (service == null) {
					// go on with JavaDelegates
					service = checkRegisteredOsgiServices(
							JavaDelegate.class.getName(), key);
				}
				if (service == null) {
					// finally ActivitiBehaviors
					service = checkRegisteredOsgiServices(
							ActivityBehavior.class.getName(), key);
				}
			} catch (InvalidSyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		if (service != null) {
			context.setPropertyResolved(true);
		}
		return service;
	}

	/**
	 * Checks the OSGi ServiceRegistry if a service matching the given filter is
	 * present.
	 * 
	 * @param filter
	 *            the LDAP filter
	 * @return null if no service could be found or the service object
	 * @throws InvalidSyntaxException
	 *             if the filter has an invalid syntax
	 * @throws RuntimeException
	 *             if more than one service is found
	 */
	private Object checkRegisteredServicesByLdapFilter(String filter)
			throws InvalidSyntaxException {
		ServiceReference[] references = getBundleContext()
				.getServiceReferences(null, filter);
		if (isEmptyOrNull(references)) {
			return null;
		}
		if (references.length == 1) {
			return getBundleContext().getService(references[0]);
		} else {
			throw new RuntimeException(
					"Too many services registered for filter: " + filter);
		}
	}

	/**
	 * Checks the OSGi ServiceRegistry if a service matching the class and key
	 * are present. The class name has to match the key where the first letter
	 * has to be lower case.
	 * <p>
	 * For example:<br/>
	 * <code>
	 * public class MyServiceTask extends JavaDelegate</code> <br/>
	 * matches {@link JavaDelegate} with key "myServiceTask".
	 * 
	 * @param serviceClazz
	 * @param key
	 *            the name of the class
	 * @return null if no service could be found or the service object
	 * @throws RuntimeException
	 *             if more than one service is found
	 */
	private Object checkRegisteredOsgiServices(String serviceClazz, String key)
			throws InvalidSyntaxException {
		ServiceReference[] references = getBundleContext()
				.getServiceReferences(serviceClazz, null);
		if (isEmptyOrNull(references)) {
			return null;
		}
		Collection<Object> matches = checkIfClassNamesMatchKey(references, key);
		if (matches.size() == 1) {
			return matches.iterator().next();
		} else if (matches.size() > 1) {
			throw new RuntimeException("Too many " + serviceClazz
					+ " registered with name: " + key);
		}
		// zero matches
		return null;
	}

	/**
	 * Gets the service objects from the {@link BundleContext} and compares the
	 * class names to the given key. For the comparison see
	 * {@link #checkRegisteredOsgiServices(String, String)}.
	 * 
	 * @param references
	 * @param key
	 * @return
	 */
	private Collection<Object> checkIfClassNamesMatchKey(
			ServiceReference[] references, String key) {
		Set<Object> result = new HashSet<Object>();
		for (ServiceReference ref : references) {
			Object service = getBundleContext().getService(ref);
			if (service != null) {
				String keyWithFirstLetterUppercase = Character.toUpperCase(key
						.charAt(0)) + key.substring(1);
				if (service.getClass().getSimpleName()
						.equals(keyWithFirstLetterUppercase)) {
					result.add(service);
				}
			}
		}
		return result;
	}

	private boolean isEmptyOrNull(ServiceReference[] serviceReferences) {
		return serviceReferences == null ? true : serviceReferences.length == 0;
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return true;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property,
			Object value) {
	}

	protected BundleContext getBundleContext() {
		return FrameworkUtil.getBundle(getClass()).getBundleContext();
	}

}
