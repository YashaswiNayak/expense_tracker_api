package com.yashaswi.expense_tracker_api.mapper;

import com.yashaswi.expense_tracker_api.dto.ExpenseResponse;
import com.yashaswi.expense_tracker_api.dto.UserResponse;
import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.entity.User;

public class EntityToDtoMapper {
    private EntityToDtoMapper() {}

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
}
