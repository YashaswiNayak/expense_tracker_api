package com.yashaswi.expense_tracker_api.dto;

import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ExpenseUpdateRequest {
    @NotNull
    private Integer id;
    private Double amount;
    private String description;
    private ExpenseCategory category;
    private LocalDate date;
    private UserResponse creator;
}
