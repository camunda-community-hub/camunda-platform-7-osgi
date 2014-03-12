package org.camunda.bpm.extension.osgi.blueprint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.juel.SimpleContext;
import org.camunda.bpm.extension.osgi.blueprint.BlueprintELResolver;
import org.junit.Before;
import org.junit.Test;

public class BlueprintELResolverTest {

	private static final String OSGI_SERVICE_BLUEPRINT_COMPNAME = "osgi.service.blueprint.compname";
	private BlueprintELResolver resolver;

	@Before
	public void setUp() {
		resolver = new BlueprintELResolver();
	}

	@Test
	public void bindService() {
		Map<String, String> props = new HashMap<String, String>();
		props.put(OSGI_SERVICE_BLUEPRINT_COMPNAME, "delegate");
		MockDelegate delegate = new MockDelegate();
		resolver.bindService(delegate, props);
		SimpleContext context = new SimpleContext();
		Object object = resolver.getValue(context, null, "delegate");
		assertThat(object == delegate, is(true));
		assertThat(context.isPropertyResolved(), is(true));
	}

	@Test
	public void bindServiceWithBase() {
		Map<String, String> props = new HashMap<String, String>();
		props.put(OSGI_SERVICE_BLUEPRINT_COMPNAME, "delegate");
		MockDelegate delegate = new MockDelegate();
		resolver.bindService(delegate, props);
		Object object = resolver.getValue(new SimpleContext(), new Object(),
				"delegate");
		assertThat(object, is(nullValue()));
	}

	@Test
	public void getValueWithNullProperty() {
		Object object = resolver.getValue(new SimpleContext(), null, null);
		assertThat(object, is(nullValue()));
	}

	@Test
	public void getValueWithUppercaseLetters() {
		Map<String, String> props = new HashMap<String, String>();
		props.put(OSGI_SERVICE_BLUEPRINT_COMPNAME, "delegate");
		MockDelegate delegate = new MockDelegate();
		resolver.bindService(delegate, props);
		SimpleContext context = new SimpleContext();
		Object object = resolver.getValue(context, null, "dElEgAtE");
		assertThat(object == delegate, is(true));
		assertThat(context.isPropertyResolved(), is(true));
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

	@Test
	public void unbindNonExistingService() {
		Map<String, String> props = new HashMap<String, String>();
		props.put(OSGI_SERVICE_BLUEPRINT_COMPNAME, "delegate");
		resolver.unbindService(new MockDelegate(), props);
		// nothing more should happen
	}

	@Test
	public void unbindNullService() {
		Map<String, String> props = new HashMap<String, String>();
		props.put(OSGI_SERVICE_BLUEPRINT_COMPNAME, "delegate");
		resolver.unbindService(null, props);
		// nothing more should happen
	}

	@Test(expected = NullPointerException.class)
	public void unbindServiceWithNullProperties() {
		resolver.unbindService(new MockDelegate(), null);
	}

	@Test
	public void unbindServiceWithEmptyProperties() {
		resolver.unbindService(new MockDelegate(), new HashMap<Object, Object>());
	}
	
	@Test
	public void unbindService(){
		Map<String, String> props = new HashMap<String, String>();
		props.put(OSGI_SERVICE_BLUEPRINT_COMPNAME, "delegate");
		MockDelegate delegate = new MockDelegate();
		resolver.bindService(delegate, props);
		resolver.unbindService(delegate, props);
		SimpleContext context = new SimpleContext();
		Object value = resolver.getValue(context, null, "delegate");
		assertThat(value, is(nullValue()));
		assertThat(context.isPropertyResolved(), is(false));
	}

	private class MockDelegate implements JavaDelegate {
		@Override
		public void execute(DelegateExecution execution) throws Exception {
		}
	}
}
