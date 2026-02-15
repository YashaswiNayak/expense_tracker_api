package com.yashaswi.expense_tracker_api.security;

import com.yashaswi.expense_tracker_api.dto.refreshtoken.RefreshTokenResponse;
import com.yashaswi.expense_tracker_api.entity.RefreshToken;
import com.yashaswi.expense_tracker_api.entity.User;
import com.yashaswi.expense_tracker_api.exception.TokenExpiredException;
import com.yashaswi.expense_tracker_api.exception.TokenNotFoundException;
import com.yashaswi.expense_tracker_api.exception.TokenRevokedException;
import com.yashaswi.expense_tracker_api.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-expiration}")
    private long jwtAccessExpiration;
    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetail) {
        return generateToken(new HashMap<>(), userDetail, jwtAccessExpiration);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "username", user.getUsername()
        );
        return generateToken(claims, user, jwtRefreshExpiration);
    }


    public RefreshTokenResponse generateTokenPair(User user) {
        String accessToken = generateAccessToken(user);
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignored) {
        }

        String refreshToken = generateRefreshToken(user);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtRefreshExpiration))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // Username is stored in "sub" claim
                .issuedAt(new Date(System.currentTimeMillis())) // Token creation time
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000L)) // Expiry time
                .signWith(getSigningKey()) // Sign with secret key
                .compact();
    }

    // Backward compatibility overload
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return generateToken(extraClaims, userDetails, jwtAccessExpiration * 1000L);  // ✅
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())           // replaces setSigningKey()
                .build()
                .parseSignedClaims(token)              // replaces parseClaimsJws()
                .getPayload();                          // replaces getBody()
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new TokenRevokedException("Refresh token revoked");
        }
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh token expired");
        }
        return refreshToken;
    }
}
