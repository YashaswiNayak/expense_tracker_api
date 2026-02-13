package com.yashaswi.expense_tracker_api.service;

import com.yashaswi.expense_tracker_api.common.LocalDateRange;
import com.yashaswi.expense_tracker_api.dto.expense.*;
import com.yashaswi.expense_tracker_api.entity.Budget;
import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.entity.User;
import com.yashaswi.expense_tracker_api.enums.DateRange;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import com.yashaswi.expense_tracker_api.exception.BadRequestException;
import com.yashaswi.expense_tracker_api.exception.BudgetExceededException;
import com.yashaswi.expense_tracker_api.exception.ExpenseNotFoundException;
import com.yashaswi.expense_tracker_api.exception.UserNotFoundException;
import com.yashaswi.expense_tracker_api.mapper.EntityToDtoMapper;
import com.yashaswi.expense_tracker_api.repository.BudgetRepository;
import com.yashaswi.expense_tracker_api.repository.ExpenseRepository;
import com.yashaswi.expense_tracker_api.repository.UserRepository;
import com.yashaswi.expense_tracker_api.specification.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetService budgetService;

    public Page<ExpenseResponse> getAllExpenses(
            String username,
            Pageable pageable,
            ExpenseFilter expenseFilter
    ) {
        LocalDateRange effectiveRange = resolveRange(expenseFilter.getStartDate(), expenseFilter.getEndDate(), expenseFilter.getDateRange());

        Specification<Expense> spec = ExpenseSpecification.byUser(username);

        if (effectiveRange != null) {
            log.info("Filtering based on effective range: {}", effectiveRange);
            spec = spec.and(ExpenseSpecification.byDateRange(effectiveRange));
        }

        if (expenseFilter.getCategories() != null) {
            log.info("Filtering based on expense category: {}", expenseFilter.getCategories());
            spec = spec.and(ExpenseSpecification.byExpenseCategories(expenseFilter.getCategories()));
        }

        if (expenseFilter.getMinAmount() != null && expenseFilter.getMaxAmount() != null) {
            spec = spec.and(ExpenseSpecification.byAmountRange(expenseFilter.getMinAmount(), expenseFilter.getMaxAmount()));
        }

        Page<Expense> page = expenseRepository.findAll(spec, pageable);
        return page.map(EntityToDtoMapper::toDto);
    }

    private LocalDateRange resolveRange(LocalDate startDate, LocalDate endDate, DateRange range) throws BadRequestException {
        if (range != null && (startDate != null || endDate != null)) {
            throw new BadRequestException("Provide either range OR startDate/endDate, not both");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("The start date cannot be after end date");
        }
        if (startDate != null || endDate != null) {
            LocalDate start = startDate != null ? startDate : LocalDate.MIN;
            LocalDate end = endDate != null ? endDate : LocalDate.MAX;
            return new LocalDateRange(start, end);
        }
        if (range != null) {
            LocalDate today = LocalDate.now();
            return switch (range) {
                case PAST_WEEK -> new LocalDateRange(today.minusWeeks(1), today);
                case PAST_MONTH -> new LocalDateRange(today.minusMonths(1), today);
                case LAST_3_MONTHS -> new LocalDateRange(today.minusMonths(3), today);
            };
        }
        return null;
    }

    //__________________________________________________________________________________
    public ExpenseResponse createNewExpense(ExpenseCreation expenseCreation, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new UserNotFoundException("Creator not found " + creatorUsername));

        Expense expense = Expense.builder()
                .amount(expenseCreation.getAmount())
                .category(expenseCreation.getCategory() != null ?
                        expenseCreation.getCategory() : ExpenseCategory.MISCELLANEOUS)
                .description(expenseCreation.getDescription())
                .date(expenseCreation.getLocalDate() != null ?
                        expenseCreation.getLocalDate() : LocalDate.now())
                .user(creator)
                .build();

        // CHECK BUDGET BEFORE SAVING (projected spend)
        YearMonth currentMonth = YearMonth.from(expense.getDate());  // ← Use expense.date (before save)

        Optional<Budget> matchingBudget = budgetRepository
                .findByUser_UsernameAndCategoryAndPeriod(creatorUsername, expense.getCategory(), currentMonth);

        if (matchingBudget.isPresent()) {
            Budget budget = matchingBudget.get();
            Double projectedSpent = budget.getSpent() + expense.getAmount();
            if (projectedSpent > budget.getBudgetLimit()) {
                throw new BudgetExceededException(
                        "Budget exceeded for " + expense.getCategory() + "! " +
                                "Projected: ₹" + projectedSpent + " > Limit: ₹" + budget.getBudgetLimit()
                );
            }
        }

        // ✅ SAVE EXPENSE
        Expense savedExpense = expenseRepository.save(expense);

        // ✅ UPDATE BUDGET (increment)
        String response = budgetService.updateBudget(creatorUsername, savedExpense.getCategory(),
                currentMonth, savedExpense.getAmount());
        log.info(response);
        return EntityToDtoMapper.toDto(savedExpense);
    }

    //__________________________________________________________________________________
    public String deleteExpense(Integer expenseId) {
        expenseRepository.deleteById(expenseId);
        return "Expense deleted successfully";
    }

    //__________________________________________________________________________________
    public ExpenseResponse updateResponse(ExpenseUpdateRequest expenseUpdateRequest, Integer id, String creatorUsername) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found: " + id));
        if (!expense.getUser().getUsername().equals(creatorUsername)) {
            throw new UserNotFoundException("You cannot modify this expense");
        }

        Double oldAmount = expense.getAmount();
        Double newAmount = expenseUpdateRequest.getAmount();
        Double delta = newAmount - oldAmount;


        expense.setAmount(expenseUpdateRequest.getAmount());
        expense.setDescription(expenseUpdateRequest.getDescription());
        expense.setCategory(expenseUpdateRequest.getCategory());
        expense.setDate(expenseUpdateRequest.getDate());
        Expense saved = expenseRepository.save(expense);

        YearMonth currentMonth = YearMonth.from(saved.getDate());
        String response = budgetService.updateBudget(creatorUsername, saved.getCategory(), currentMonth, delta);
        log.info(response);
        return EntityToDtoMapper.toDto(saved);
    }

    public List<ExpenseSummaryDto> getSummary(
            String userName,
            Integer year,
            Integer month) {
        return expenseRepository.getMonthlySummary(userName, year, month);
    }

}
