package org.grupouno.parking.it4.controller;


import org.grupouno.parking.it4.dto.LoginResponse;
import org.grupouno.parking.it4.dto.LoginUserDto;
import org.grupouno.parking.it4.dto.RegisterUserDto;
import org.grupouno.parking.it4.dto.ResetPasswordDto;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.security.AuthenticationService;
import org.grupouno.parking.it4.security.JwtService;
import org.grupouno.parking.it4.service.MailService;
import org.grupouno.parking.it4.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.grupouno.parking.it4.utils.Validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final String MESSAGE = "message";
    private final JwtService jwtService;
    private Validations validate = new Validations();
    private final AuthenticationService authenticationService;
    private final MailService emailService;
    private final UserService userService;

    public AuthController(JwtService jwtService, AuthenticationService authenticationService, MailService emailService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
        this.userService = userService;

    }

    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found");
        }
        String verificationCode = validate.generateVerificationCode();
        userService.saveVerificationCode(user.get(), verificationCode);
        emailService.sendVerificationCode(email, verificationCode);
        return ResponseEntity.ok("The email has been sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto request) {
        Optional<User> user = userService.findByEmail(request.getEmail());
        if (user.isEmpty() || !userService.isVerificationCodeValid(user.get(), request.getVerificationCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code invlid");
        }
        userService.changePassword(user.get().getUserId(), request.getNewPassword());
        return ResponseEntity.ok("Password changed");
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterUserDto registerUserDto) {
        Map<String, String> response = new HashMap<>();
        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            response.put(MESSAGE, "User add" + registeredUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(MESSAGE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put(MESSAGE, "Error");
            response.put("err", "An error occurred while adding the user " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

}
