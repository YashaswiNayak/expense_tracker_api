package com.yashaswi.expense_tracker_api.controller;

import com.yashaswi.expense_tracker_api.dto.expense.*;
import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import com.yashaswi.expense_tracker_api.service.CsvExportService;
import com.yashaswi.expense_tracker_api.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CsvExportService csvExportService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseCreation expenseCreation, @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseResponse response = expenseService.createNewExpense(expenseCreation, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //____________________________________________________________________________________
    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(@PageableDefault(page = 0, size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable, @AuthenticationPrincipal UserDetails userDetails, @ModelAttribute ExpenseFilter expenseFilter

    ) {
        Page<ExpenseResponse> expenses = expenseService.getAllExpenses(userDetails.getUsername(), pageable, expenseFilter);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<ExpenseSummaryDto>> getSummary(@PageableDefault(page = 0, size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable, @AuthenticationPrincipal UserDetails userDetails, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(expenseService.getSummary(userDetails.getUsername(), year, month));

    }

    @GetMapping("/trends")
    public ResponseEntity<List<TrendResponse>> getTrends(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(required = false) ExpenseCategory category) {
        List<TrendResponse> trends = expenseService.getMonthlyTrends(userDetails.getUsername(), category);
        return ResponseEntity.ok(trends);
    }


    @GetMapping("/top")
    public ResponseEntity<Page<TopExpenseResponse>> getTopExpense(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "5") int limit) {
        Page<TopExpenseResponse> topExpense = expenseService.getTopExpenses(userDetails.getUsername(), limit);

        return ResponseEntity.ok(topExpense);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExpenses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(defaultValue = "PAST_MONTH") String range,
            @RequestParam(required = false) ExpenseCategory expenseCategory
    ) {
        String username = userDetails.getUsername();

        List<Expense> expenses = expenseService.getExpensesforExport(username, range, expenseCategory);

        try {
            String csvContent = csvExportService.exportExpensesToCSV(expenses);
            byte[] bytes = csvContent.getBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "expenses_" + LocalDate.now() + ".csv");
            headers.setContentLength(bytes.length);

            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //____________________________________________________________________________________
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Integer id) {
        return ResponseEntity.ok(expenseService.deleteExpense(id));
    }

    //____________________________________________________________________________________
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Integer id, @Valid @RequestBody ExpenseUpdateRequest expenseUpdateRequest, @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseResponse response = expenseService.updateResponse(expenseUpdateRequest, id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
