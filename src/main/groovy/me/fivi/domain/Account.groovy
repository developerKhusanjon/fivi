package me.fivi.domain

import io.micronaut.data.annotation.*
import io.micronaut.core.annotation.Nullable
import groovy.transform.CompileStatic

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import java.time.Instant

@CompileStatic
@MappedEntity("accounts")
class Account {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    Long id

    @NotBlank
    @Size(min = 2, max = 50)
    String firstName

    @NotBlank
    @Size(min = 2, max = 50)
    String lastName

    @NotBlank
    @Size(min = 3, max = 30)
    String nickname

    @NotBlank
    @Email
    String email

    @NotBlank
    String password

    Boolean emailConfirmed = false

    @Nullable
    String emailConfirmationToken

    @Nullable
    Instant emailConfirmationTokenExpiry

    @DateCreated
    Instant createdAt

    @DateUpdated
    Instant updatedAt

    @Nullable
    Instant lastLoginAt

    Boolean active = true
}
