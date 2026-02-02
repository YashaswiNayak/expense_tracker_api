package com.yashaswi.expense_tracker_api.dto.expense;

import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSummaryDto {
    ExpenseCategory category;
    Double totalAmount;
    Long expenseCount;
    Double averageAmount;
}
