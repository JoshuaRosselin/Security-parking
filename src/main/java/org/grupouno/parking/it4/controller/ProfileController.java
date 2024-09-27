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

    private final ProfileService profileService;
    private static final String ERROR = "Error:";

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> listProfiles(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<Profile> profilePage = profileService.getAllProfiles(page, size);
            response.put("profiles", profilePage.getContent());
            response.put("totalPages", profilePage.getTotalPages());
            response.put("currentPage", profilePage.getNumber());
            response.put("totalElements", profilePage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Error fetching profiles"));
        }
    }

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @GetMapping("/{profileId}")
    public ResponseEntity<Map<String, Object>> findProfileById(@PathVariable Long profileId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Profile> profile = profileService.findById(profileId);
            return profile.map(p -> {
                response.put("profile", p);
                return ResponseEntity.ok(response);
            }).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @PostMapping("/saveProfile")
    public ResponseEntity<String> saveProfile(@RequestBody Profile profile) {
        try {
            Profile savedProfile = profileService.saveProfile(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body("Perfil guardado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el perfil: " + e.getMessage());
        }
    }

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
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

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @DeleteMapping("delete/{profileId}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long profileId) {
        try {
            profileService.deleteProfile(profileId);
            return ResponseEntity.ok("Perfil eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ERROR + e.getMessage());
        } catch (UserDeletionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el perfil: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    @RolesAllowed({"ADMIN", "USER", "AUDITH"})
    @PatchMapping("patchProfile/{profileId}")
    public ResponseEntity<String> patchProfile(@RequestBody ProfileDto profileDto, @PathVariable Long profileId) {
        try {
            profileService.patchProfile(profileDto, profileId);
            return ResponseEntity.ok("Perfil actualizado parcialmente");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ERROR + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Error: Datos de perfil inválidos. " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el perfil: " + e.getMessage());
        }
    }
}
