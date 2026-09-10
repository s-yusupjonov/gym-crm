package com.gym.crm.controller;

import com.gym.crm.dto.auth.ChangeLoginRequest;
import com.gym.crm.dto.auth.LoginRequest;
import com.gym.crm.dto.auth.LoginResponse;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.security.JwtService;
import com.gym.crm.security.TokenBlacklistService;
import com.gym.crm.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
@Api(tags = "Authentication")
public class AuthenticationController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService,
                                    TokenBlacklistService tokenBlacklistService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    @ApiOperation(value = "Login", notes = "Verifies a trainee's or trainer's username/password and issues a "
            + "bearer token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Credentials are valid, token issued"),
            @ApiResponse(code = 400, message = "Username or password missing"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 423, message = "Account temporarily locked after repeated failed attempts")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authenticationService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PutMapping("/login")
    @ApiOperation(value = "Change login", notes = "Sets a new password for a trainee/trainer, given their current "
            + "credentials.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password changed"),
            @ApiResponse(code = 400, message = "Request is invalid"),
            @ApiResponse(code = 401, message = "Old username/password is invalid"),
            @ApiResponse(code = 423, message = "Account temporarily locked after repeated failed attempts")
    })
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody ChangeLoginRequest request) {
        authenticationService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @ApiOperation(value = "Logout", notes = "Revokes the bearer token used to authenticate this request.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Logged out"),
            @ApiResponse(code = 401, message = "Not authorized")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        tokenBlacklistService.blacklist(jwtService.extractJti(token), jwtService.extractExpiration(token));
        return ResponseEntity.ok().build();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException("Missing or invalid Authorization header; expected a Bearer token");
        }
        return header.substring(BEARER_PREFIX.length());
    }
}