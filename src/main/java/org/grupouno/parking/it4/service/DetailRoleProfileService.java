package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.DetailDTO;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.DetailRoleProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DetailRoleProfileService implements IDetailRoleProfileService {

    private final DetailRoleProfileRepository repository;
    private final AudithService audithService;

    @Autowired
    public DetailRoleProfileService(DetailRoleProfileRepository repository, AudithService audithService) {
        this.repository = repository;
        this.audithService = audithService;
    }

    @Override
    @Transactional
    public DetailRoleProfile saveDetailRoleProfile(DetailRoleProfile detailRoleProfile) {
        try {
            return repository.save(detailRoleProfile);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el detalle del rol y perfil", e);
        }
    }

    @Override
    public Optional<DetailRoleProfile> getDetailRoleProfileById(Profile profile, Rol role) {
        DetailDTO id = new DetailDTO();
        id.setIdProfile(profile.getProfileId());
        id.setIdRole(role.getId());

        Optional<DetailRoleProfile> detail = repository.findById(id);
        return detail;
    }

    @Override
    public List<DetailRoleProfile> getAllDetailRoleProfiles() {
        List<DetailRoleProfile> details = repository.findAll();
        return details;
    }

    @Override
    @Transactional
    public void deleteDetailRoleProfile(Profile profile, Rol role) {
        DetailDTO id = new DetailDTO();
        id.setIdProfile(profile.getProfileId());
        id.setIdRole(role.getId());

        repository.deleteById(id);
        logAudit("DELETE", profile, role, Optional.empty());
    }

    @Override
    public List<Rol> getRolesByProfileId(long profileId) {
        List<Rol> roles = repository.findByProfile_ProfileId(profileId).stream()
                .map(DetailRoleProfile::getRole)
                .collect(Collectors.toList());
        return roles;
    }

    @Override
    public List<Profile> getProfilesByRoleId(long roleId) {
        List<Profile> profiles = repository.findByRole_Id(roleId).stream()
                .map(DetailRoleProfile::getProfile)
                .collect(Collectors.toList());
        return profiles;
    }

    @Override
    @Transactional
    public void deleteRolesFromProfile(long profileId) {
        List<DetailRoleProfile> details = repository.findByProfile_ProfileId(profileId);
        if (!details.isEmpty()) {
            details.forEach(repository::delete);
        }
    }

    private void logAudit(String action, Profile profile, Rol role, Optional<DetailRoleProfile> detail) {
        audithService.createAudit(
                "DetailRoleProfile",
                "Consulta de detalle de rol-perfil por ID",
                action,
                Map.of("profileId", profile != null ? profile.getProfileId() : null,
                        "roleId", role != null ? role.getId() : null),
                Map.of("foundDetail", detail.orElse(null)),
                detail.isPresent() ? "SUCCESS" : "NOT_FOUND"
        );
    }

    private void logAudit(String action, Long profileId, Long roleId, List<?> items) {
        audithService.createAudit(
                "DetailRoleProfile",
                action + " de detalle de rol-perfil",
                action,
                Map.of("profileId", profileId, "roleId", roleId),
                Map.of("itemsCount", items.size()),
                "SUCCESS"
        );
    }
}
