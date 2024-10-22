package org.grupouno.parking.it4.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.ProfileDto;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RequestMapping("/profiles")
@RestController
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;
    private static final String ERROR = "Error:";
    private static final String DETAIL = "Detail:";
    private static final String PROFILE = "profile:";
    private static final String MESSAGE = "Message";

    @RolesAllowed("PROFILE")
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> listProfiles(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(required = false) String description) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<Profile> profilePage = profileService.getAllProfiles(page, size, description);

            response.put("profiles", profilePage.getContent());
            response.put("totalPages", profilePage.getTotalPages());
            response.put("currentPage", profilePage.getNumber());
            response.put("totalElements", profilePage.getTotalElements());

            logger.info("Profiles retrieved, pages: {}, elements: {}, filter by name: {}",
                    profilePage.getTotalPages(), profilePage.getTotalElements(), description != null ? description : "No name filter");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(MESSAGE, "Error fetching profiles"));
        }
    }
    @RolesAllowed("PROFILE")
    @PostMapping("/addProfileRoles")
    public ResponseEntity<Map<String, Object>> saveProfileWithRoles(
            @RequestBody Profile profile,
            @RequestParam List<Long> roleIds) {
        Map<String, Object> response = new HashMap<>();
        try {
            Profile savedProfile = profileService.saveProfileWithRoles(profile, roleIds);
            response.put(PROFILE, savedProfile);
            response.put(MESSAGE, "Profile and roles saved successfully");
            return ResponseEntity.ok(response);
        }catch (RuntimeException  e) {
            response.put(MESSAGE, "Error saving profile with roles");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RolesAllowed("PROFILE")
    @PutMapping("/update/{profileId}/roles")
    public ResponseEntity<Map<String, Object>>updateProfileRoles(
            @PathVariable Long profileId,
            @RequestParam List<Long> roleIds,
            @RequestBody(required = false) ProfileDto profileDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Profile updatedProfile = profileService.updateProfileRoles(profileId, roleIds);
            if (profileDto != null) {
                profileService.updateProfile(profileDto, profileId);
            }
            response.put(MESSAGE, "Profile and roles saved successfully");
            response.put(PROFILE,updatedProfile );
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put(MESSAGE, "Profile or roles not found");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (IllegalArgumentException e) {
            response.put(MESSAGE, "Invalid arguments provided");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put(MESSAGE, "Error updating profile with roles");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RolesAllowed("PROFILE")
    @DeleteMapping("/detailProfile/{profileId}")
    public ResponseEntity<Map<String, Object>> deleteProfileAndDetail(@PathVariable Long profileId) {
        Map<String, Object> response = new HashMap<>();
        try {
            profileService.deleteProfileAndDetail(profileId);
            response.put(MESSAGE, "Profile: " + profileId + " Deleted");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put(ERROR, "Profile not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (UserDeletionException  e) {
            response.put(ERROR, "Cannot delete profile. It may be referenced by another entity: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put(ERROR, "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @RolesAllowed("PROFILE")
    @GetMapping("/{profileId}")
    public ResponseEntity<Map<String, Object>> findProfileById(@PathVariable Long profileId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Profile> profile = profileService.findById(profileId);
            return profile.map(p -> {
                List<Rol> roles = profileService.getRolesByProfileId(profileId);
                response.put("profile", p);
                response.put("roles", roles);
                return ResponseEntity.ok(response);
            }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @RolesAllowed("PROFILE")
    @PostMapping("/saveProfile")
    public ResponseEntity<String> saveProfile(@RequestBody Profile profile) {
        try {
            Profile savedProfile = profileService.saveProfile(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body("Perfil guardado con éxito"+ savedProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el perfil: " + e.getMessage());
        }
    }

    @RolesAllowed("PROFILE")
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

    @RolesAllowed("PROFILE")
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

    @RolesAllowed("PROFILE")
    @PatchMapping("patchProfile/{profileId}")
    public ResponseEntity<Map<String, Object>> patchProfile(@RequestBody ProfileDto profileDto, @PathVariable Long profileId) {
        Map<String, Object> response = new HashMap<>();
        try {
            profileService.patchProfile(profileDto, profileId);
            response.put("MESSAGE", "Perfil actualizado parcialmente");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put(ERROR, "Perfil no encontrado");
            response.put(DETAIL, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (IllegalArgumentException e) {
            response.put(ERROR, "Datos de perfil inválidos");
            response.put(DETAIL, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put(ERROR, "Error al actualizar el perfil");
            response.put(DETAIL, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @RolesAllowed("PROFILE")
    @GetMapping("roles/{profileId}")
    public ResponseEntity<List<Rol>> getRolesByProfileId(@PathVariable Long profileId) {
        List<Rol> roles = profileService.getRolesByProfileId(profileId);
        return ResponseEntity.ok(roles);
    }
}
