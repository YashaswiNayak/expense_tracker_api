package com.yashaswi.expense_tracker_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotEmpty(message = "Username is not filled")
    private String username;
    @NotEmpty(message = "Password is not filled")
    private String password;
}
