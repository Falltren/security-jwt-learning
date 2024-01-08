package com.example.securityjwtlearning.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class RegistrationUserDto {

    private String username;

    private String password;

    @JsonAlias("confirmpassword")
    private String confirmPassword;

    private String email;
}
