package com.yashaswi.expense_tracker_api.dto.expense;

import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExportExpenseResponse {
    private Integer id;
    private String description;
    private Double amount;
    private LocalDate date;
    private ExpenseCategory expenseCategory;
}
