package com.yashaswi.expense_tracker_api.dto.budget;

import com.yashaswi.expense_tracker_api.dto.user.UserResponse;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCreation {
    @NotNull
    private ExpenseCategory expenseCategory;
    @NotNull
    @Positive
    private Double limit;
    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}")
    private String period;
}
