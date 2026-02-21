package com.yashaswi.expense_tracker_api.dto.expense;

import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.YearMonth;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TrendResponse {
    private YearMonth month;
    private Double totalAmount;
    private ExpenseCategory category;
}
