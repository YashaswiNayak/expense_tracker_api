package com.yashaswi.expense_tracker_api.controller;

import com.yashaswi.expense_tracker_api.dto.expense.ExpenseCreation;
import com.yashaswi.expense_tracker_api.dto.expense.ExpenseResponse;
import com.yashaswi.expense_tracker_api.dto.expense.ExpenseSummaryDto;
import com.yashaswi.expense_tracker_api.dto.expense.ExpenseUpdateRequest;
import com.yashaswi.expense_tracker_api.enums.DateRange;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import com.yashaswi.expense_tracker_api.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseCreation expenseCreation,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ExpenseResponse response =
                expenseService.createNewExpense(expenseCreation, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //____________________________________________________________________________________
    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "date",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDate endDate,
            @RequestParam(required = false)
            DateRange dateRange,
            @RequestParam(required = false)
            ExpenseCategory expenseCategory

    ) {
        Page<ExpenseResponse> expenses =
                expenseService.getAllExpenses(userDetails.getUsername(), pageable, startDate, endDate, dateRange, expenseCategory);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<ExpenseSummaryDto>> getSummary(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "date",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false)
            Integer year,
            @RequestParam(required = false)
            Integer month
    ) {
        return ResponseEntity.ok(expenseService.getSummary(userDetails.getUsername(), year, month));

    }

    //____________________________________________________________________________________
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Integer id) {
        return ResponseEntity.ok(expenseService.deleteExpense(id));
    }

    //____________________________________________________________________________________
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Integer id,
            @Valid @RequestBody ExpenseUpdateRequest expenseUpdateRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ExpenseResponse response = expenseService.updateResponse(expenseUpdateRequest, id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
