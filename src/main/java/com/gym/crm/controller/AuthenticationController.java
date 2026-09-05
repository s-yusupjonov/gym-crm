package com.gym.crm.controller;

import com.gym.crm.dto.auth.ChangeLoginRequest;
import com.gym.crm.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@Validated
@Api(tags = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping
    @ApiOperation(value = "Login", notes = "Verifies a trainee's or trainer's username/password. Safe, idempotent.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Credentials are valid"),
            @ApiResponse(code = 400, message = "Username or password missing"),
            @ApiResponse(code = 401, message = "Invalid username or password")
    })
    public ResponseEntity<Void> login(
            @ApiParam(value = "Username", required = true)
            @NotBlank(message = "Username is required") @RequestParam String username,
            @ApiParam(value = "Password", required = true)
            @NotBlank(message = "Password is required") @RequestParam String password) {
        authenticationService.authenticate(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @ApiOperation(value = "Change login", notes = "Sets a new password for a trainee/trainer, given their current "
            + "credentials. PUT semantics: it sets the password resource to an explicit new value, so replaying the "
            + "exact same call from the same starting state always ends in the same state (password = newPassword). "
            + "Note that once applied, a literal replay will fail authentication, since oldPassword no longer "
            + "matches the now-current password - that is a precondition check, not a violation of idempotency.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password changed"),
            @ApiResponse(code = 400, message = "Request is invalid"),
            @ApiResponse(code = 401, message = "Old username/password is invalid")
    })
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody ChangeLoginRequest request) {
        authenticationService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}