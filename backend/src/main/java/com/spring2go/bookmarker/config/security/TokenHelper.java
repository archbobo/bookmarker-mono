package com.spring2go.bookmarker.config.security;

import com.spring2go.bookmarker.config.BookmarkerProperties;
import com.spring2go.bookmarker.config.TimeProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenHelper {
    @Autowired
    private BookmarkerProperties bookmarkerProperties;

    @Autowired
    private TimeProvider timeProvider;

    private static final int MILLIS_PER_SECOND = 1000;
    private static final String AUDIENCE_WEB = "web";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    public String getUsernameFromToken(String token) {
        return this.getAllClaimsFromToken(token).getSubject();
    }

    public String refreshToken(String token) {
        Claims claims = this.getAllClaimsFromToken(token);
        claims.setIssuedAt(timeProvider.now());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(signatureAlgorithm, bookmarkerProperties.getJwt().getSecret())
                .compact();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setIssuer(bookmarkerProperties.getJwt().getIssuer())
                .setSubject(username)
                .setAudience(AUDIENCE_WEB)
                .setIssuedAt(timeProvider.now())
                .setExpiration(generateExpirationDate())
                .signWith(signatureAlgorithm, bookmarkerProperties.getJwt().getSecret())
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(bookmarkerProperties.getJwt().getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    private Date generateExpirationDate() {
        return new Date(timeProvider.now().getTime() +
                bookmarkerProperties.getJwt().getExpiresIn() * MILLIS_PER_SECOND);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return StringUtils.equals(getUsernameFromToken(token), userDetails.getUsername());
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(bookmarkerProperties.getJwt().getHeader());
    }
}
