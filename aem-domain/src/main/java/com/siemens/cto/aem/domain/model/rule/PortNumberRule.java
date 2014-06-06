package com.siemens.cto.aem.domain.model.rule;

import com.siemens.cto.aem.common.exception.BadRequestException;
import com.siemens.cto.aem.common.exception.MessageResponseStatus;
import com.siemens.cto.aem.domain.model.fault.AemFaultType;

public class PortNumberRule implements Rule {

    protected final Integer port;
	private AemFaultType error = AemFaultType.INVALID_HOST_NAME;
    protected boolean nullable;

    public PortNumberRule(final Integer thePort, final AemFaultType errorCode) {
        port = thePort;
        error = errorCode;
    }

    public PortNumberRule(final Integer thePort, final AemFaultType errorCode, final boolean nullable) {
        port = thePort;
        error = errorCode;
        this.nullable = nullable;
    }

    @Override
    public boolean isValid() {
        if (!nullable || port != null) {
            return (port != null) && (port > 0/*TCP/IP Reserved Port*/) && (port <= 65535 /*2^16-1*/);
        }
        return true;
    }

    @Override
    public void validate() throws BadRequestException {
        if (!isValid()) {
            throw new BadRequestException(getMessageResponseStatus(),
                                          getMessage());
        }
    }

    protected MessageResponseStatus getMessageResponseStatus() { return error; }

    protected String getMessage() { 
   		return "Port specified is invalid" + (port != null?(" ("+port+")."):".");
    }
}
