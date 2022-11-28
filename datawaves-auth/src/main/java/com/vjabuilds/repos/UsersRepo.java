package com.vjabuilds.repos;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.vjabuilds.models.DatawavesUser;
import com.vjabuilds.models.Role;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class UsersRepo {
    @Inject ReactiveRedisDataSource redisDataSource;

    public void printNice()
    {
        System.out.println("Calling the redis datasource!");
        DatawavesUser user = new DatawavesUser(
            1l, "jovan", "vjestica", "jovan@gmail.com", "test.123", true, List.of(new Role(1l, "admin"), new Role(2l, "user"))
        );
        this.redisDataSource.hash(DatawavesUser.class)
            .hset("jovan.vjestica@gmail.com", "test", user)
            .await().indefinitely();
        System.out.println("This was nice!");
    }

    public Uni<DatawavesUser> getJovan()
    {
        return this.redisDataSource.hash(DatawavesUser.class).hget("jovan.vjestica@gmail.com", "test");
    }
}
