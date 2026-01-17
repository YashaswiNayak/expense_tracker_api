package com.yashaswi.expense_tracker_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotEmpty(message = "Username is mandatory")
    private String username;
    @NotEmpty(message = "Email is needed")
    private String email;
    @NotEmpty(message = "Password is needed")
    @Size(min = 8,message = "Password needs to be 8 characters long")
    private String password;
}
