package org.grupouno.parking.it4.repository;

import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.DetailDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailRoleProfileRepository extends JpaRepository<DetailRoleProfile, DetailDTO> {
    List<DetailRoleProfile> findByProfile_ProfileId(long profileId);
    List<DetailRoleProfile> findByRole_Id(long roleId);
}
