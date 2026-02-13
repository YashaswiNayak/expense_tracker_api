package com.yashaswi.expense_tracker_api.specification;

import com.yashaswi.expense_tracker_api.common.LocalDateRange;
import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ExpenseSpecification {
    public static Specification<Expense> byUser(String username) {
        return username == null ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("user").get("username"), username));
    }

    public static Specification<Expense> byExpenseCategories(List<ExpenseCategory> categories) {
        if(categories == null || categories.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->root.get("category").in(categories);
    }

    public static Specification<Expense> byDateRange(LocalDateRange dateRange) {
        return dateRange == null ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.between(root.get("date"),
                                dateRange.start(), dateRange.end()));
    }

    public static Specification<Expense> byAmountRange(Double minAmount, Double maxAmount) {
        if (minAmount == null || maxAmount == null) return null;

        return (root, query, cb) ->
                cb.between(root.get("amount"), minAmount, maxAmount);

    }
}
