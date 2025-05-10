package me.fivi.domain

import io.micronaut.data.annotation.*
import io.micronaut.core.annotation.Nullable
import groovy.transform.CompileStatic

import javax.validation.constraints.NotNull
import java.time.Instant

@CompileStatic
@MappedEntity("profiles")
class Profile {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    Long id

    @NotNull
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    Account account

    // Profile settings
    Boolean notificationsEnabled = true

    Boolean privateProfile = false

    @Nullable
    String bio

    @Nullable
    String avatarUrl

    @Nullable
    String location

    // Preferences
    String language = "en"

    String theme = "light"

    // App-specific settings
    Boolean autoPlayVideos = true

    Boolean saveDataMode = false

    @DateCreated
    Instant createdAt

    @DateUpdated
    Instant updatedAt
}
