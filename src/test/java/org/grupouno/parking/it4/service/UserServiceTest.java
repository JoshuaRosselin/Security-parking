package org.grupouno.parking.it4.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.grupouno.parking.it4.dto.UserDto;
import org.grupouno.parking.it4.exceptions.DpiException;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.exceptions.UserNotFoundException;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.repository.ProfileRepository;
import org.grupouno.parking.it4.repository.UserRepository;
import org.grupouno.parking.it4.utils.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AudithService audithService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Validations validations;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(audithService).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    void findById_ShouldThrowException_WhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(audithService, never()).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    void save_ShouldSaveUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);

        assertNotNull(result);
        verify(userRepository).save(user);
    }



    @Test
    void delete_ShouldThrowException_WhenUserDoesNotExist() {

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> userService.delete(userId));
        verify(userRepository, never()).deleteById(anyLong());
    }


    @Test
    void updatePassword_ShouldThrowException_WhenPasswordIsIncorrect() {

        Long userId = 1L;
        String oldPassword = "oldPass";
        String newPassword = "newPass";
        String confirmPassword = "newPass";
        User user = new User();
        user.setPassword("encodedOldPassword");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(userId, oldPassword, newPassword, confirmPassword));
    }

    @Test
    void signup_ShouldThrowException_WhenEmailIsInvalid() {

        UserDto userDto = new UserDto();
        userDto.setEmail("invalidEmail");

        assertThrows(IllegalArgumentException.class, () -> userService.signup(userDto));
    }

}
