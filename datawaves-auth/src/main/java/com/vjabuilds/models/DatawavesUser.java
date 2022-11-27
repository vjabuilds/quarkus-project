package com.vjabuilds.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatawavesUser {
    private Long user_id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled = true;
}
