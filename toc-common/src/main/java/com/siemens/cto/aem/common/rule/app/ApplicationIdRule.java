package com.siemens.cto.aem.common.rule.app;

import com.siemens.cto.aem.common.domain.model.app.Application;
import com.siemens.cto.aem.common.domain.model.fault.AemFaultType;
import com.siemens.cto.aem.common.domain.model.id.Identifier;
import com.siemens.cto.aem.common.rule.Rule;
import com.siemens.cto.aem.common.rule.identifier.IdentifierRule;

public class ApplicationIdRule extends IdentifierRule<Application> implements Rule {

    public ApplicationIdRule(final Identifier<Application> theId) {
        super(theId,
              AemFaultType.APPLICATION_NOT_SPECIFIED,
              "Application Id was not specified");
    }

}