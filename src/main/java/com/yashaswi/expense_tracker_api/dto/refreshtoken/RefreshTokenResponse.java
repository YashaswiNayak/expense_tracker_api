package com.yashaswi.expense_tracker_api.dto.refreshtoken;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
