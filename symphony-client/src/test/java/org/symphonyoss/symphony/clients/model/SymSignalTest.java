package org.symphonyoss.symphony.clients.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.symphonyoss.symphony.agent.model.BaseSignal;
import org.symphonyoss.symphony.agent.model.Signal;
import org.symphonyoss.symphony.agent.model.SignalList;

public class SymSignalTest {
	public static final String DUMMY_ID = "dummy_id";
	public static final String DUMMY_QUERY = "HASHTAG:dummy AND CASHTAG:query";
	public static final String DUMMY_NAME = "Dummy Name for signal";
	public static final Boolean COMPANY_WIDE = false;
	public static final Boolean VISIBLE_ON_PROFILE = true;

	public void compareSignalToSymSignal(Signal sig, SymSignal symSig) {
		assertEquals(sig.getId(), symSig.getId());
		assertEquals(sig.getName(), symSig.getName());
		assertEquals(sig.getQuery(), symSig.getQuery());
		assertEquals(sig.getCompanyWide(), symSig.getCompanyWide());
		assertEquals(sig.getVisibleOnProfile(), symSig.getVisibleOnProfile());
	}
	
	public void compareBaseSignalToSymSignal(BaseSignal sig, SymSignal symSig) {
		assertEquals(sig.getName(), symSig.getName());
		assertEquals(sig.getQuery(), symSig.getQuery());
		assertEquals(sig.getCompanyWide(), symSig.getCompanyWide());
		assertEquals(sig.getVisibleOnProfile(), symSig.getVisibleOnProfile());
	}
	
	@Test
	public void testToSymSignal() {
		Signal sig = new Signal();
		sig.id(DUMMY_ID);
		sig.name(DUMMY_NAME);
		sig.query(DUMMY_QUERY);
		sig.companyWide(COMPANY_WIDE);
		sig.visibleOnProfile(VISIBLE_ON_PROFILE);
		SymSignal symSig = SymSignal.toSymSignal(sig);
		compareSignalToSymSignal(sig, symSig);
	}

	@Test
	public void testFromSymSignal() {
		SymSignal symSig = new SymSignal();
		symSig.setId(DUMMY_ID);
		symSig.setName(DUMMY_NAME);
		symSig.setQuery(DUMMY_QUERY);
		symSig.setCompanyWide(COMPANY_WIDE);
		symSig.setVisibleOnProfile(VISIBLE_ON_PROFILE);
		Signal sig = SymSignal.toSignal(symSig);
		compareSignalToSymSignal(sig, symSig);
	}

	@Test
	public void toBaseSignalTest() {
		SymSignal symSig = new SymSignal();
		symSig.setName(DUMMY_NAME);
		symSig.setQuery(DUMMY_QUERY);
		symSig.setCompanyWide(COMPANY_WIDE);
		symSig.setVisibleOnProfile(VISIBLE_ON_PROFILE);
		BaseSignal sig = SymSignal.toBaseSignal(symSig);
		compareBaseSignalToSymSignal(sig, symSig);
	}
	
	@Test
	public void toSignalListTest() {
		SignalList sigList=new SignalList();
		Signal sig = new Signal();
		sig.id(DUMMY_ID);
		sig.name(DUMMY_NAME);
		sig.query(DUMMY_QUERY);
		sig.companyWide(COMPANY_WIDE);
		sig.visibleOnProfile(VISIBLE_ON_PROFILE);
		sigList.add(sig);
		List<SymSignal> sigs=SymSignal.fromSignalList(sigList);
		SymSignal symSig=sigs.get(0);
		compareSignalToSymSignal(sig, symSig);
	}
}
