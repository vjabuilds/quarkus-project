package com.vjabuilds.resources;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.vjabuilds.models.Role;
import com.vjabuilds.repos.RolesRepo;
import com.vjabuilds.view_models.CreateRoleModel;
import com.vjabuilds.view_models.UserRoleModel;

import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;

@Path("/roles")
@AllArgsConstructor
@RolesAllowed({"admin"})
public class RolesResource {
        @Inject JsonWebToken jwt;
        @Inject RolesRepo repo;

        @GET
        public Uni<List<Role>> getRoles()
        {
            return repo.getRoles();
        }

        @POST
        public Uni<Boolean> createRole(CreateRoleModel model) {
            return repo.createRole(model.role_name());
        }

        @PUT
        @Path("/grant")
        public Uni<Boolean> giveUserRole(UserRoleModel model) {
            return repo.giveUserRole(model.username(), model.role_name());
        }

        @PUT
        @Path("/revoke")
        public Uni<Boolean> revokeUserRole(UserRoleModel model) {
            return repo.revokeUserRole(model.username(), model.role_name());
        }
}
