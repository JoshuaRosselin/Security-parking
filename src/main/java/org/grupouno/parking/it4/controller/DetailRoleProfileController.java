package org.grupouno.parking.it4.controller;

import jakarta.annotation.security.RolesAllowed;
import org.grupouno.parking.it4.model.DetailDTO;
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

    private final DetailRoleProfileService detailRoleProfileService;
    private final ProfileService profileService;
    private final RoleService rolService;

    @Autowired
    public DetailRoleProfileController(DetailRoleProfileService detailRoleProfileService,
                                       ProfileService profileService,
                                       RoleService rolService) {
        this.detailRoleProfileService = detailRoleProfileService;
        this.profileService = profileService;
        this.rolService = rolService;
    }

    @RolesAllowed("DETAILROLEPROFILE")
    @PostMapping("/{profileId}/{roleId}")
    public ResponseEntity<DetailRoleProfile> saveDetailRoleProfile(
            @PathVariable long profileId,
            @PathVariable long roleId) {

        Optional<Profile> profileOpt = profileService.findById(profileId);
        Optional<Rol> rolOpt = rolService.findRolById(roleId);

        if (profileOpt.isPresent() && rolOpt.isPresent()) {
            DetailRoleProfile detailRoleProfile = new DetailRoleProfile();

            // Establecer los IDs en el objeto DetailDTO
            DetailDTO id = new DetailDTO(profileId, roleId);
            detailRoleProfile.setId(id);
            detailRoleProfile.setProfile(profileOpt.get());
            detailRoleProfile.setRole(rolOpt.get());

            DetailRoleProfile savedDetail = detailRoleProfileService.saveDetailRoleProfile(detailRoleProfile);
            return ResponseEntity.ok(savedDetail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RolesAllowed("DETAILROLEPROFILE")
    @GetMapping("")
    public List<DetailRoleProfile> getAllDetailRoleProfiles() {
        return detailRoleProfileService.getAllDetailRoleProfiles();
    }

    @RolesAllowed("DETAILROLEPROFILE")
    @GetMapping("/profile/{profileId}/roles")
    public ResponseEntity<List<Rol>> getRolesByProfileId(@PathVariable long profileId) {
        List<Rol> roles = detailRoleProfileService.getRolesByProfileId(profileId);
        return ResponseEntity.ok(roles);
    }

    @RolesAllowed("DETAILROLEPROFILE")
    @GetMapping("/role/{roleId}/profiles")
    public ResponseEntity<List<Profile>> getProfilesByRoleId(@PathVariable long roleId) {
        List<Profile> profiles = detailRoleProfileService.getProfilesByRoleId(roleId);
        return ResponseEntity.ok(profiles);
    }


    @RolesAllowed("DETAILROLEPROFILE")
    @PutMapping("/profile/{profileId}/roles")
    public ResponseEntity<Void> updateRolesForProfile(@PathVariable long profileId, @RequestBody List<Long> roleIds) {
        detailRoleProfileService.deleteRolesFromProfile(profileId);

        for (Long roleId : roleIds) {
            Optional<Rol> rolOpt = rolService.findRolById(roleId);
            if (rolOpt.isPresent()) {
                DetailRoleProfile detailRoleProfile = new DetailRoleProfile();
                Profile profile = new Profile();
                profile.setProfileId(profileId);
                detailRoleProfile.setProfile(profile);
                detailRoleProfile.setRole(rolOpt.get());
                detailRoleProfileService.saveDetailRoleProfile(detailRoleProfile);
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.noContent().build();
    }
}
