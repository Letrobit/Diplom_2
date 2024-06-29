package ru.practikum.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    private String name;
    private String password;

    public CreateUserRequest(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    private String email;

}
