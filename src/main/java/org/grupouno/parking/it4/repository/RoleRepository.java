package org.grupouno.parking.it4.repository;

import org.grupouno.parking.it4.model.Rol;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Rol, Long> {
    @Query("SELECT r FROM Rol r WHERE r.id IN (SELECT d.idRole FROM DetailRoleProfile d WHERE d.idProfile = :profileId)")
    List<Rol> findRolesByProfileId(@Param("profileId") Long profileId);
}
