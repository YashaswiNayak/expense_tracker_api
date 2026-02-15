package com.yashaswi.expense_tracker_api.config;

import com.yashaswi.expense_tracker_api.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanup implements ApplicationRunner {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void run(ApplicationArguments args){
        refreshTokenRepository.deleteExpiredTokens();
    }
}
