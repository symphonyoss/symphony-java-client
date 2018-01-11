package org.symphonyoss.symphony.clients.jmx;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SystemException;
import org.symphonyoss.symphony.clients.AgentSystemClient;
import org.symphonyoss.symphony.clients.AgentSystemClientFactory;
import org.symphonyoss.symphony.clients.model.SymAgentHealthCheck;

public class ClientCheck implements ClientCheckMBean {

    AgentSystemClient agentSystemClient;

    public ClientCheck(SymphonyClient symClient) {
        this.agentSystemClient = AgentSystemClientFactory.getClient(symClient);
    }

    @Override
    public SymAgentHealthCheck fetchFullDetail() throws SystemException {
        return this.agentSystemClient.getAgentHealthCheck();
    }

    @Override
    public Boolean isUp() throws SystemException {
        return fetchFullDetail().isUp();
    }
}
