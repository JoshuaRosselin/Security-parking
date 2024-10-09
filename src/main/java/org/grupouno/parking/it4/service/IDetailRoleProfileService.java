package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;

import java.util.List;
import java.util.Optional;

public interface IDetailRoleProfileService {
    DetailRoleProfile saveDetailRoleProfile(DetailRoleProfile detailRoleProfile);

    Optional<DetailRoleProfile> getDetailRoleProfileById(Profile profile, Rol role);

    List<DetailRoleProfile> getAllDetailRoleProfiles();

    void deleteDetailRoleProfile(Profile profile, Rol role);

    List<Rol> getRolesByProfileId(long profileId); // Agregado
    List<Profile> getProfilesByRoleId(long roleId); // Agregado
    void deleteRolesFromProfile(long profileId); // Agregado
}
