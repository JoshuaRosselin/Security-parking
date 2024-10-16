package org.grupouno.parking.it4.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.ProfileDto;
import org.grupouno.parking.it4.exceptions.ProfileConversionException;
import org.grupouno.parking.it4.exceptions.RoleExistingException;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.DetailDTO;
import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.DetailRoleProfileRepository;
import org.grupouno.parking.it4.repository.ProfileRepository;
import org.grupouno.parking.it4.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private static final String PROFILE = "Profile";
    private static final String SUCCES = "Success";
    private static final String NOTEXIST = "does not exist";

    public List<Rol> getRolesByProfileId(Long profileId) {
        return detailRoleProfileRepository.findRolesByProfileId(profileId);
    }

    @Override
    public Page<Profile> getAllProfiles(int page, int size, String description) {
        Pageable pageable = PageRequest.of(page, size);

        if (description != null && !description.isEmpty()) {
            return profileRepository.findByDescriptionContainingIgnoreCase(description, pageable);
        }
        Page<Profile> profiles = profileRepository.findAll(pageable);

        auditAction(PROFILE, "Fetching all profiles", "Read",
                Map.of(),
                Map.of("profilesCount", profiles.getTotalElements()),
                SUCCES);

        return profiles;
    }

    public List<Profile> getAllProfilesForUser() {

        List<Profile> profiles = profileRepository.findAll();

        auditAction(PROFILE, "Fetching all profiles", "Read",
                Map.of(),
                Map.of("profilesCount", profiles.size()),
                SUCCES);

        return profiles;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        validateId(id);

        Optional<Profile> profile = profileRepository.findById(id);
        String responseMessage = profile.map(Profile::toString).orElse("Not Found");

        auditAction(PROFILE, "Fetching profile by ID", "Read",
                Map.of("id", id),
                Map.of("profile", responseMessage),
                profile.isPresent() ? SUCCES : "Not Found");

        return profile;
    }

    @Override
    public Profile saveProfile(Profile profile) {
        validateProfile(profile);
        Profile savedProfile = profileRepository.save(profile);

        auditAction(PROFILE, "Saving profile", "Create",
                convertToMap(profile),
                convertToMap(savedProfile),
                SUCCES);

        return savedProfile;
    }

    @Transactional
    public Profile saveProfileWithRoles(Profile profile, List<Long> roleIds) {
        Optional<Profile> existingProfile = profileRepository.findByDescription(profile.getDescription());
        if (existingProfile.isPresent()) {
            throw new RoleExistingException("Profile already exists with the name: " + profile.getDescription());
        }
        Profile savedProfile = profileRepository.save(profile);

        for (Long roleId : roleIds) {
            Rol role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with id " + roleId));

            Optional<DetailRoleProfile> existingDetail = detailRoleProfileRepository.findByIdIdProfileAndIdIdRole(savedProfile.getProfileId(), roleId);
            if (existingDetail.isPresent()) {
                throw new RoleExistingException("The role with id " + roleId + " is already assigned to the profile " + savedProfile.getProfileId());
            }
            DetailDTO detailDTO = new DetailDTO(savedProfile.getProfileId(), roleId);
            DetailRoleProfile detailRoleProfile = new DetailRoleProfile();
            detailRoleProfile.setId(detailDTO);
            detailRoleProfile.setProfile(savedProfile);
            detailRoleProfile.setRole(role);
            detailRoleProfileRepository.save(detailRoleProfile);
        }
        return savedProfile;
    }

    @Override
    public void updateProfile(ProfileDto profileDto, Long profileId) {
        validateProfileDto(profileDto);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile ID " + profileId + NOTEXIST));

        updateProfileFields(profile, profileDto);
        profileRepository.save(profile);

        auditAction(PROFILE, "Updating profile", "Update",
                Map.of("profile Id", profileId, "profileUpdates", profileDto),
                convertToMap(profile),
                SUCCES);
    }

    @Transactional
    public Profile updateProfileRoles(Long profileId, List<Long> newRoleIds) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found with id " + profileId));

        List<DetailRoleProfile> currentRoles = detailRoleProfileRepository.findAllByIdIdProfile(profileId);
        List<Long> currentRoleIds = currentRoles.stream()
                .map(detail -> detail.getId().getIdRole())
                .toList();

        List<Long> rolesToAdd = newRoleIds.stream()
                .filter(roleId -> !currentRoleIds.contains(roleId))
                .toList();

        List<Long> rolesToRemove = currentRoleIds.stream()
                .filter(roleId -> !newRoleIds.contains(roleId))
                .toList();

        for (Long roleId : rolesToAdd) {
            Rol role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with id " + roleId));

            DetailDTO detailDTO = new DetailDTO(profileId, roleId);
            DetailRoleProfile newDetailRoleProfile = new DetailRoleProfile();
            newDetailRoleProfile.setId(detailDTO);
            newDetailRoleProfile.setProfile(profile);
            newDetailRoleProfile.setRole(role);

            detailRoleProfileRepository.save(newDetailRoleProfile);
        }

        for (Long roleId : rolesToRemove) {
            detailRoleProfileRepository.deleteById(new DetailDTO(profileId, roleId));
        }
        return profile;
    }

    @Override
    public void patchProfile(ProfileDto profileDto, Long profileId) {
        validateProfileDto(profileDto);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile with ID " + profileId + " does not exist"));

        updateProfileFields(profile, profileDto);
        profileRepository.save(profile);

        auditAction(PROFILE, "Patching profile", "Update",
                Map.of("profileId", profileId, "profileUpdates", profileDto),
                convertToMap(profile),
                SUCCES);
    }

    @Override
    public void deleteProfile(Long profileId) {
        validateProfileId(profileId);

        try {
            profileRepository.deleteById(profileId);
            auditAction(PROFILE, "Deleting profile", "Delete",
                    Map.of("profileId", profileId),
                    null,
                    SUCCES);
        } catch (DataAccessException e) {
            throw new UserDeletionException("Error deleting profile", e);
        }
    }

    @Transactional
    public void deleteProfileAndDetail(Long profileId) {
        validateProfileId(profileId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        detailRoleProfileRepository.deleteByProfile(profile);
        profileRepository.deleteById(profileId);
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
        } catch (RuntimeException e) {
            throw new ProfileConversionException("Error converting Profile to Map", e);
        }
    }

    private void auditAction(String entity, String description, String operation,
                             Map<String, Object> request, Map<String, Object> response, String result) {
        audithService.createAudit(entity, description, operation, request, response, result);
    }
}
