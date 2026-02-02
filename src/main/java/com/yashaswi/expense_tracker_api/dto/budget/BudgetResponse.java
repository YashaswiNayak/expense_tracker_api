package com.yashaswi.expense_tracker_api.dto.budget;

import com.yashaswi.expense_tracker_api.dto.user.UserResponse;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetResponse {
    private Integer id;
    private ExpenseCategory expenseCategory;
    private Double limit;
    private Double spent;
    private YearMonth period;
    private UserResponse creator;

}
