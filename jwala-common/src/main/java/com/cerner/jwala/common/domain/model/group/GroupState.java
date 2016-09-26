package com.cerner.jwala.common.domain.model.group;

import java.util.HashMap;
import java.util.Map;

import com.cerner.jwala.common.domain.model.state.OperationalState;


public enum GroupState implements OperationalState {

    GRP_INITIALIZED("INITIALIZED"),
    GRP_PARTIAL( "PARTIAL"),
    GRP_FAILURE( "FAILED"),
    GRP_STARTED( "STARTED"),
    GRP_STOPPED( "STOPPED"),
    GRP_STARTING("STARTING"),
    GRP_STOPPING("STOPPING"),
    GRP_UNKNOWN( "UNKNOWN");

    private static final Map<String, GroupState> LOOKUP_MAP = new HashMap<>();

    static {
        for (final GroupState state : values()) {
            LOOKUP_MAP.put(state.toPersistentString(), state);
        }
    }

    public static GroupState convertFrom(final String aStateName) {
        if (LOOKUP_MAP.containsKey(aStateName)) {
            return LOOKUP_MAP.get(aStateName);
        }
        return GRP_UNKNOWN;
    }

    private final String stateName;

    private GroupState(final String theStateName) {
        stateName = theStateName;
    }

    public String getStateName() {
        return stateName;
    }

    @Override
    public String toStateLabel() {
        return stateName;
    }

    @Override
    public String toPersistentString() {
        return name();
    }
}