package org.acme;

import java.time.LocalDate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    record Person(String name, String last_name, LocalDate date_of_birth){

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Jovan Vjestica using Quarkus";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("test")
    public Person test() {
        return new Person("Jovan", "Vjestica", LocalDate.of(1996, 8, 9));
    }
}