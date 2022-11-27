package com.vjabuilds;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.vjabuilds.models.DatawavesUser;

@Path("/hello")
public class UserResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<DatawavesUser> users(){
       return List.of(new DatawavesUser(1l, "Pera", "Peric", "pera.peric@gmail.com", "pera123", true)); 
    }
}
