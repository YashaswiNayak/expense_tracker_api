package com.yashaswi.expense_tracker_api.specification;

import com.yashaswi.expense_tracker_api.common.LocalDateRange;
import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpecification {
    public static Specification<Expense> byUser(String username) {
        return username == null ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("user").get("username"), username));
    }

    public static Specification<Expense> byExpenseCategory(ExpenseCategory expenseCategory) {
        return expenseCategory == null ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("category"), expenseCategory));
    }

    public static Specification<Expense> byDateRange(LocalDateRange dateRange) {
        return dateRange == null ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.between(root.get("date"),
                                dateRange.start(), dateRange.end()));
    }

}
