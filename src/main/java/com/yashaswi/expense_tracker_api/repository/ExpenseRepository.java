package com.yashaswi.expense_tracker_api.repository;

import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface ExpenseRepository extends JpaRepository<Expense, Integer>, JpaSpecificationExecutor<Expense> {
    Page<Expense> findByUser_Username(String username, Pageable pageable);

    Page<Expense> findByUser_UsernameAndDateBetween(String username, LocalDate start, LocalDate end, Pageable pageable);

    Page<Expense> findByUser_UsernameAndExpenseCategory(String username, ExpenseCategory expenseCategory, Pageable pageable);
}
