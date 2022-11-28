package com.vjabuilds;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.vjabuilds.models.DatawavesUser;
import com.vjabuilds.repos.UsersRepo;
import com.vjabuilds.view_models.RegistrationModel;

import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;

@Path("/users")
@AllArgsConstructor
public class UserResource {

    UsersRepo usersRepo;

    @GET
    public Uni<List<DatawavesUser>> users(){
       return usersRepo.getUsers();
    }

    @POST
    public Uni<Boolean> jovan(RegistrationModel model) {
        return usersRepo.registerUser(model);
    }
}
