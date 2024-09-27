package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.DetailDTO;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.DetailRoleProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DetailRoleProfileService implements IDetailRoleProfileService {

    private final DetailRoleProfileRepository repository;
    private final AudithService audithService; // Inyectar el servicio de auditoría

    @Autowired
    public DetailRoleProfileService(DetailRoleProfileRepository repository, AudithService audithService) {
        this.repository = repository;
        this.audithService = audithService;
    }

    @Override
    public DetailRoleProfile saveDetailRoleProfile(DetailRoleProfile detailRoleProfile) {
        DetailRoleProfile savedDetail = repository.save(detailRoleProfile);

        // Crear auditoría después de guardar
        audithService.createAudit(
                "DetailRoleProfile",
                "Guardado de detalle de rol-perfil",
                "SAVE",
                Map.of("profileId", detailRoleProfile.getProfile().getProfileId(), "roleId", detailRoleProfile.getRole().getId()),
                Map.of("savedDetail", savedDetail),
                "SUCCESS"
        );

        return savedDetail;
    }

    @Override
    public Optional<DetailRoleProfile> getDetailRoleProfileById(Profile profile, Rol role) {
        DetailDTO id = new DetailDTO();
        id.setIdProfile(profile.getProfileId());
        id.setIdRole(role.getId());

        Optional<DetailRoleProfile> detail = repository.findById(id);

        // Crear auditoría después de la consulta
        audithService.createAudit(
                "DetailRoleProfile",
                "Consulta de detalle de rol-perfil por ID",
                "GET",
                Map.of("profileId", profile.getProfileId(), "roleId", role.getId()),
                Map.of("foundDetail", detail.orElse(null)),
                detail.isPresent() ? "SUCCESS" : "NOT_FOUND"
        );

        return detail;
    }

    @Override
    public List<DetailRoleProfile> getAllDetailRoleProfiles() {
        List<DetailRoleProfile> details = repository.findAll();

        // Crear auditoría para obtener todos los detalles
        audithService.createAudit(
                "DetailRoleProfile",
                "Consulta de todos los detalles de rol-perfil",
                "GET_ALL",
                null,
                Map.of("totalDetails", details.size()),
                "SUCCESS"
        );

        return details;
    }

    @Override
    public void deleteDetailRoleProfile(Profile profile, Rol role) {
        DetailDTO id = new DetailDTO();
        id.setIdProfile(profile.getProfileId());
        id.setIdRole(role.getId());

        repository.deleteById(id);

        // Crear auditoría después de eliminar
        audithService.createAudit(
                "DetailRoleProfile",
                "Eliminación de detalle de rol-perfil",
                "DELETE",
                Map.of("profileId", profile.getProfileId(), "roleId", role.getId()),
                null,
                "SUCCESS"
        );
    }

    // Nuevo método para obtener roles por ID de perfil
    public List<Rol> getRolesByProfileId(long profileId) {
        List<Rol> roles = repository.findByProfile_ProfileId(profileId).stream()
                .map(DetailRoleProfile::getRole)
                .collect(Collectors.toList());

        // Crear auditoría para consulta de roles por ID de perfil
        audithService.createAudit(
                "DetailRoleProfile",
                "Consulta de roles por ID de perfil",
                "GET_ROLES_BY_PROFILE",
                Map.of("profileId", profileId),
                Map.of("rolesCount", roles.size()),
                "SUCCESS"
        );

        return roles;
    }

    // Nuevo método para obtener perfiles por ID de rol
    public List<Profile> getProfilesByRoleId(long roleId) {
        List<Profile> profiles = repository.findByRole_Id(roleId).stream()
                .map(DetailRoleProfile::getProfile)
                .collect(Collectors.toList());

        // Crear auditoría para consulta de perfiles por ID de rol
        audithService.createAudit(
                "DetailRoleProfile",
                "Consulta de perfiles por ID de rol",
                "GET_PROFILES_BY_ROLE",
                Map.of("roleId", roleId),
                Map.of("profilesCount", profiles.size()),
                "SUCCESS"
        );

        return profiles;
    }

    // Nuevo método para eliminar roles de un perfil
    public void deleteRolesFromProfile(long profileId) {
        List<DetailRoleProfile> details = repository.findByProfile_ProfileId(profileId);
        for (DetailRoleProfile detail : details) {
            repository.delete(detail);
        }

        // Crear auditoría después de eliminar roles de un perfil
        audithService.createAudit(
                "DetailRoleProfile",
                "Eliminación de roles de un perfil",
                "DELETE_ROLES_FROM_PROFILE",
                Map.of("profileId", profileId),
                Map.of("rolesDeleted", details.size()),
                "SUCCESS"
        );
    }
}
