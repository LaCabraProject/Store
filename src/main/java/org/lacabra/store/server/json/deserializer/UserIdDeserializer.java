package org.lacabra.store.server.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.lacabra.store.server.api.type.id.UserId;

import java.io.IOException;

public final class UserIdDeserializer extends JsonDeserializer<UserId> {
    @Override
    public UserId deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.readValueAsTree();

        if (!node.isTextual())
            return null;

        return UserId.from(jp.getValueAsString());
    }
}