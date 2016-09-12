package org.symphonyoss.symphony.clients;

import org.symphonyoss.exceptions.SymException;
import org.symphonyoss.symphony.pod.model.MembershipList;

/**
 * Created by Frank Tarsillo on 6/12/2016.
 */
public interface RoomMembershipClient {
    MembershipList getRoomMembership(String id) throws SymException;
}
