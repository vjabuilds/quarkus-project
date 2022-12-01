package com.vjabuilds.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.vjabuilds.models.Role;
import com.vjabuilds.repos.RolesRepo;
import com.vjabuilds.view_models.CreateRoleModel;
import com.vjabuilds.view_models.GiveUserRoleModel;

import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;

@Path("/roles")
@AllArgsConstructor
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

        @POST
        @Path("/grant")
        public Uni<Boolean> giveUserRole(GiveUserRoleModel model) {
            return repo.giveUserRole(model.username(), model.role_name());
        }
}
