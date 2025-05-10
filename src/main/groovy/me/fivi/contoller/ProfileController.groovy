package me.fivi.contoller

import me.fivi.domain.Profile
import me.fivi.dto.ApiResponse
import me.fivi.dto.ProfileResponse
import me.fivi.dto.UpdateProfileRequest
import me.fivi.service.ProfileService
import groovy.transform.CompileStatic
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import io.micronaut.validation.Validated
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.validation.Valid
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@CompileStatic
@Controller("/api/profiles")
@Validated
@Secured(SecurityRule.IS_AUTHENTICATED)
class ProfileController {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileController.class)

    private final ProfileService profileService

    // In a real app, you'd use cloud storage or a CDN
    private static final String UPLOAD_PATH = "uploads/avatars"

    ProfileController(ProfileService profileService) {
        this.profileService = profileService

        // Ensure upload directory exists
        Path path = Paths.get(UPLOAD_PATH)
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
    }

    @Get("/me")
    HttpResponse<ApiResponse<ProfileResponse>> getMyProfile(Authentication authentication) {
        Long accountId = Long.valueOf(authentication.name)
        Profile profile = profileService.getProfileByAccountId(accountId)
        ProfileResponse response = profileService.mapToProfileResponse(profile)

        return HttpResponse.ok(ApiResponse.success("Profile retrieved successfully", response))
    }

    @Put("/me")
    HttpResponse<ApiResponse<ProfileResponse>> updateMyProfile(Authentication authentication,
                                                               @Body @Valid UpdateProfileRequest request) {
        Long accountId = Long.valueOf(authentication.name)
        ProfileResponse updatedProfile = profileService.updateProfile(accountId, request)

        return HttpResponse.ok(ApiResponse.success("Profile updated successfully", updatedProfile))
    }

    @Post(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA)
    HttpResponse<ApiResponse<Map>> uploadAvatar(Authentication authentication,
                                                @Part("avatar") CompletedFileUpload file) {
        Long accountId = Long.valueOf(authentication.name)

        // Validate file type
        String contentType = file.contentType.toString()
        if (!contentType.startsWith("image/")) {
            return HttpResponse.badRequest(ApiResponse.error("Only image files are allowed"))
        }

        try {
            // Generate unique filename
            String originalFilename = file.filename
            String extension = originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ""
            String newFilename = UUID.randomUUID().toString() + extension

            // Save file
            Path destinationPath = Paths.get(UPLOAD_PATH, newFilename)
            Files.write(destinationPath, file.bytes)

            // In a real app, this would be a URL to your storage service or CDN
            String avatarUrl = "/api/uploads/avatars/" + newFilename

            // Update profile with new avatar URL
            profileService.updateProfileAvatar(accountId, avatarUrl)

            Map<String, String> result = [
                    avatarUrl: avatarUrl
            ]

            return HttpResponse.ok(
                    ApiResponse.success("Avatar uploaded successfully", result)
            )
        } catch (Exception e) {
            LOG.error("Unexpected error while uploading avatar file: " + e.message)
            return HttpResponse.serverError(ApiResponse.error("Error while uploading avatar"))
        }
    }
}