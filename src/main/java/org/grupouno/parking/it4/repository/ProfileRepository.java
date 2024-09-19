package org.grupouno.parking.it4.repository;

import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfileRepository extends JpaRepository<Profile, Long> {

}
