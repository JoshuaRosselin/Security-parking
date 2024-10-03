package org.grupouno.parking.it4.repository;

import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.DetailDTO;
import org.grupouno.parking.it4.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailRoleProfileRepository extends JpaRepository<DetailRoleProfile, DetailDTO> {
    List<DetailRoleProfile> findByProfile_ProfileId(long profileId);
    List<DetailRoleProfile> findByRole_Id(long roleId);

    @Query("SELECT d.role FROM DetailRoleProfile d WHERE d.profile.profileId = :profileId")
    List<Rol> findRolesByProfileId(@Param("profileId") Long profileId);
}
