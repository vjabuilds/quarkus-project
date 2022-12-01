package com.vjabuilds.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.vjabuilds.models.DatawavesUser;
import com.vjabuilds.repos.UsersRepo;
import com.vjabuilds.view_models.LoginModel;
import com.vjabuilds.view_models.RegistrationModel;

import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;

@Path("/users")
@AllArgsConstructor
public class UserResource {

    UsersRepo usersRepo;
    @Inject JsonWebToken jwt;

    @GET
    public Uni<List<DatawavesUser>> users(){
       return usersRepo.getUsers();
    }

    @POST
    public Uni<Response> register_user(RegistrationModel model) {
        return usersRepo.registerUser(model).map(x -> x ? 
            Response.ok().build() : Response.status(409).build()
        );
    }

    @POST
    @Path("/login")
    public Uni<Response> login(LoginModel model) {
        return usersRepo.login(model).map(
            x -> x != null ? Response.ok(x.auth())
                    .cookie(
                        new NewCookie("refresh_token", 
                        x.refresh(), 
                        null, null, 
                        NewCookie.DEFAULT_VERSION, 
                        null, 
                        NewCookie.DEFAULT_MAX_AGE, 
                        null, 
                        false, 
                        true)
                    ).build() 
                    :
                    Response.status(401, "Invalid username and password combiantion")
                    .build()

        );
    }
}
