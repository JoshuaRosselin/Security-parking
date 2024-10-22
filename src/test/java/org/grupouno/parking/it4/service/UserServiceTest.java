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
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(audithService).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    void findById_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(audithService, never()).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    void save_ShouldSaveUser() {
        // Arrange
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.save(user);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsers_ShouldReturnPagedUsers() {
        // Arrange
        int page = 0;
        int size = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(new User()), pageRequest, 1);
        when(userRepository.findAll(pageRequest)).thenReturn(userPage);

        // Act
        Page<User> result = userService.getAllUsers(page, size, null);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(audithService).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    void delete_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.delete(userId));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateUser_ShouldUpdateUserFields() {
        // Arrange
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setProfileId(1L);
        Profile profile = new Profile();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        // Act
        userService.updateUser(userDto, userId);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updatePassword_ShouldThrowException_WhenPasswordIsIncorrect() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "oldPass";
        String newPassword = "newPass";
        String confirmPassword = "newPass";
        User user = new User();
        user.setPassword("encodedOldPassword");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(userId, oldPassword, newPassword, confirmPassword));
    }

    @Test
    void signup_ShouldThrowException_WhenEmailIsInvalid() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("invalidEmail");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.signup(userDto));
    }

    @Test
    void patchUser_ShouldUpdateUserFieldsPartially() {
        // Arrange
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setProfileId(1L);
        Profile profile = new Profile();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        // Act
        userService.patchUser(userDto, userId);

        // Assert
        verify(userRepository).save(any(User.class));
    }
}
