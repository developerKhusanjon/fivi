package me.fivi.dto

import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@CompileStatic
@Introspected
class SignUpRequest {
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
    @Size(min = 8, max = 100)
    String password
}

@CompileStatic
@Introspected
class SignInRequest {
    @NotBlank
    String username  // can be email or nickname

    @NotBlank
    String password
}

@CompileStatic
@Introspected
class AuthResponse {
    String accessToken
    String refreshToken
    Long accountId
    String nickname
    Boolean emailConfirmed
}

@CompileStatic
@Introspected
class RefreshTokenRequest {
    @NotBlank
    String refreshToken
}

@CompileStatic
@Introspected
class EmailConfirmationRequest {
    @NotBlank
    String token
}

@CompileStatic
@Introspected
class ResetPasswordRequest {
    @NotBlank
    @Email
    String email
}

@CompileStatic
@Introspected
class ChangePasswordRequest {
    @NotBlank
    String currentPassword

    @NotBlank
    @Size(min = 8, max = 100)
    String newPassword
}
