package com.vjabuilds;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.vjabuilds.models.DatawavesUser;
import com.vjabuilds.repos.UsersRepo;

import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;

@Path("/pera")
@AllArgsConstructor
public class UserResource {

    UsersRepo usersRepo;

    @GET
    public Response users(){
        usersRepo.printNice();
       return Response.ok(List.of(new DatawavesUser(1l, "Pera", "Peric", "pera.peric@gmail.com", "pera123", true, List.of()))).build(); 
    }

    @GET
    @Path("/jovan")
    public Uni<DatawavesUser> jovan() {
        return usersRepo.getJovan();
    }
}
