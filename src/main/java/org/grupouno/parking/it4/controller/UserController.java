package org.grupouno.parking.it4.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.grupouno.parking.it4.dto.ChangePasswordDto;
import org.grupouno.parking.it4.dto.UserDto;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private static final String MESSAGE = "message";
    private static final String ERROR = "Error";

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUserDetails = (User) authentication.getPrincipal();
        userService.updatePassword(customUserDetails.getUserId(), password.getPastPassword(), password.getNewPassword(), password.getConfirmPassword());
        return ResponseEntity.ok("Password changed");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @PatchMapping("/patch-user/{idUser}")
    public ResponseEntity<Map<String, String>> patchUserId(@PathVariable Long idUser, @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.patchUser(userDto, idUser);
            response.put(MESSAGE, "User updated successfully");
            return ResponseEntity.ok(response); // 200 OK
        } catch (EntityNotFoundException e) {
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            response.put(ERROR, "Invalid data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put(ERROR, "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @PatchMapping("/patch-user")
    public ResponseEntity<Map<String, String>> patchUser( @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUserDetails = (User) authentication.getPrincipal();
        try {
            userService.patchUser(userDto, customUserDetails.getUserId());
            response.put(MESSAGE, "User updated successfully");
            return ResponseEntity.ok(response); // 200 OK
        } catch (EntityNotFoundException e) {
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            response.put(ERROR, "Invalid data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put(ERROR, "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @PutMapping("/update-user/{idUser}")
    public ResponseEntity<Map<String, String>> updateUserId(@PathVariable Long idUser, @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        try{
            userService.updateUser(userDto, idUser);
            response.put(MESSAGE, "User Updated Successfully");
            return ResponseEntity.ok(response);

        }catch(EntityNotFoundException e){
            response.put(MESSAGE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @PutMapping("/update-user")
    public ResponseEntity<Map<String, String>> updateUser( @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUserDetails = (User) authentication.getPrincipal();
        try{
            userService.updateUser(userDto, customUserDetails.getUserId());
            response.put(MESSAGE, "User Updated Successfully");
            return ResponseEntity.ok(response);

        }catch(EntityNotFoundException e){
            response.put(MESSAGE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @GetMapping("/find-user/{idUser}")
    public ResponseEntity<Map<String, String>> findUsers(@PathVariable Long idUser) {
        Map<String, String> response = new HashMap<>();
        try{
            Optional<User> user = userService.findById(idUser);
            if (user.isPresent()) {
                response.put(MESSAGE, user.toString() );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (IllegalArgumentException e) {
            response.put(MESSAGE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }catch (Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error finding user " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }

    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @GetMapping("/get-users")
    public ResponseEntity<Map<String, String>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Map<String, String> response = new HashMap<>();
        try{
            Page<User> userPage = userService.getAllUsers(page, size);
            response.put(MESSAGE, "Users retrieved successfully");
            response.put("users", userPage.getContent().toString());
            response.put("totalPages", String.valueOf(userPage.getTotalPages()));
            response.put("currentPage", String.valueOf(userPage.getNumber()));
            response.put("totalElements", String.valueOf(userPage.getTotalElements()));
            return ResponseEntity.ok(response);
        }catch(Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error get users " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @DeleteMapping("/delete-user/{idUser}")
    public ResponseEntity<Map<String, String>> deleteUserId(@PathVariable Long idUser) {
        Map<String, String> response = new HashMap<>();
        try{
            userService.delete(idUser);
            response.put(MESSAGE, "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(MESSAGE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error ocurred deliting user " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }

    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUDITH')")
    @DeleteMapping("/delete-user")
    public ResponseEntity<Map<String, String>> deleteUser() {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUserDetails = (User) authentication.getPrincipal();
        try{
            userService.delete(customUserDetails.getUserId());
            response.put(MESSAGE, "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(MESSAGE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error ocurred deliting user " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }

    }


}
