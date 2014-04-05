package org.camunda.bpm.extension.osgi.el;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OSGiELResolverTest {

	private TestOSGiELResolver resolver;

	private ELContext elContext;

	@Before
	public void setUp() {
		resolver = new TestOSGiELResolver();
		resolver.ctx = mock(BundleContext.class);
		elContext = mock(ELContext.class);
	}

	@Test
	public void getValueThatCantBeResolved() {
		Object value = resolver.getValue(elContext, null, "myServiceTask");
		assertThat(value, is(nullValue()));
		// make sure nobody called setPropertyResolved()
		verifyZeroInteractions(elContext);
	}

	@Test
	public void getValueForBaseNotNull() {
		Object value = resolver.getValue(elContext, new Object(),
				"myServiceTask");
		assertThat(value, is(nullValue()));
		// make sure nobody called setPropertyResolved()
		verifyZeroInteractions(elContext);
	}

	@Test
	public void getValueForMatchingLdapFilter() throws InvalidSyntaxException {
		String lookupName = "myServiceTask";
		String ldapFilter = "(processExpression=" + lookupName + ")";
		Object serviceObject = new Object();
		ServiceReference reference = mockService(serviceObject);
		registerServiceRefsAtContext(null, ldapFilter, reference);
		Object value = resolver.getValue(elContext, null, lookupName);
		assertThat(value, is(sameInstance(serviceObject)));
		ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
		verify(elContext).setPropertyResolved(captor.capture());
		assertThat(captor.getValue(), is(true));
	}

	@Test(expected = RuntimeException.class)
	public void getValueForTooManyMatchingLdapFilter()
			throws InvalidSyntaxException {
		String lookupName = "everybodysServiceTask";
		String ldapFilter = "(processExpression=" + lookupName + ")";
		registerServiceRefsAtContext(null, ldapFilter,
				mock(ServiceReference.class), mock(ServiceReference.class));
		resolver.getValue(elContext, null, lookupName);
	}

	@Test
	public void getValueForSingleJavaDelegate() throws InvalidSyntaxException {
		String lookupName = "testJavaDelegate";
		Object serviceObject = new TestJavaDelegate();
		ServiceReference reference = mockService(serviceObject);
		registerServiceRefsAtContext(JavaDelegate.class, null, reference);
		Object value = resolver.getValue(elContext, null, lookupName);
		assertThat(value, is(sameInstance(serviceObject)));
		ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
		verify(elContext).setPropertyResolved(captor.capture());
		assertThat(captor.getValue(), is(true));
	}

	@Test
	public void getValueForTwoJavaDelegates() throws InvalidSyntaxException {
		String lookupName = "testJavaDelegate";
		Object serviceObject = new TestJavaDelegate();
		ServiceReference reference1 = mockService(serviceObject);
		ServiceReference reference2 = mockService(new Object());
		registerServiceRefsAtContext(JavaDelegate.class, null, reference1,
				reference2);
		Object value = resolver.getValue(elContext, null, lookupName);
		assertThat(value, is(sameInstance(serviceObject)));
		ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
		verify(elContext).setPropertyResolved(captor.capture());
		assertThat(captor.getValue(), is(true));
	}

	@Test(expected = RuntimeException.class)
	public void getValueForTwoJavaDelegatesWithSameName()
			throws InvalidSyntaxException {
		String lookupName = "testJavaDelegate";
		ServiceReference reference1 = mockService(new TestJavaDelegate());
		ServiceReference reference2 = mockService(new TestJavaDelegate());
		registerServiceRefsAtContext(JavaDelegate.class, null, reference1,
				reference2);
		resolver.getValue(elContext, null, lookupName);
	}

	@Test
	public void getValueForSingleActivityBehavior()
			throws InvalidSyntaxException {
		String lookupName = "testActivityBehaviour";
		Object serviceObject = new TestActivityBehaviour();
		ServiceReference reference = mockService(serviceObject);
		registerServiceRefsAtContext(ActivityBehavior.class, null, reference);
		Object value = resolver.getValue(elContext, null, lookupName);
		assertThat(value, is(sameInstance(serviceObject)));
		ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
		verify(elContext).setPropertyResolved(captor.capture());
		assertThat(captor.getValue(), is(true));
	}

	@Test
	public void getValueForTwoActivityBehaviors() throws InvalidSyntaxException {
		String lookupName = "testActivityBehaviour";
		Object serviceObject = new TestActivityBehaviour();
		ServiceReference reference1 = mockService(serviceObject);
		ServiceReference reference2 = mockService(new Object());
		registerServiceRefsAtContext(ActivityBehavior.class, null, reference1,
				reference2);
		Object value = resolver.getValue(elContext, null, lookupName);
		assertThat(value, is(sameInstance(serviceObject)));
		ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
		verify(elContext).setPropertyResolved(captor.capture());
		assertThat(captor.getValue(), is(true));
	}

	@Test(expected = RuntimeException.class)
	public void getValueForTwoActivityBehaviorsWithSameName()
			throws InvalidSyntaxException {
		String lookupName = "testActivityBehaviour";
		ServiceReference reference1 = mockService(new TestActivityBehaviour());
		ServiceReference reference2 = mockService(new TestActivityBehaviour());
		registerServiceRefsAtContext(ActivityBehavior.class, null, reference1,
				reference2);
		resolver.getValue(elContext, null, lookupName);
	}

	private void registerServiceRefsAtContext(Class<?> clazz,
			String ldapFilter, ServiceReference... refs) {
		try {
			when(
					resolver.ctx.getServiceReferences(
							clazz != null ? clazz.getName() : null, ldapFilter))
					.thenReturn(refs);
		} catch (InvalidSyntaxException e) {
			// wont happen
			fail(e.getMessage());
		}
	}

	private ServiceReference mockService(Object service) {
		ServiceReference ref = mock(ServiceReference.class);
		when(resolver.ctx.getService(eq(ref))).thenReturn(service);
		return ref;
	}

	private class TestOSGiELResolver extends OSGiELResolver {

		private BundleContext ctx;

		@Override
		protected BundleContext getBundleContext() {
			return ctx;
		}

	}
	
	@Test
	public void isReadonly() {
		assertThat(resolver.isReadOnly(null, null, null), is(true));
	}

	@Test
	public void getCommonPropertyType() {
		assertThat(resolver.getCommonPropertyType(null, null),
				isA(Object.class));
	}

	@Test
	public void getFeatureDescriptors() {
		assertThat(resolver.getFeatureDescriptors(null, null), is(nullValue()));
	}

	@Test
	public void getType() {
		assertThat(resolver.getType(null, null, null), isA(Object.class));
	}
}
