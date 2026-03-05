package com.yashaswi.expense_tracker_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<String> handleAuthenticationNullException(AuthenticationFailedException ex) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleAuthenticationNullException(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(BudgetExceededException.class)
    public ResponseEntity<String> handleBudgetExceeded(BudgetExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage()); // 409
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<String> handleExpenseNotFound(ExpenseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // 404
    }

    @ExceptionHandler(BudgetNotFoundException.class)
    public ResponseEntity<String> handleBudgetNotFound(BudgetNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // 404
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage()); // 401
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<String> handleTokenNotFound(TokenNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage()); // 401
    }

    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<String> handleTokenRevoked(TokenRevokedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage()); // 403
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }


}
