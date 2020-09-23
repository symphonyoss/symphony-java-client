package org.symphonyoss.symphony.clients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;
import org.powermock.api.mockito.internal.mockcreation.RuntimeExceptionProxy;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SignalsException;
import org.symphonyoss.symphony.agent.api.SignalsApi;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.BaseSignal;
import org.symphonyoss.symphony.agent.model.Signal;
import org.symphonyoss.symphony.agent.model.SuccessResponse;
import org.symphonyoss.symphony.clients.impl.SignalsClientImpl;
import org.symphonyoss.symphony.clients.model.SymSignal;
import org.symphonyoss.symphony.clients.model.SymSignalTest;
import org.symphonyoss.util.TestFactory;

public class SignalsClientTest {

	SignalsClient signalsClient;
	SymphonyClient testClient;

	@Spy
	SignalsApi api;
	@Spy
	SignalsClientImpl spySignalsClient;

	@Before
	public void setUp() {
		testClient = TestFactory.getSymClient();
		signalsClient = testClient.getSignalsClient();
		spySignalsClient = spy(new SignalsClientImpl(testClient.getSymAuth(), testClient.getConfig()));
		api = mock(SignalsApi.class);
		when(spySignalsClient.createSignalsApi()).thenReturn(api);
	}

	@Test
	public void getClientTest() throws Exception {
		assertTrue("get DEFAULT client", SignalsFactory.getClient(testClient) instanceof SignalsClientImpl);
	}

	@Test
	public void createSignalTest() throws Exception {
		SymSignal signal = new SymSignal();
		signal.setName(SymSignalTest.DUMMY_NAME);
		signal.setQuery(SymSignalTest.DUMMY_QUERY);

		Signal apiSignal = SymSignal.toSignal(signal);
		when(api.v1SignalsCreatePost(anyString(), any(BaseSignal.class), anyString())).thenReturn(apiSignal);
		SymSignal result = spySignalsClient.createSignal(signal);
		assertEquals(SymSignalTest.DUMMY_NAME, result.getName());
		assertEquals(SymSignalTest.DUMMY_QUERY, result.getQuery());
	}

	@Test
	public void createSignalWithError() throws Exception {
		when(api.v1SignalsCreatePost(anyString(), any(BaseSignal.class), anyString())).thenThrow(new ApiException());
		try {
			spySignalsClient.createSignal(new SymSignal());
		} catch (Throwable sig) {
			// NOTE we will get a runtimeExceptionProxy due to how this is mocked
			assertTrue(sig instanceof RuntimeExceptionProxy);
			// This is the part we would expect in real life.
			assertTrue(sig.getCause() instanceof SignalsException);
			assertTrue(sig.getCause().getCause() instanceof ApiException);
		}
	}

	@Test
	public void updateSignalTest() throws Exception {
		SymSignal signal = new SymSignal();
		signal.setName(SymSignalTest.DUMMY_NAME);
		signal.setQuery(SymSignalTest.DUMMY_QUERY);
		signal.setId(SymSignalTest.DUMMY_ID);
		when(api.v1SignalsIdUpdatePost(anyString(), anyString(), any(BaseSignal.class), anyString())).thenReturn(SymSignal.toSignal(signal));
		SymSignal result = spySignalsClient.updateSignal(signal.getId(), signal);
		assertEquals(SymSignalTest.DUMMY_ID, result.getId());
	}

	@Test
	public void deleteSignalTest() throws Exception {
		SymSignal signal = new SymSignal();
		signal.setName(SymSignalTest.DUMMY_NAME);
		signal.setQuery(SymSignalTest.DUMMY_QUERY);
		signal.setId(SymSignalTest.DUMMY_ID);
		SuccessResponse resp=new SuccessResponse();
		when(api.v1SignalsIdDeletePost(anyString(), anyString(), anyString())).thenReturn(resp);
		spySignalsClient.deleteSignal(signal.getId());
	}
	
}
