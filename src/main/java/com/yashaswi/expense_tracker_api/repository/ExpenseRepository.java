package com.yashaswi.expense_tracker_api.repository;

import com.yashaswi.expense_tracker_api.dto.expense.ExpenseSummaryDto;
import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer>, JpaSpecificationExecutor<Expense> {

    @Query("""
            SELECT new com.yashaswi.expense_tracker_api.dto.expense.ExpenseSummaryDto(
                e.category, 
                SUM(e.amount), 
                COUNT(e), 
                AVG(e.amount)
            )
            FROM Expense e 
            WHERE e.user.username = :username 
              AND (:year IS NULL OR YEAR(e.date) = :year)
              AND (:month IS NULL OR MONTH(e.date) = :month)
            GROUP BY e.category
            ORDER BY SUM(e.amount) DESC
            """)
    List<ExpenseSummaryDto> getMonthlySummary(
            @Param("username") String username,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) 
            FROM Expense e 
            WHERE e.user.username = :username 
              AND e.category = :category 
              AND YEAR(e.date) = :year 
              AND MONTH(e.date) = :month
            """)
    Double sumAmountByUserUsernameCategoryAndPeriod(
            @Param("username") String username,
            @Param("category") ExpenseCategory category,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

}
