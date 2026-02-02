package com.yashaswi.expense_tracker_api.entity;

import com.yashaswi.expense_tracker_api.enums.ExpenseCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@Builder
@Entity
@Table(name = "budget")
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false)
    private YearMonth period;

    @Positive
    @Column(nullable = false)
    private Double budgetLimit;

    @Column(nullable = false)
    private Double spent = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    @PreUpdate
    public void ensureNonNegative() {
        if (spent < 0) spent = 0.0;
    }
}
