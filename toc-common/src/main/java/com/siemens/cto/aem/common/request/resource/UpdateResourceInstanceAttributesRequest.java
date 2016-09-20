package com.siemens.cto.aem.common.request.resource;

import com.siemens.cto.aem.common.request.Request;
import com.siemens.cto.aem.common.exception.BadRequestException;
import com.siemens.cto.aem.common.domain.model.id.Identifier;
import com.siemens.cto.aem.common.domain.model.resource.ResourceInstance;
import com.siemens.cto.aem.common.rule.MultipleRules;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by z003e5zv on 3/16/2015.
 */
public class UpdateResourceInstanceAttributesRequest implements Serializable, Request {
    private static final long serialVersionUID = 1L;

    private final Identifier<ResourceInstance> resourceInstanceId;

    private final Map<String, String> attributes;

    @Override
    public void validate() {
        new MultipleRules(
        );
    }
    public UpdateResourceInstanceAttributesRequest(Identifier<ResourceInstance> resourceInstanceId, Map<String, String> attributes) {
        this.resourceInstanceId = resourceInstanceId;
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Identifier<ResourceInstance> getResourceInstanceId() {
        return this.resourceInstanceId;
    }

}