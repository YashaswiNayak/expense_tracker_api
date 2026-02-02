package com.yashaswi.expense_tracker_api.mapper;

import com.yashaswi.expense_tracker_api.dto.budget.BudgetResponse;
import com.yashaswi.expense_tracker_api.dto.expense.ExpenseResponse;
import com.yashaswi.expense_tracker_api.dto.user.UserResponse;
import com.yashaswi.expense_tracker_api.entity.Budget;
import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.entity.User;

public class EntityToDtoMapper {
    private EntityToDtoMapper() {
    }

    public static ExpenseResponse toDto(Expense expense) {
        if (expense == null) {
            return null;
        }
        ExpenseResponse expenseResponse = new ExpenseResponse();
        expenseResponse.setId(expense.getId());
        expenseResponse.setCategory(expense.getCategory());
        expenseResponse.setAmount(expense.getAmount());
        expenseResponse.setLocalDate(expense.getDate());
        expenseResponse.setDescription(expense.getDescription());
        if (expense.getUser() != null) {
            expenseResponse.setCreator(toDto(expense.getUser()));
        }
        return expenseResponse;
    }

    private static UserResponse toDto(User user) {
        if (user == null) {
            return null;
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        return userResponse;
    }

    public static BudgetResponse toDto(Budget budget) {
        if (budget == null) {
            return null;
        }
        BudgetResponse budgetResponse = new BudgetResponse();
        budgetResponse.setId(budget.getId());
        budgetResponse.setLimit(budget.getBudgetLimit());
        budgetResponse.setExpenseCategory(budget.getCategory());
        budgetResponse.setSpent(budget.getSpent());
        budgetResponse.setPeriod(budget.getPeriod());
        if (budget.getUser() != null) {
            budgetResponse.setCreator(toDto(budget.getUser()));
        }
        return budgetResponse;
    }
}
