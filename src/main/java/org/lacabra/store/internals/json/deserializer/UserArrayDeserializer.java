package org.lacabra.store.internals.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.lacabra.store.client.dto.UserDTO;
import org.lacabra.store.internals.json.provider.ObjectMapperProvider;
import org.lacabra.store.server.api.type.user.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class UserArrayDeserializer {
    public static final class DTO extends JsonDeserializer<UserDTO[]> {
        @Override
        public UserDTO[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            JsonNode node = jp.readValueAsTree();

            if (node.isNull())
                return null;

            final var omp = new ObjectMapperProvider();

            if (!node.isArray())
                node = omp.getContext(UserDTO[].class).createArrayNode().add(node);

            final List<UserDTO> users = new ArrayList<>();

            var mapper = omp.getContext(UserDTO.class);
            for (final JsonNode n : node) {
                users.add(mapper.treeToValue(n, UserDTO.class));
            }

            return users.toArray(new UserDTO[0]);
        }
    }

    public static final class Persisent extends JsonDeserializer<User[]> {
        @Override
        public User[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            JsonNode node = jp.readValueAsTree();

            if (node.isNull())
                return null;

            final var omp = new ObjectMapperProvider();

            if (!node.isArray())
                node = omp.getContext(User[].class).createArrayNode().add(node);

            final List<User> users = new ArrayList<>();

            var mapper = omp.getContext(User.class);
            for (final JsonNode n : node) {
                users.add(mapper.treeToValue(n, User.class));
            }

            return users.toArray(new User[0]);
        }
    }
}