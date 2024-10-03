package org.grupouno.parking.it4.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.ProfileDto;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.DetailRoleProfileRepository;
import org.grupouno.parking.it4.repository.ProfileRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProfileService implements IProfileService {

    private final ProfileRepository profileRepository;
    private final AudithService audithService;
    private final ObjectMapper objectMapper;
    private final DetailRoleProfileRepository detailRoleProfileRepository;

    public List<Rol> getRolesByProfileId(Long profileId) {
        return detailRoleProfileRepository.findRolesByProfileId(profileId);
    }

    @Override
    public Page<Profile> getAllProfiles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Profile> profiles = profileRepository.findAll(pageable);

        auditAction("Profile", "Fetching all profiles", "Read",
                Map.of(),
                Map.of("profilesCount", profiles.getTotalElements()),
                "Success");

        return profiles;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        validateId(id);

        Optional<Profile> profile = profileRepository.findById(id);
        String responseMessage = profile.map(Profile::toString).orElse("Not Found");

        auditAction("Profile", "Fetching profile by ID", "Read",
                Map.of("id", id),
                Map.of("profile", responseMessage),
                profile.isPresent() ? "Success" : "Not Found");

        return profile;
    }

    @Override
    public Profile saveProfile(Profile profile) {
        validateProfile(profile);
        Profile savedProfile = profileRepository.save(profile);

        auditAction("Profile", "Saving profile", "Create",
                convertToMap(profile),
                convertToMap(savedProfile),
                "Success");

        return savedProfile;
    }

    @Override
    public void updateProfile(ProfileDto profileDto, Long profileId) {
        validateProfileDto(profileDto);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile with ID " + profileId + " does not exist"));

        updateProfileFields(profile, profileDto);
        profileRepository.save(profile);

        auditAction("Profile", "Updating profile", "Update",
                Map.of("profileId", profileId, "profileUpdates", profileDto),
                convertToMap(profile),
                "Success");
    }

    @Override
    public void patchProfile(ProfileDto profileDto, Long profileId) {
        validateProfileDto(profileDto);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile with ID " + profileId + " does not exist"));

        updateProfileFields(profile, profileDto);
        profileRepository.save(profile);

        auditAction("Profile", "Patching profile", "Update",
                Map.of("profileId", profileId, "profileUpdates", profileDto),
                convertToMap(profile),
                "Success");
    }

    @Override
    public void deleteProfile(Long profileId) {
        validateProfileId(profileId);

        try {
            profileRepository.deleteById(profileId);
            auditAction("Profile", "Deleting profile", "Delete",
                    Map.of("profileId", profileId),
                    null,
                    "Success");
        } catch (DataAccessException e) {
            throw new UserDeletionException("Error deleting profile", e);
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
    }

    private void validateProfile(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
    }

    private void validateProfileDto(ProfileDto profileDto) {
        if (profileDto == null) {
            throw new IllegalArgumentException("ProfileDto cannot be null");
        }
    }

    private void validateProfileId(Long profileId) {
        if (profileId == null || !profileRepository.existsById(profileId)) {
            throw new EntityNotFoundException("Profile with ID " + profileId + " does not exist");
        }
    }

    private void updateProfileFields(Profile profile, ProfileDto profileDto) {
        if (profileDto.getDescription() != null) {
            profile.setDescription(profileDto.getDescription());
        }
        if (profileDto.isStatus() != profile.isStatus()) {
            profile.setStatus(profileDto.isStatus());
        }
    }

    private Map<String, Object> convertToMap(Profile profile) {
        try {
            return objectMapper.convertValue(profile, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Profile to Map", e);
        }
    }

    private void auditAction(String entity, String description, String operation,
                             Map<String, Object> request, Map<String, Object> response, String result) {
        audithService.createAudit(entity, description, operation, request, response, result);
    }
}
