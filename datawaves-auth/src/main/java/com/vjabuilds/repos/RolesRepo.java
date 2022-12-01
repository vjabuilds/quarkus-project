package com.vjabuilds.repos;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.vjabuilds.models.DatawavesUser;
import com.vjabuilds.models.Role;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class RolesRepo {
    @Inject ReactiveRedisDataSource redisDataSource;

    public static final String TABLE_NAME = "roles";

    public Uni<List<Role>> getRoles() {
        return redisDataSource.hash(Role.class)
            .hgetall(TABLE_NAME).map(x -> new ArrayList<>(x.values()));
    }
    
    public Uni<Boolean> createRole(String role_name) {
        return redisDataSource.hash(Role.class)
            .hset(TABLE_NAME, role_name, new Role(role_name));
    }

    public Uni<Boolean> giveUserRole(String username, String role_name) {
        return Uni.combine().all().unis(
            redisDataSource.hash(Role.class).hget(TABLE_NAME, role_name), 
            redisDataSource.hash(DatawavesUser.class).hget(UsersRepo.TABLE_NAME, username)
        ).asTuple()
            .flatMap(x -> {
                var role = x.getItem1();
                var user = x.getItem2();
                if(role == null || user == null || role.getDeleted() != null || user.getDeleted() != null) {
                    return Uni.createFrom().item(false);
                }
                else 
                {
                    user.getRoles().add(role.getName());  // TODO: role might have been deleted between 
                    user.setUpdated(ZonedDateTime.now()); // fetching role info and assignement. Should implement
                                                          // either optimistic locking or role checking on user read.
                    return redisDataSource.hash(DatawavesUser.class)
                        .hset(UsersRepo.TABLE_NAME, user.getEmail(), user).map(res -> true);
                }
            }
        );
    }

    public Uni<Boolean> revokeUserRole(String username, String role_name) {
        return redisDataSource.hash(DatawavesUser.class).hget(UsersRepo.TABLE_NAME, username)
            .flatMap(x -> {
                if(x == null)
                    return Uni.createFrom().item(false);
                else {
                    x.getRoles().remove(role_name);
                    x.setUpdated(ZonedDateTime.now());
                    return redisDataSource.hash(DatawavesUser.class)
                        .hset(UsersRepo.TABLE_NAME, username, x).map(res -> true);
                }
            });
    }
}
