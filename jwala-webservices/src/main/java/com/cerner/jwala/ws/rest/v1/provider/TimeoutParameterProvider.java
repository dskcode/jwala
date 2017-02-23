package com.cerner.jwala.ws.rest.v1.provider;

import com.cerner.jwala.common.exception.BadRequestException;
import com.cerner.jwala.common.time.TimeDuration;
import com.cerner.jwala.ws.rest.v1.fault.RestFaultType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.QueryParam;
import java.util.concurrent.TimeUnit;

/**
 * Timeout provider
 * Now in milliseconds.
 * 
 * @author horspe00
 *
 */
public class TimeoutParameterProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutParameterProvider.class);

    private static final TimeDuration DEFAULT_TIMEOUT_VALUE = new TimeDuration(30L, TimeUnit.SECONDS);

    @QueryParam("timeout")
    private String timeout;

    public TimeoutParameterProvider() {
    }

    public TimeoutParameterProvider(final String aTimeout) {
        timeout = aTimeout;
    }

    public TimeDuration valueOf() throws BadRequestException {
        if (isParameterPresent()) {
            return parseValue();
        }

        return DEFAULT_TIMEOUT_VALUE;
    }

    TimeDuration parseValue() {
        try {
            final Long timeoutValue = Long.valueOf(timeout);
            validate(timeoutValue);
            return new TimeDuration(timeoutValue,
                                    TimeUnit.MILLISECONDS);
        } catch (final NumberFormatException nfe) {
            //This is logged here instead of included in the BadRequestException because it's a potential CSRF vector
            LOGGER.info("Non-integer value specified for the timeout parameter", nfe);
            throw new BadRequestException(RestFaultType.INVALID_TIMEOUT_PARAMETER,
                                          "Non-integer value specified for the timeout parameter");
        }
    }

    boolean isParameterPresent() {
        return timeout != null;
    }

    void validate(final Long aValue) {
        if (aValue <= 0 || aValue > 60) {
            throw new BadRequestException(RestFaultType.INVALID_TIMEOUT_PARAMETER,
                                          "Timeout value was outside the allowable range : " + aValue);
        }
    }
}