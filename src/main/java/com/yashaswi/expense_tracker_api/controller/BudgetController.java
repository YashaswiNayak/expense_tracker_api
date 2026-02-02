package com.yashaswi.expense_tracker_api.controller;

import com.yashaswi.expense_tracker_api.dto.budget.BudgetCreation;
import com.yashaswi.expense_tracker_api.dto.budget.BudgetResponse;
import com.yashaswi.expense_tracker_api.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetCreation budgetCreation,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        BudgetResponse budgetResponse = budgetService.createBudget(budgetCreation, userDetails.getUsername());
        return ResponseEntity.ok(budgetResponse);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String period
    ) {
        List<BudgetResponse> budgets = budgetService.getAllBudgets(userDetails.getUsername(), period);
        return ResponseEntity.ok(budgets);
    }
}
