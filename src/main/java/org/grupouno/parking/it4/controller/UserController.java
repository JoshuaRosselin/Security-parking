package org.grupouno.parking.it4.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.grupouno.parking.it4.dto.ChangePasswordDto;
import org.grupouno.parking.it4.dto.UserDto;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private static final String MESSAGE = "message";
    private static final String ERROR = "Error";

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @PostMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUserDetails = (User) authentication.getPrincipal();
        userService.updatePassword(customUserDetails.getUserId(), password.getPastPassword(), password.getNewPassword(), password.getConfirmPassword());
        logger.info("Password changed succesfully");
        return ResponseEntity.ok("Password changed");
    }

    @RolesAllowed({"ADMIN", "AUDITH"})
    @PatchMapping("/{idUser}")
    public ResponseEntity<Map<String, String>> patchUserId(@PathVariable Long idUser, @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.patchUser(userDto, idUser);
            response.put(MESSAGE, "User updated successfully");
            logger.info("User {} updated", idUser);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put(ERROR, e.getMessage());
            logger.error("User not {} not found, {}", idUser, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            response.put(ERROR, "Invalid data: " + e.getMessage());
            logger.error("Profile not found to updated, {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put(ERROR, "An error occurred: " + e.getMessage());
            logger.error("Fail patch user {}, {}", idUser, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @PatchMapping("")
    public ResponseEntity<Map<String, String>> patchUser( @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUserDetails = (User) authentication.getPrincipal();
        try {
            userService.patchUser(userDto, customUserDetails.getUserId());
            response.put(MESSAGE, "User updated successfully");
            logger.info("User updated succesfully");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put(ERROR, e.getMessage());
            logger.error("User not found, {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Profile not found, {}", e.getMessage());
            response.put(ERROR, "Invalid data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put(ERROR, "An error occurred: " + e.getMessage());
            logger.error("Faild patch user, {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RolesAllowed({"ADMIN", "AUDITH"})
    @PutMapping("/{idUser}")
    public ResponseEntity<Map<String, String>> updateUserId(@PathVariable Long idUser, @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        try{
            userService.updateUser(userDto, idUser);
            response.put(MESSAGE, "User Updated Successfully");
            logger.info("User User Updated Successfully, {}", idUser);
            return ResponseEntity.ok(response);

        }catch(EntityNotFoundException e){
            response.put(MESSAGE, e.getMessage());
            logger.error("User not found {}, {}",idUser,  e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @PutMapping("")
    public ResponseEntity<Map<String, String>> updateUser( @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUserDetails = (User) authentication.getPrincipal();
        try{
            userService.updateUser(userDto, customUserDetails.getUserId());
            response.put(MESSAGE, "User Updated Successfully");
            logger.info("User has been updated");
            return ResponseEntity.ok(response);

        }catch(EntityNotFoundException e){
            response.put(MESSAGE, e.getMessage());
            logger.error("User {} not found", userDto.getName());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RolesAllowed({"ADMIN", "AUDITH"})
    @GetMapping("/{idUser}")
    public ResponseEntity<Map<String, String>> findUsers(@PathVariable Long idUser) {
        Map<String, String> response = new HashMap<>();
        try{
            Optional<User> user = userService.findById(idUser);
            if (user.isPresent()) {
                response.put(MESSAGE, user.toString() );
                logger.info("Get user {}", idUser);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (IllegalArgumentException e) {
            response.put(MESSAGE, e.getMessage());
            logger.error("User not found");
            return ResponseEntity.badRequest().body(response);
        }catch (Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error finding user " + e.getMessage());
            logger.error("Fail find user {}", idUser);
            return ResponseEntity.internalServerError().body(response);
        }

    }

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @GetMapping("")
    public ResponseEntity<Map<String, String>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Map<String, String> response = new HashMap<>();
        try{
            Page<User> userPage = userService.getAllUsers(page, size);
            response.put(MESSAGE, "Users retrieved successfully");
            response.put("users", userPage.getContent().toString());
            response.put("totalPages", String.valueOf(userPage.getTotalPages()));
            response.put("currentPage", String.valueOf(userPage.getNumber()));
            response.put("totalElements", String.valueOf(userPage.getTotalElements()));
            logger.info("Get users, pages: {}, elements: {}", page, userPage.getTotalElements());
            return ResponseEntity.ok(response);
        }catch(Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error get users " + e.getMessage());
            logger.error("Fail get users");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed("ADMIN")
    @DeleteMapping("/{idUser}")
    public ResponseEntity<Map<String, String>> deleteUserId(@PathVariable Long idUser) {
        Map<String, String> response = new HashMap<>();
        try{
            userService.delete(idUser);
            response.put(MESSAGE, "User deleted successfully");
            logger.info("Delte user {}", idUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(MESSAGE, e.getMessage());
            logger.error("User not found to  deleting");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error ocurred deleting user " + e.getMessage());
            logger.error("Error delete  users");
            return ResponseEntity.internalServerError().body(response);
        }

    }

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @DeleteMapping("")
    public ResponseEntity<Map<String, String>> deleteUser() {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUserDetails = (User) authentication.getPrincipal();
        try{
            userService.delete(customUserDetails.getUserId());
            response.put(MESSAGE, "User deleted successfully");
            logger.info("Delte user");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(MESSAGE, e.getMessage());
            logger.error("User not found, {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error ocurred deliting user " + e.getMessage());
            logger.error("Error deleting user,  {}", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }

    }


}