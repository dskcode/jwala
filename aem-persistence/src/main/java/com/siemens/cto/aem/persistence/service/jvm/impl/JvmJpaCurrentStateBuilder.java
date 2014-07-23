package com.siemens.cto.aem.persistence.service.jvm.impl;

import com.siemens.cto.aem.domain.model.jvm.Jvm;
import com.siemens.cto.aem.domain.model.jvm.JvmState;
import com.siemens.cto.aem.domain.model.state.CurrentState;
import com.siemens.cto.aem.domain.model.state.StateType;
import com.siemens.cto.aem.persistence.jpa.domain.JpaCurrentState;
import com.siemens.cto.aem.persistence.service.state.impl.AbstractJpaCurrentStateBuilder;

public class JvmJpaCurrentStateBuilder extends AbstractJpaCurrentStateBuilder<Jvm, JvmState> {

    public JvmJpaCurrentStateBuilder(final JpaCurrentState aCurrentState) {
        super(aCurrentState);
    }

    public JvmJpaCurrentStateBuilder setCurrentState(final JpaCurrentState aCurrentState) {
        currentState = aCurrentState;
        return this;
    }

    @Override
    public CurrentState<Jvm, JvmState> build() {
        if (currentState != null) {
            return new CurrentState<>(createId(),
                                      createState(),
                                      createAsOf(),
                                      StateType.JVM);
        }

        return null;
    }

    private JvmState createState() {
        return JvmState.convertFrom(currentState.getState());
    }
}
