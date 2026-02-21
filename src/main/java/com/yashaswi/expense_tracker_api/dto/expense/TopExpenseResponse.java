package com.yashaswi.expense_tracker_api.dto.expense;

import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
@AllArgsConstructor
public class TopExpenseResponse {
    private Integer id;
    private String description;
    private Double amount;
    private LocalDate date;
    private ExpenseCategory category;
}
