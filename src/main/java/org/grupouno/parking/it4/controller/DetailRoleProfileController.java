package org.grupouno.parking.it4.controller;

import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.service.DetailRoleProfileService;
import org.grupouno.parking.it4.service.ProfileService;
import org.grupouno.parking.it4.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/detailsRoleProfile")
public class DetailRoleProfileController {

    @Autowired
    private DetailRoleProfileService detailRoleProfileService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private RoleService rolService;

    // Crear o actualizar un DetailRoleProfile
    @PostMapping("/{profileId}/{roleId}")
    public ResponseEntity<DetailRoleProfile> saveDetailRoleProfile(
            @PathVariable long profileId,
            @PathVariable long roleId) {

        // Buscar el Profile y Rol por ID antes de crear la relación
        Optional<Profile> profileOpt = profileService.findById(profileId);
        Optional<Rol> rolOpt = rolService.findRolById(roleId);

        if (profileOpt.isPresent() && rolOpt.isPresent()) {
            DetailRoleProfile detailRoleProfile = new DetailRoleProfile();
            detailRoleProfile.setProfile(profileOpt.get());
            detailRoleProfile.setRole(rolOpt.get());
            DetailRoleProfile savedDetail = detailRoleProfileService.saveDetailRoleProfile(detailRoleProfile);
            return ResponseEntity.ok(savedDetail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Obtener todos los DetailRoleProfiles
    @GetMapping("")
    public List<DetailRoleProfile> getAllDetailRoleProfiles() {
        return detailRoleProfileService.getAllDetailRoleProfiles();
    }

    // Obtener todos los roles de un perfil por ID
    @GetMapping("/profile/{profileId}/roles")
    public ResponseEntity<List<Rol>> getRolesByProfileId(@PathVariable long profileId) {
        List<Rol> roles = detailRoleProfileService.getRolesByProfileId(profileId);
        return ResponseEntity.ok(roles);
    }

    // Obtener todos los perfiles de un rol por ID
    @GetMapping("/role/{roleId}/profiles")
    public ResponseEntity<List<Profile>> getProfilesByRoleId(@PathVariable long roleId) {
        List<Profile> profiles = detailRoleProfileService.getProfilesByRoleId(roleId);
        return ResponseEntity.ok(profiles);
    }

    // Eliminar roles de un perfil por ID
    @DeleteMapping("/profile/{profileId}/roles")
    public ResponseEntity<Void> deleteRolesFromProfile(@PathVariable long profileId, @RequestBody List<Long> roleIds) {
        for (Long roleId : roleIds) {
            Optional<Rol> rolOpt = rolService.findRolById(roleId);
            if (rolOpt.isPresent()) {
                detailRoleProfileService.deleteDetailRoleProfile(new Profile(), rolOpt.get());
            }
        }
        return ResponseEntity.noContent().build();
    }

    // Actualizar roles de un perfil por ID
    @PutMapping("/profile/{profileId}/roles")
    public ResponseEntity<Void> updateRolesForProfile(@PathVariable long profileId, @RequestBody List<Long> roleIds) {
        // Primero, elimina los roles existentes
        detailRoleProfileService.deleteRolesFromProfile(profileId);

        // Luego, agrega los nuevos roles
        for (Long roleId : roleIds) {
            Optional<Rol> rolOpt = rolService.findRolById(roleId);
            if (rolOpt.isPresent()) {
                DetailRoleProfile detailRoleProfile = new DetailRoleProfile();
                detailRoleProfile.setProfile(new Profile()); // Asignar solo el ID
                detailRoleProfile.setRole(rolOpt.get());
                detailRoleProfileService.saveDetailRoleProfile(detailRoleProfile);
            } else {
                // Manejar el caso donde el rol no existe
                return ResponseEntity.badRequest().build(); // o ResponseEntity.notFound().build() según tu preferencia
            }
        }
        return ResponseEntity.noContent().build();
    }
}
