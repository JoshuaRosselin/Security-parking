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
import org.grupouno.parking.it4.utils.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MailService mailService;

    @Mock
    private UserService userService;

    @Mock
    private Validations validations;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testForgotPassword_EmailExists() {
        String email = "test@example.com";
        User user = new User();
        user.setUserId(1L);
        user.setEmail(email);

        when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, String>> response = authController.forgotPassword(email);

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService).saveVerificationCode(eq(user), codeCaptor.capture());
        String generatedCode = codeCaptor.getValue();

        verify(mailService).sendVerificationCode(email, generatedCode);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email has been send", response.getBody().get("message"));
    }

    @Test
    void testForgotPassword_EmailNotFound() {
        String email = "test@example.com";
        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = authController.forgotPassword(email);

        verify(mailService, never()).sendVerificationCode(anyString(), anyString());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email not found", response.getBody().get("message"));
    }

    // Test for resetPassword
    @Test
    void testResetPassword_Success() {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setEmail("test@example.com");
        resetPasswordDto.setVerificationCode("123456");
        resetPasswordDto.setNewPassword("newPassword");
        resetPasswordDto.setConfirmPassword("newPassword");

        User user = new User();
        user.setUserId(1L);  // Inicializar campos necesarios
        user.setEmail("test@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userService.isVerificationCodeValid(user, "123456")).thenReturn(true);

        ResponseEntity<Map<String, String>> response = authController.resetPassword(resetPasswordDto);

        verify(userService).changePassword(user.getUserId(), "newPassword", "newPassword");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed", response.getBody().get("message"));
    }

    @Test
    void testResetPassword_InvalidCode() {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setEmail("test@example.com");
        resetPasswordDto.setVerificationCode("wrongCode");

        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userService.isVerificationCodeValid(user, "wrongCode")).thenReturn(false);

        ResponseEntity<Map<String, String>> response = authController.resetPassword(resetPasswordDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Code invalid", response.getBody().get("message"));
    }

    // Test for register
    @Test
    void testRegister_Success() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        User user = new User();
        user.setUserId(1L);  // Inicializar campos necesarios
        user.setEmail("test@example.com");

        when(authenticationService.signup(registerUserDto)).thenReturn(user);

        ResponseEntity<Map<String, String>> response = authController.register(registerUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User add" + user, response.getBody().get("message"));
    }

    @Test
    void testRegister_DuplicateEmail() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        when(authenticationService.signup(registerUserDto)).thenThrow(new IllegalArgumentException("Email already exists"));

        ResponseEntity<Map<String, String>> response = authController.register(registerUserDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already exists", response.getBody().get("message"));
    }

    // Test for authenticate (login)
    @Test
    void testAuthenticate_Success() {
        LoginUserDto loginUserDto = new LoginUserDto();
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(authenticationService.authenticate(loginUserDto)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        ResponseEntity<LoginResponse> response = authController.authenticate(loginUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwtToken", response.getBody().getToken());
    }
}
