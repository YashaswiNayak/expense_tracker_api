package com.yashaswi.expense_tracker_api.dto.expense;

import com.yashaswi.expense_tracker_api.dto.user.UserResponse;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseResponse {
    private Integer id;
    private Double amount;
    private String description;
    private ExpenseCategory category;
    private LocalDate localDate;
    private UserResponse creator;
}
