package org.grupouno.parking.it4.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.grupouno.parking.it4.dto.ProfileDto;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.service.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RequestMapping("/profiles")
@RestController
public class ProfileController {

    ProfileService profileService;
    private static final String ERROR = "Error:";

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER", "ROLE_AUDITH"})
    @GetMapping("")
    public ResponseEntity<Map<String, String>> listProfiles(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Map<String, String> response = new HashMap<>();
        try {
            Page<Profile> profilePage = profileService.getAllProfiles(page,size);
            response.put("profile", profilePage.getContent().toString());
            response.put("totalPages", String.valueOf(profilePage.getTotalPages()));
            response.put("currentPage", String.valueOf(profilePage.getNumber()));
            response.put("totalElements", String.valueOf(profilePage.getTotalElements()));
            return  ResponseEntity.ok(response);
        }catch(EntityNotFoundException e){
            return  ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER", "ROLE_AUDITH"})
    @GetMapping("/{profileId}")
    public ResponseEntity<Map<String, String>> findProfileById(@PathVariable Long profileId){
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Profile> profile = profileService.findById(profileId);
            if (profile.isPresent()){
                response.put("Hola",profile.toString());
                return ResponseEntity.ok(response);
            }else {
                return ResponseEntity.notFound().build();
            }

        }catch (IllegalArgumentException e){
            return  ResponseEntity.badRequest().body(response);
        }
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER", "ROLE_AUDITH"})
    @PostMapping("/saveProfile")
    public ResponseEntity<String> saveProfile(@RequestBody Profile profile) {
        try {
            Profile savedProfile = profileService.saveProfile(profile);
            if (savedProfile != null) {
                return ResponseEntity.ok("Perfil guardado");
            } else {
                return ResponseEntity.badRequest().body("Perfil no guardado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el perfil: " + e.getMessage());
        }
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER", "ROLE_AUDITH"})
    @PutMapping("/update/{profileId}")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileDto profileDto, @PathVariable Long profileId) {
        try {
            profileService.updateProfile(profileDto, profileId);
            return ResponseEntity.ok("Perfil actualizado correctamente");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ERROR + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el perfil: " + e.getMessage());
        }
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER", "ROLE_AUDITH"})
    @DeleteMapping("/delete/{profileId}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long profileId) {
        try {
            profileService.deleteProfile(profileId);
            return ResponseEntity.ok("Perfil eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (UserDeletionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el perfil: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER", "ROLE_AUDITH"})
    @PatchMapping("/patchUpdate/{profileId}")
    public ResponseEntity<String> patchProfile(@RequestBody ProfileDto profileDto, @PathVariable Long profileId) {
        try {
            profileService.patchProfile(profileDto, profileId);
            return ResponseEntity.ok("Perfil actualizado parcialmente");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el perfil: " + e.getMessage());
        }
    }

}

