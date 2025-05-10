package me.fivi.service

import io.micronaut.transaction.annotation.Transactional
import me.fivi.domain.Account
import me.fivi.domain.Profile
import me.fivi.dto.ProfileResponse
import me.fivi.dto.UpdateProfileRequest
import me.fivi.exception.NotFoundException
import me.fivi.repository.AccountRepository
import me.fivi.repository.ProfileRepository
import groovy.transform.CompileStatic
import jakarta.inject.Singleton

@CompileStatic
@Singleton
class ProfileService {
    private final ProfileRepository profileRepository
    private final AccountRepository accountRepository

    ProfileService(ProfileRepository profileRepository, AccountRepository accountRepository) {
        this.profileRepository = profileRepository
        this.accountRepository = accountRepository
    }

    Profile getProfileByAccountId(Long accountId) {
        return profileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Profile not found for account"))
    }

    ProfileResponse mapToProfileResponse(Profile profile) {
        Account account = profile.account

        return new ProfileResponse(
                id: profile.id,
                accountId: account.id,
                firstName: account.firstName,
                lastName: account.lastName,
                nickname: account.nickname,
                email: account.email,
                notificationsEnabled: profile.notificationsEnabled,
                privateProfile: profile.privateProfile,
                bio: profile.bio,
                avatarUrl: profile.avatarUrl,
                location: profile.location,
                language: profile.language,
                theme: profile.theme,
                autoPlayVideos: profile.autoPlayVideos,
                saveDataMode: profile.saveDataMode
        )
    }

    @Transactional
    ProfileResponse updateProfile(Long accountId, UpdateProfileRequest request) {
        Profile profile = getProfileByAccountId(accountId)

        // Update only non-null fields
        if (request.bio != null) {
            profile.bio = request.bio
        }

        if (request.location != null) {
            profile.location = request.location
        }

        if (request.language != null) {
            profile.language = request.language
        }

        if (request.theme != null) {
            profile.theme = request.theme
        }

        if (request.notificationsEnabled != null) {
            profile.notificationsEnabled = request.notificationsEnabled
        }

        if (request.privateProfile != null) {
            profile.privateProfile = request.privateProfile
        }

        if (request.autoPlayVideos != null) {
            profile.autoPlayVideos = request.autoPlayVideos
        }

        if (request.saveDataMode != null) {
            profile.saveDataMode = request.saveDataMode
        }

        profile = profileRepository.update(profile)
        return mapToProfileResponse(profile)
    }

    @Transactional
    void updateProfileAvatar(Long accountId, String avatarUrl) {
        Profile profile = getProfileByAccountId(accountId)
        profile.avatarUrl = avatarUrl
        profileRepository.update(profile)
    }
}
