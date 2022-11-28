package com.vjabuilds.repos;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.password4j.Password;
import com.vjabuilds.models.DatawavesUser;
import com.vjabuilds.models.Role;
import com.vjabuilds.view_models.RegistrationModel;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class UsersRepo {
    @Inject ReactiveRedisDataSource redisDataSource;

    private static final String TABLE_NAME = "users";
    @ConfigProperty(name = "secret_salt")
    private String salt;

    public Uni<Boolean> registerUser(RegistrationModel model)
    {
        DatawavesUser user = new DatawavesUser(
            1l,
            model.name(),
            model.lastName(),
            model.email(),
            Password.hash(model.password()).addRandomSalt().addPepper(salt).withArgon2().getResult(),
            true,
            List.of(new Role(1l, "user"))
        );

        return this.redisDataSource.hash(DatawavesUser.class)
            .hset(TABLE_NAME, model.email(), user);
    }

    public Uni<List<DatawavesUser>> getUsers()
    {
        return this.redisDataSource.hash(DatawavesUser.class)
            .hgetall(TABLE_NAME).map(x -> new ArrayList<>(x.values()));
    }
}
