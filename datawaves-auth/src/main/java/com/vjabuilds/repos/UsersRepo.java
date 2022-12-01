package com.vjabuilds.repos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.password4j.Password;
import com.vjabuilds.models.DatawavesUser;
import com.vjabuilds.view_models.AuthRefreshToken;
import com.vjabuilds.view_models.LoginModel;
import com.vjabuilds.view_models.RegistrationModel;

import io.smallrye.jwt.build.Jwt;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class UsersRepo {
    @Inject ReactiveRedisDataSource redisDataSource;

    public static final String TABLE_NAME = "users";

    @ConfigProperty(name = "secret_salt")
    String salt;

    @ConfigProperty(name = "datawaves.jwt.auth-length")
    Long authLength;

    @ConfigProperty(name = "datawaves.jwt.refresh-length")
    Long refreshLength;

    public Uni<Boolean> registerUser(RegistrationModel model)
    {
        DatawavesUser user = new DatawavesUser(
            model.name(),
            model.lastName(),
            model.email(),
            Password.hash(model.password()).addRandomSalt().addPepper(salt).withArgon2().getResult(),
            true,
            Set.of("user")
        );
        return this.redisDataSource.hash(DatawavesUser.class)
            .hsetnx(TABLE_NAME, model.email(), user);
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
                        .expiresIn(authLength)
                        .groups(
                            new HashSet<>(x.user.getRoles())
                        ).sign();
                    String refresh = Jwt.issuer("https://vjabuilds.dev")
                        .upn(x.user.getEmail())
                        .audience("https://vjabuilds.dev/refresh")
                        .expiresIn(refreshLength)
                        .sign();
                    return new AuthRefreshToken(auth, refresh);
                }
                return null;
            });
    }

    public Uni<String> refresh(JsonWebToken jwt)
    {   
        String username = jwt.getClaim(Claims.upn);
        return this.redisDataSource.hash(DatawavesUser.class)
            .hget(TABLE_NAME, username)
            .map(x -> {
                String auth = Jwt.issuer("https://vjabuilds.dev")
                    .upn(x.getEmail())
                    .groups(
                        new HashSet<>(x.getRoles())
                    ).sign();
                return auth;
            });
    }
}
