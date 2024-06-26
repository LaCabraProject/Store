package org.lacabra.store.server.api.type.security.user;

import org.lacabra.store.internals.type.id.UserId;
import org.lacabra.store.server.api.type.user.Authority;
import org.lacabra.store.server.api.type.user.Credentials;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public final class AuthedUserDetails implements Principal {
    private final String id;
    @NotNull
    private final Set<Authority> authorities;

    public AuthedUserDetails(String id, Authority authorities) {
        this(id, Collections.singleton(authorities));
    }

    public AuthedUserDetails(String id, Collection<Authority> authorities) {
        this(UserId.from(id), authorities);
    }

    public AuthedUserDetails(UserId id, Authority authorities) {
        this(id, Collections.singleton(authorities));
    }

    public AuthedUserDetails(UserId id, Collection<Authority> authorities) {
        this(new Credentials(id, authorities));
    }

    @SuppressWarnings("unchecked")
    public AuthedUserDetails(Credentials creds) {
        switch (creds) {
            case null -> {
                this.id = null;
                this.authorities = Collections.EMPTY_SET;
            }

            case Credentials c -> {
                this.id = creds.id() == null ? null : creds.id().get();
                this.authorities = Set.copyOf(creds.authorities() == null ? Collections.EMPTY_SET :
                        Objects.requireNonNull(creds.authorities()));
            }
        }
    }

    public AuthedUserDetails(String id, Set<Authority> authorities) {
        this.id = id;
        this.authorities = authorities;
    }

    public Set<Authority> authorities() {
        return this.authorities;
    }

    public String id() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.id;
    }
}