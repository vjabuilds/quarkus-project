package com.vjabuilds.models;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class DatawavesUser extends BaseModel {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled = true;
    private Set<String> roles;
}
