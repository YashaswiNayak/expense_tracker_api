package com.yashaswi.expense_tracker_api.dto.expense;

import com.yashaswi.expense_tracker_api.enums.DateRange;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class ExpenseFilter {
    private String userName;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDate endDate;
    private DateRange dateRange;
    private List<ExpenseCategory> categories;
    private Double minAmount;
    private Double maxAmount;
}
