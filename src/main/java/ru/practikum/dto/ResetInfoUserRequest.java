package ru.practikum.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetInfoUserRequest {
    private String name;
    private String email;
}
