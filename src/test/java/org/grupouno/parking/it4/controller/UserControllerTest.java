package org.grupouno.parking.it4.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityNotFoundException;
import org.grupouno.parking.it4.dto.ChangePasswordDto;
import org.grupouno.parking.it4.dto.UserDto;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.service.UserService;
import org.grupouno.parking.it4.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ProfileService profileService;



    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;
    private ProfileController profileController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testChangePassword() throws Exception {
        ChangePasswordDto passwordDto = new ChangePasswordDto();

        User user = new User();
        user.setUserId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        mockMvc.perform(post("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pastPassword\":\"oldPass\", \"newPassword\":\"newPass\", \"confirmPassword\":\"confirmPass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed"));

        verify(userService, times(1)).updatePassword(1L, "oldPass", "newPass", "confirmPass");
    }

    @Test
    public void testPatchUserId_Success() throws Exception {
        UserDto userDto = new UserDto();

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"));

        verify(userService, times(1)).patchUser(any(UserDto.class), eq(1L));
    }

    @Test
    public void testPatchUserId_UserNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User not found")).when(userService).patchUser(any(UserDto.class), eq(1L));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("User not found"));

        verify(userService, times(1)).patchUser(any(UserDto.class), eq(1L));
    }

    @Test
    public void testPatchUser_Success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"));

        verify(userService, times(1)).patchUser(any(UserDto.class), eq(1L));
    }

    @Test
    public void testPatchUser_UserNotFound() throws Exception {
        User user = new User();
        user.setUserId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        doThrow(new EntityNotFoundException("User not found")).when(userService).patchUser(any(UserDto.class), eq(1L));

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("User not found"));

        verify(userService, times(1)).patchUser(any(UserDto.class), eq(1L));
    }

    @Test
    public void testDeleteUserId_Success() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(userService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteUserId_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("User not found")).when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService, times(1)).delete(1L);
    }

    @Test
    public void testGetAllUsers_Success() throws Exception {
        Page<User> userPage = new PageImpl<>(Collections.singletonList(new User()));
        when(userService.getAllUsers(0, 10, null)).thenReturn(userPage);

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"));

        verify(userService, times(1)).getAllUsers(0, 10, null);
    }

    @Test
    public void testGetAllUsers_Exception() throws Exception {
        when(userService.getAllUsers(0, 10, null)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.err").value("An error get users Error"));

        verify(userService, times(1)).getAllUsers(0, 10, null);
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(userService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteUser_NotFound() throws Exception {
        User user = new User();
        user.setUserId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        doThrow(new IllegalArgumentException("User not found")).when(userService).delete(1L);

        mockMvc.perform(delete("/users"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService, times(1)).delete(1L);
    }

    @Test
    public void testFindUsers_Success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(user));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    public void testFindUsers_NotFound() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(1L);
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        User user = new User();
        when(userService.signup(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User add" + user));

        verify(userService, times(1)).signup(any(UserDto.class));
    }

    @Test
    public void testRegisterUser_BadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid data")).when(userService).signup(any(UserDto.class));

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"));

        verify(userService, times(1)).signup(any(UserDto.class));
    }

    @Test
    public void testFindByEmail_Success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(user));

        verify(userService, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testFindByEmail_NotFound() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/email")
                        .param("email", "test@example.com"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findByEmail("test@example.com");
    }

    @Test
    void updateUserId_success() {
        Long idUser = 1L;
        UserDto userDto = new UserDto();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("MESSAGE", "User Updated Successfully");

        ResponseEntity<Map<String, String>> response = userController.updateUserId(idUser, userDto);

        verify(userService, times(1)).updateUser(userDto, idUser);
        assertEquals(ResponseEntity.ok(expectedResponse), response);
    }

    @Test
    void updateUserId_userNotFound() {
        Long idUser = 1L;
        UserDto userDto = new UserDto();
        when("T").thenThrow(new EntityNotFoundException("User not found"));

        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("MESSAGE", "User not found");

        ResponseEntity<Map<String, String>> response = userController.updateUserId(idUser, userDto);

        verify(userService, times(1)).updateUser(userDto, idUser);
        assertEquals(ResponseEntity.badRequest().body(expectedResponse), response);
    }
    @Test
    void findByEmail_success() {
        String email = "test@example.com";
        User user = new User(); // Asegúrate de tener un constructor adecuado o usar un builder
        user.setEmail(email); // Configura los atributos que necesitas

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("MESSAGE", "User found");
        expectedResponse.put("user", user);

        when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, Object>> response = userController.findByEmail(email);

        verify(userService, times(1)).findByEmail(email);
        assertEquals(ResponseEntity.ok(expectedResponse), response);
    }

    @Test
    void findByEmail_userNotFound() {
        String email = "test@example.com";

        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("MESSAGE", "User found");

        ResponseEntity<Map<String, Object>> response = userController.findByEmail(email);

        verify(userService, times(1)).findByEmail(email);
        assertEquals(ResponseEntity.ok(expectedResponse), response);
    }

    @Test
    void findByEmail_emailIsEmpty() {
        String email = "";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("MESSAGE", "Email is required");

        ResponseEntity<Map<String, Object>> response = userController.findByEmail(email);

        verify(userService, times(0)).findByEmail(email);
        assertEquals(ResponseEntity.badRequest().body(expectedResponse), response);
    }

    @Test
    void findByEmail_internalServerError() {
        String email = "test@example.com";
        when(userService.findByEmail(email)).thenThrow(new RuntimeException("Database error"));

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("MESSAGE", "ERROR");
        expectedResponse.put("err", "An error get users Database error");

        ResponseEntity<Map<String, Object>> response = userController.findByEmail(email);

        verify(userService, times(1)).findByEmail(email);
        assertEquals(ResponseEntity.internalServerError().body(expectedResponse), response);
    }
    @Test
    void updateUser_success() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User"); // Configura los atributos que necesites

        User customUserDetails = mock(User.class);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserId()).thenReturn(1L); // Ajusta según sea necesario

        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("MESSAGE", "User Updated Successfully");

        ResponseEntity<Map<String, String>> response = userController.updateUser(userDto);

        verify(userService, times(1)).updateUser(userDto, customUserDetails.getUserId());
        assertEquals(ResponseEntity.ok(expectedResponse), response);
    }

    @Test
    void updateUser_userNotFound() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");

        User customUserDetails = mock(User.class);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserId()).thenReturn(1L);

        when("T").thenThrow(new EntityNotFoundException("User not found"));

        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("MESSAGE", "User not found");

        ResponseEntity<Map<String, String>> response = userController.updateUser(userDto);

        verify(userService, times(1)).updateUser(userDto, customUserDetails.getUserId());
        assertEquals(ResponseEntity.badRequest().body(expectedResponse), response);
    }

}
