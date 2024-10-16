package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.exceptions.CustomDataAccessException;
import org.grupouno.parking.it4.exceptions.CustomIllegalArgumentException;
import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.DetailDTO;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.DetailRoleProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
        } catch (DataAccessException e) { // Captura una excepción específica
            throw new CustomDataAccessException("Error al guardar el detalle del rol y perfil", e);
        } catch (IllegalArgumentException e) {
            throw new CustomIllegalArgumentException("Argumento inválido al guardar el detalle del rol y perfil", e);
        }
    }

    @Override
    public Optional<DetailRoleProfile> getDetailRoleProfileById(Profile profile, Rol role) {
        DetailDTO id = new DetailDTO();
        id.setIdProfile(profile.getProfileId());
        id.setIdRole(role.getId());

        return repository.findById(id);

    }

    @Override
    public List<DetailRoleProfile> getAllDetailRoleProfiles() {
        return repository.findAll();

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
        return repository.findByProfile_ProfileId(profileId).stream()
                .map(DetailRoleProfile::getRole)
                .toList();
    }

    @Override
    public List<Profile> getProfilesByRoleId(long roleId) {
        return repository.findByRole_Id(roleId).stream()
                .map(DetailRoleProfile::getProfile)
                .toList();
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

}
