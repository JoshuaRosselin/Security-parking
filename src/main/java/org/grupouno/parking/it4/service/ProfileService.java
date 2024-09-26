package org.grupouno.parking.it4.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.ProfileDto;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@AllArgsConstructor
@Service
public class ProfileService implements IProfileService{
    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    ProfileRepository profileRepository;

    @Override
    public Page<Profile> getAllProfiles(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return profileRepository.findAll(pageable);
    }

    @Override
    public Optional<Profile> findById(Long id){
        if (id == null){
            logger.warn("Id could not be null ");
            throw  new IllegalArgumentException("Id could not be null");
        }
        return profileRepository.findById(id);
    }

    @Override
    public Profile saveProfile(Profile profile) {
        return profileRepository.save(profile);
    }


    @Override
    public void updateProfile(ProfileDto profileDto, Long profileId){
        if (!profileRepository.existsById(profileId)){
            logger.error("Profile with id {} not exist ", profileId);
            throw new EntityNotFoundException(profileId+"Dont exist");
        }
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (optionalProfile.isPresent()){
            Profile profile = optionalProfile.get();
            if (profileDto.getDescription() != null) profile.setDescription(profileDto.getDescription());
            if (profileDto.isStatus()) profile.setStatus(profileDto.isStatus());
            logger.info("Profile updated");
            profileRepository.save(profile);
        }
    }

    public void patchProfile (ProfileDto profileDto, Long profileId){
        if (!profileRepository.existsById(profileId)){
            logger.error("Porfile with {} not found", profileId);
            throw new EntityNotFoundException("El perfil con Id: "+ profileId + "no se puedo encontrar");
        }
        Profile profile = profileRepository.findById(profileId).orElseThrow(() ->
                new EntityNotFoundException());
        if (profileDto.getDescription() != null){
            profile.setDescription(profileDto.getDescription());
        }
        if (profileDto.isStatus()){
            profile.setStatus(profileDto.isStatus());
        }
        logger.info("Profile updated");
        profileRepository.save(profile);
    }

    @Override
    public void deleteProfile(Long profileId){
        if (!profileRepository.existsById(profileId)){
            logger.error("Porfile with {} not found", profileId);
            throw  new IllegalArgumentException("El perfil con el id: " + profileId + "no se pudo encontrar");
        }
        try{
            profileRepository.deleteById(profileId);
        }catch (DataAccessException e){
            logger.error("Fail delete profile {}", profileId);
            throw new UserDeletionException("Error al eliminar el perfil", e);
        }
    }


}