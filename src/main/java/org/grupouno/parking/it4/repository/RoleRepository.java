package org.grupouno.parking.it4.repository;

import org.grupouno.parking.it4.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Rol, Long> {

    @Query("SELECT r FROM Rol r WHERE r.id IN (SELECT d.role.id FROM DetailRoleProfile d WHERE d.profile.id = :profileId)")
    List<Rol> findRolesByProfileId(@Param("profileId") Long profileId);
    Optional<Rol> findByRole(String role);

}
