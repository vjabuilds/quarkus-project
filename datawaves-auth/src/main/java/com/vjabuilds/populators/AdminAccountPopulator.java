package com.vjabuilds.populators;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.vjabuilds.repos.RolesRepo;
import com.vjabuilds.repos.UsersRepo;
import com.vjabuilds.view_models.RegistrationModel;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class AdminAccountPopulator {
    @Inject RolesRepo rolesRepo;
    @Inject UsersRepo usersRepo;

    @ConfigProperty(name = "datawaves.admin.should_populate")
    Boolean should_populate;

    @ConfigProperty(name = "datawaves.admin.username")
    String username;

    @ConfigProperty(name = "datawaves.admin.password")
    String password;
    
    void onStart(@Observes StartupEvent ev) {             
        if(should_populate)  
            rolesRepo.createRole("admin").onItem()
                .call(x -> rolesRepo.createRole("user"))
                .call(x -> usersRepo.registerUser(new RegistrationModel(
                    username,
                    password, 
                    "admin", 
                    "admin")))
                .call(x -> rolesRepo.giveUserRole(username, "admin"))
                .await().indefinitely();
    }
}
