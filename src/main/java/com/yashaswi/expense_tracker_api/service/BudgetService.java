package com.yashaswi.expense_tracker_api.service;

import com.yashaswi.expense_tracker_api.dto.budget.BudgetCreation;
import com.yashaswi.expense_tracker_api.dto.budget.BudgetResponse;
import com.yashaswi.expense_tracker_api.dto.budget.BudgetUpdateRequest;
import com.yashaswi.expense_tracker_api.entity.Budget;
import com.yashaswi.expense_tracker_api.entity.User;
import com.yashaswi.expense_tracker_api.exception.BadRequestException;
import com.yashaswi.expense_tracker_api.exception.UserNotFoundException;
import com.yashaswi.expense_tracker_api.mapper.EntityToDtoMapper;
import com.yashaswi.expense_tracker_api.repository.BudgetRepository;
import com.yashaswi.expense_tracker_api.repository.ExpenseRepository;
import com.yashaswi.expense_tracker_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public BudgetResponse createBudget(BudgetCreation budgetCreation, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername).orElseThrow(() -> new UserNotFoundException("Creator not found" + creatorUsername));

        YearMonth period = YearMonth.parse(budgetCreation.getPeriod());

        budgetRepository.findByUser_UsernameAndCategoryAndPeriod(creatorUsername,
                        budgetCreation.getExpenseCategory(), period)
                .ifPresent(b -> {
                    throw new BadRequestException("Budget already exists for " + budgetCreation.getExpenseCategory() + " in " + period);
                });

        Budget budget = Budget.builder().user(creator).budgetLimit(budgetCreation.getLimit()).category(budgetCreation.getExpenseCategory()).period(period).user(creator).build();
        Budget saveBudget = budgetRepository.save(budget);
        return EntityToDtoMapper.toDto(saveBudget);
    }

    public List<BudgetResponse> getAllBudgets(String username, String period) {
        YearMonth periodYM = period != null ? YearMonth.parse(period) : null;

        List<Budget> budgets = periodYM == null ? budgetRepository.findByUser_Username(username) : budgetRepository.findByUser_UsernameAndPeriod(username, periodYM);

        return budgets.stream().map(EntityToDtoMapper::toDto).toList();
    }

    public String updateBudget(String username, BudgetUpdateRequest budgetUpdateRequest) {
        budgetRepository.findByUser_UsernameAndCategoryAndPeriod(username, budgetUpdateRequest.getExpenseCategory(), budgetUpdateRequest.getPeriod())
                .ifPresent(budget -> {
                    budget.setSpent(Math.max(0, budget.getSpent() + budgetUpdateRequest.getAmountDelta()));
                    budgetRepository.save(budget);
                });
        return "Budget updated for expense category -> " + budgetUpdateRequest.getExpenseCategory() + "<- for the period" + budgetUpdateRequest.getPeriod();
    }

}
