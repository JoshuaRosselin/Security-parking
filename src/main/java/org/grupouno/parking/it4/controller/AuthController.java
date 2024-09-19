package org.grupouno.parking.it4.controller;

import org.grupouno.parking.it4.dto.LoginResponse;
import org.grupouno.parking.it4.dto.LoginUserDto;
import org.grupouno.parking.it4.dto.RegisterUserDto;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.security.AuthenticationService;
import org.grupouno.parking.it4.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.grupouno.parking.it4.utils.Validations;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final String MESSAGE = "message";
    private final JwtService jwtService;
    private Validations validate = new Validations();
    private final AuthenticationService authenticationService;

    public AuthController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
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
