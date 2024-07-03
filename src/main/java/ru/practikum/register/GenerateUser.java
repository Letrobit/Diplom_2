package ru.practikum.register;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;


@Getter
@Setter
public class GenerateUser {
    private String name;
    private String password;
    private String email;

    public GenerateUser() {
        this.name = RandomStringUtils.randomAlphabetic(10);
        this.password = RandomStringUtils.randomAlphabetic(10);
        this.email = RandomStringUtils.randomAlphabetic(5) + "@" + "megaBorgerLove.com";
    }

    public GenerateUser(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

}
