package com.vjabuilds.repos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.password4j.Password;
import com.vjabuilds.models.DatawavesUser;
import com.vjabuilds.models.Role;
import com.vjabuilds.view_models.AuthRefreshToken;
import com.vjabuilds.view_models.LoginModel;
import com.vjabuilds.view_models.RegistrationModel;

import io.smallrye.jwt.build.Jwt;
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
            UUID.randomUUID().toString(),
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

    public Uni<AuthRefreshToken> login(LoginModel model)
    {   
        return this.redisDataSource.hash(DatawavesUser.class)
            .hget(TABLE_NAME, model.username())
            .map(x -> new Object(){
                public boolean valid = Password.check(model.password(), x.getPassword()).addPepper(salt).withArgon2();
                public DatawavesUser user = x;
            }).map(x -> {
                if(x.valid){
                    String auth = Jwt.issuer("https://vjabuilds.dev")
                        .upn(x.user.getEmail())
                        .groups(
                            new HashSet<>(x.user.getRoles().stream()
                                .map(r -> r.getName())
                                .collect(Collectors.toList()))
                        ).sign();
                    String refresh = Jwt.issuer("https://vjabuilds.dev")
                        .upn(x.user.getEmail())
                        .expiresIn(24*60*60)
                        .sign();
                    return new AuthRefreshToken(auth, refresh);
                }
                return null;
            });
    }
}
