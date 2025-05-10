package me.fivi.dto

import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.Nullable

@CompileStatic
@Introspected
class ProfileResponse {
    Long id
    Long accountId
    String firstName
    String lastName
    String nickname
    String email
    Boolean notificationsEnabled
    Boolean privateProfile
    String bio
    String avatarUrl
    String location
    String language
    String theme
    Boolean autoPlayVideos
    Boolean saveDataMode
}

@CompileStatic
@Introspected
class UpdateProfileRequest {
    @Nullable
    String bio

    @Nullable
    String location

    @Nullable
    String language

    @Nullable
    String theme

    @Nullable
    Boolean notificationsEnabled

    @Nullable
    Boolean privateProfile

    @Nullable
    Boolean autoPlayVideos

    @Nullable
    Boolean saveDataMode
}
