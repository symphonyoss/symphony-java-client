package org.symphonyoss.symphony.clients;

import org.symphonyoss.symphony.clients.model.SymUserConnection;
import org.symphonyoss.symphony.clients.model.SymUserConnectionRequest;

import java.util.List;

/**
 * Created by frank.tarsillo on 9/9/2016.
 */
public interface ConnectionsClient {
    List<SymUserConnection> getIncomingRequests() throws Exception;

    List<SymUserConnection> getPendingRequests() throws Exception;

    List<SymUserConnection> getRejectedRequests() throws Exception;

    List<SymUserConnection> getAcceptedRequests() throws Exception;

    List<SymUserConnection> getAllConnections() throws Exception;

    SymUserConnection sendConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws Exception;

    SymUserConnection acceptConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws Exception;

    SymUserConnection rejectConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws Exception;

    SymUserConnection getUserConnection(String userId) throws Exception;
}
