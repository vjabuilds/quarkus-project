package com.vjabuilds.models;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class BaseModel {
    private String id;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    private ZonedDateTime deleted;

    public BaseModel()
    {
        id = UUID.randomUUID().toString();
        created = ZonedDateTime.now();
        updated = ZonedDateTime.now();
        deleted = null;
    }
}
