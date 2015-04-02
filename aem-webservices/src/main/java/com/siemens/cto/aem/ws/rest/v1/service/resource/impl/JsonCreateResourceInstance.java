package com.siemens.cto.aem.ws.rest.v1.service.resource.impl;

import com.siemens.cto.aem.domain.model.resource.command.CreateResourceInstanceCommand;
import com.siemens.cto.aem.ws.rest.v1.json.AbstractJsonDeserializer;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by z003e5zv on 3/16/2015.
 */
@JsonDeserialize(using = JsonCreateResourceInstance.CreateResourceInstanceJSONDeserializer.class)
public class JsonCreateResourceInstance {

    private static final Logger LOGGER = Logger.getLogger(JsonCreateResourceInstance.class);

    private final String resourceTypeName;
    private final String groupName;
    private final String friendlyName;
    private final Map<String, String> attributes;

    static final class CreateResourceInstanceJSONDeserializer extends AbstractJsonDeserializer<JsonCreateResourceInstance> {
        @Override
        public JsonCreateResourceInstance deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final ObjectCodec obj = jp.getCodec();
            final JsonNode rootNode = obj.readTree(jp);
            final JsonNode resourceTypeNameNode = rootNode.get("resourceTypeName");
            final JsonNode groupNameNode = rootNode.get("groupName");
            final JsonNode friendlyNameNode = rootNode.get("resourceName");
            final JsonNode attributesNode = rootNode.get("attributes");
            Iterator<JsonNode> attributesIt = attributesNode.getElements();
            Map<String, String> attributes = new HashMap<>();
            while (attributesIt.hasNext()) {
                JsonNode attributesEntry = attributesIt.next();
                attributes.put((attributesEntry.get("key")).getTextValue(), (attributesEntry.get("value")).getTextValue());
            }
            JsonCreateResourceInstance results = new JsonCreateResourceInstance(resourceTypeNameNode.getTextValue(), friendlyNameNode.getTextValue(), groupNameNode.getTextValue(), attributes);
            return results;
        }
    }
    public JsonCreateResourceInstance(final String resourceTypeName, final String friendlyName, final String groupName, final Map<String, String> attributes) {
        this.resourceTypeName = resourceTypeName;
        this.groupName = groupName;
        this.friendlyName = friendlyName;
        this.attributes = attributes;
    }
    public String getResourceTypeName() {
        return resourceTypeName;
    }

    public String getGroupName() {
        return groupName;
    }

    public CreateResourceInstanceCommand getCommand() {
        return new CreateResourceInstanceCommand(this.resourceTypeName, this.friendlyName, this.groupName, attributes);
    }
}
