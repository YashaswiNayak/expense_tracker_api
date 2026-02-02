package com.yashaswi.expense_tracker_api.repository;

import com.yashaswi.expense_tracker_api.entity.Budget;
import com.yashaswi.expense_tracker_api.entity.User;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    Optional<Budget> findByUser_UsernameAndCategoryAndPeriod(
            String username, ExpenseCategory category, YearMonth period);

    List<Budget> findByUser_UsernameAndPeriod(
            String username, YearMonth period);

    List<Budget> findByUser_Username(String username);
}
