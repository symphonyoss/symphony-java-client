package org.symphonyoss.symphony.clients;

import org.junit.Test;
import org.symphonyoss.symphony.clients.impl.PresenceClientImpl;
import org.symphonyoss.util.TestFactory;

import static org.junit.Assert.*;

/**
 * Created by Frank Tarsillo on 6/19/2016.
 */
public class PresenceFactoryTest {
    @Test
    public void getClient() throws Exception {

        assertTrue("get DEFAULT client",  PresenceFactory.getClient(TestFactory.getSymClient(),PresenceFactory.TYPE.DEFAULT) instanceof PresenceClientImpl);


    }

}