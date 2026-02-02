package com.yashaswi.expense_tracker_api.dto.budget;

import com.yashaswi.expense_tracker_api.dto.user.UserResponse;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.YearMonth;

@Data
public class BudgetUpdateRequest {
    @NotNull
    private ExpenseCategory expenseCategory;
    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}")
    private YearMonth period;
    @NotNull
    private Double amountDelta;
}
