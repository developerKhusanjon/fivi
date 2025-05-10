package me.fivi.contoller

import io.micronaut.security.token.jwt.validator.JwtTokenValidator
import me.fivi.domain.Account
import me.fivi.dto.*
import me.fivi.exception.AuthenticationException
import me.fivi.exception.InvalidTokenException
import me.fivi.service.AccountService
import me.fivi.service.AuthService
import groovy.transform.CompileStatic
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator
import io.micronaut.validation.Validated
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.validation.Valid

@CompileStatic
@Controller("/api/auth")
@Validated
class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class)

    private final AccountService accountService
    private final AuthService authService
    private final JwtTokenGenerator jwtTokenGenerator
    private final JwtTokenValidator jwtTokenValidator

    AuthController(AccountService accountService, AuthService authService, JwtTokenGenerator jwtTokenGenerator, JwtTokenValidator jwtTokenValidator) {
        this.accountService = accountService
        this.authService = authService
        this.jwtTokenGenerator = jwtTokenGenerator
        this.jwtTokenValidator = jwtTokenValidator
    }

    @Post("/signup")
    @Secured(SecurityRule.IS_ANONYMOUS)
    HttpResponse<ApiResponse<Void>> signUp(@Body @Valid SignUpRequest request) {
        Account account = accountService.createAccount(request)

        return HttpResponse.created(
                ApiResponse.success("Account created successfully. Please check your email to confirm your account.")
        )
    }

    @Post("/signin")
    @Secured(SecurityRule.IS_ANONYMOUS)
    HttpResponse<ApiResponse<AuthResponse>> signIn(@Body @Valid SignInRequest request) {
        AuthResponse authResponse = authService.authenticate(request)

        return HttpResponse.ok(
                ApiResponse.success("Authentication successful", authResponse)
        )
    }

    @Post("/refresh")
    @Secured(SecurityRule.IS_ANONYMOUS)
    HttpResponse<ApiResponse<AuthResponse>> refreshToken(@Body @Valid RefreshTokenRequest request) {
        try {
            // Validate and parse the refresh token
            Optional<Map<String, Object>> claims = jwtTokenValidator
                                                        .validateToken(request.refreshToken, request).properties as Optional<Map<String, Object>>

            if (!claims.isPresent() || !"refresh".equals(claims.get().get("type"))) {
                throw new InvalidTokenException("Invalid refresh token")
            }

            String accountId = claims.get().get("sub").toString()
            Account account = accountService.getAccountById(Long.parseLong(accountId))

            // Generate new tokens
            Map<String, Object> accessClaims = [
                    "sub": account.id.toString(),
                    "email": account.email,
                    "nickname": account.nickname,
                    "emailConfirmed": account.emailConfirmed,
                    "roles": ["ROLE_USER"]
            ]

            Optional<String> accessToken = jwtTokenGenerator.generateToken(accessClaims)

            Map<String, Object> refreshClaims = [
                    "sub": account.id.toString(),
                    "type": "refresh"
            ] as Map<String, Object>
            Optional<String> refreshToken = jwtTokenGenerator.generateToken(refreshClaims)

            if (!accessToken.isPresent() || !refreshToken.isPresent()) {
                throw new AuthenticationException("Error generating authentication tokens")
            }

            AuthResponse response = new AuthResponse(
                    accessToken: accessToken.get(),
                    refreshToken: refreshToken.get(),
                    accountId: account.id,
                    nickname: account.nickname,
                    emailConfirmed: account.emailConfirmed
            )

            return HttpResponse.ok(ApiResponse.success("Token refreshed", response))

        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired refresh token")
        }
    }

    @Post("/confirm-email")
    @Secured(SecurityRule.IS_ANONYMOUS)
    HttpResponse<ApiResponse<Void>> confirmEmail(@Body @Valid EmailConfirmationRequest request) {
        Account account = accountService.confirmEmail(request.token)

        return HttpResponse.ok(
                ApiResponse.success("Email confirmed successfully")
        )
    }

    @Post("/change-password")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    HttpResponse<ApiResponse<Void>> changePassword(Authentication authentication,
                                                   @Body @Valid ChangePasswordRequest request) {
        Long accountId = Long.valueOf(authentication.name)
        Account account = accountService.getAccountById(accountId)

        // Verify current password
        if (!authService.verifyPassword(request.currentPassword, account.password)) {
            throw new AuthenticationException("Current password is incorrect")
        }

        // Update password
        account.password = authService.hashPassword(request.newPassword)
        accountService.recordLoginTimestamp(accountId) // Update timestamp and save account

        // Invalidate cache entry for the user
        authService.invalidateAuthCache(account.email)

        return HttpResponse.ok(
                ApiResponse.success("Password changed successfully")
        )
    }

    @Get("/check-status")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    HttpResponse<ApiResponse<Map>> checkAuthStatus(Authentication authentication) {
        Map<String, Object> response = [
                authenticated: true,
                accountId: authentication.name, // This is the account ID as a string
                attributes: authentication.attributes
        ]

        return HttpResponse.ok(
                ApiResponse.success("Authentication valid", response)
        )
    }
}
