package com.yashaswi.expense_tracker_api.exception;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(Integer id) {
        super("Budget with id: "+id+" not found");
    }
}
