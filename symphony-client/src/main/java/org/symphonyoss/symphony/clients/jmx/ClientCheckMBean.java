package org.symphonyoss.symphony.clients.jmx;

import org.symphonyoss.client.exceptions.SystemException;
import org.symphonyoss.symphony.clients.model.SymAgentHealthCheck;

public interface ClientCheckMBean {
    public SymAgentHealthCheck fetchFullDetail() throws SystemException;

    public Boolean isUp() throws SystemException;
}
