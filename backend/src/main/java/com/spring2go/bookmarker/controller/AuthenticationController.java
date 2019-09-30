package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.config.BookmarkerProperties;
import com.spring2go.bookmarker.config.security.CustomUserDetailsService;
import com.spring2go.bookmarker.config.security.SecurityUser;
import com.spring2go.bookmarker.config.security.TokenHelper;
import com.spring2go.bookmarker.dto.AuthenticationRequest;
import com.spring2go.bookmarker.dto.AuthenticationResponse;
import com.spring2go.bookmarker.dto.UserDto;
import com.spring2go.bookmarker.model.User;
import com.spring2go.bookmarker.common.SecurityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "api")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private TokenHelper tokenHelper;
    @Autowired
    private BookmarkerProperties bookmarkerProperties;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = {"/auth/login"})
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest credentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        String jws = tokenHelper.generateToken(user.getUsername());
        return new AuthenticationResponse().setAccessToken(jws).setExpiresIn(bookmarkerProperties.getJwt().getExpiresIn());
    }

    @PostMapping(value = {"/auth/refresh"})
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<AuthenticationResponse> refreshAuthenticationToken(HttpServletRequest request) {
        String authToken = tokenHelper.getToken(request);
        if (authToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String email = tokenHelper.getUsernameFromToken(authToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        boolean validToken = tokenHelper.validateToken(authToken, userDetails);
        if (validToken) {
            String refreshToken = tokenHelper.refreshToken(authToken);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setAccessToken(refreshToken);
            authenticationResponse.setExpiresIn(bookmarkerProperties.getJwt().getExpiresIn());
            return ResponseEntity.ok(authenticationResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> me() {
        User user = SecurityUtils.loginUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(userDto);
    }
}
