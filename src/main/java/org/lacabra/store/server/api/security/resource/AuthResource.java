package org.lacabra.store.server.api.security.resource;

import org.lacabra.store.server.api.security.service.token.AuthTokenUtils;
import org.lacabra.store.server.api.type.security.context.TokenSecurityContext;
import org.lacabra.store.server.api.type.security.password.CredValidator;
import org.lacabra.store.server.api.type.security.token.AuthToken;
import org.lacabra.store.server.api.type.user.Credentials;
import org.lacabra.store.server.api.type.user.User;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@RequestScoped
@Path("auth")
public class AuthResource {

    @Context
    private SecurityContext securityContext;

    @Inject
    private CredValidator validator;

    @Inject
    private AuthTokenUtils tokenUtils;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response authenticate(Credentials creds) {
        User user = validator.validate(creds.id(), creds.passwd());
        return Response.ok(new AuthToken(tokenUtils.issue(user.id(), user.authorities()))).build();
    }

    @POST
    @Path("refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refresh() {
        return Response.ok(new AuthToken(tokenUtils.refresh(((TokenSecurityContext) securityContext).getAuthTokenDetails()))).build();
    }
}