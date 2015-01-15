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
import org.camunda.bpm.engine.impl.javax.el.MethodNotFoundException;
import org.camunda.bpm.engine.impl.javax.el.PropertyNotFoundException;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.extension.osgi.TestActivityBehaviour;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OSGiELResolverTest {

	private TestOSGiELResolver resolver;

	private ELContext elContext;
	
	private ArgumentCaptor<Boolean> booleanCaptor;

	@Before
	public void setUp() {
		resolver = new TestOSGiELResolver();
		resolver.ctx = mock(BundleContext.class);
		elContext = mock(ELContext.class);
		booleanCaptor = ArgumentCaptor.forClass(Boolean.class);
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
		wasPropertyResolved();
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
		wasPropertyResolved();
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
		wasPropertyResolved();
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
		wasPropertyResolved();
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
		wasPropertyResolved();
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

	private void wasPropertyResolved(){
		verify(elContext).setPropertyResolved(booleanCaptor.capture());
		assertThat(booleanCaptor.getValue(), is(true));
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
		assertThat(resolver.getCommonPropertyType(null, new Object()),
				isA(Object.class));
	}

	@Test
	public void getFeatureDescriptors() {
		assertThat(resolver.getFeatureDescriptors(null, null), is(nullValue()));
	}
	
	@Test
	public void getValueForGetterProperty(){
		TestJavaBean bean = new TestJavaBean();
		Object value = new Object();
		bean.setValue(value);
		Object object = resolver.getValue(elContext, bean, "value");
		assertThat(object, is(sameInstance(value)));
		wasPropertyResolved();
	}
	
	@Test
	public void getValueForIsProperty(){
		TestJavaBean bean = new TestJavaBean();
		bean.setCalled(true);
		Boolean object = (Boolean) resolver.getValue(elContext, bean, "called");
		assertThat(object, is(true));
		wasPropertyResolved();
	}
	
	@Test
	public void getValueForAbsentProperty(){
		TestJavaBean bean = new TestJavaBean();
		resolver.getValue(elContext, bean, "foo");
		verifyZeroInteractions(elContext);
	}

	@Test
	public void setValue(){
		TestJavaBean bean = new TestJavaBean();
		Object value = new Object();
		resolver.setValue(elContext, bean, "value", value);
		assertThat(bean.getValue(), is(sameInstance(value)));
		wasPropertyResolved();
	}
	
	@Test(expected=PropertyNotFoundException.class)
	public void setValueForAbsentProperty(){
		TestJavaBean bean = new TestJavaBean();
		resolver.setValue(elContext, bean, "bar", new Object());
		verifyZeroInteractions(elContext);
	}
	
	@Test
	public void invoke(){
		TestJavaBean bean = new TestJavaBean();
		Object value = new Object();
		bean.setValue(value);
		Object invoke = resolver.invoke(elContext, bean, "getValue", new Class[0], null);
		assertThat(invoke, is(sameInstance(value)));
	}
	
	@Test(expected=MethodNotFoundException.class)
	public void invokeAbsentMethod(){
		TestJavaBean bean = new TestJavaBean();
		resolver.invoke(elContext, bean, "method", new Class[0], null);
		verifyZeroInteractions(elContext);
	}
}
