package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.dto.ProfileDto;
import org.grupouno.parking.it4.model.Profile;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IProfileService {


    Page<Profile> getAllProfiles(int page, int size, String description);

    Optional<Profile> findById(Long id);

    Profile saveProfile(Profile profile);

    void updateProfile(ProfileDto profileDto, Long profileId);

    void patchProfile(ProfileDto profileDto, Long profileId);

    void deleteProfile(Long profileId);
}