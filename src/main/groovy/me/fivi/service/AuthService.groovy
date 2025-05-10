package me.fivi.service

import at.favre.lib.crypto.bcrypt.BCrypt
import me.fivi.domain.Account
import me.fivi.dto.AuthResponse
import me.fivi.dto.SignInRequest
import me.fivi.exception.AuthenticationException
import me.fivi.repository.AccountRepository
import groovy.transform.CompileStatic
import io.micronaut.cache.annotation.CacheInvalidate
import io.micronaut.cache.annotation.Cacheable
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@CompileStatic
@Singleton
class AuthService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class)

    private final AccountRepository accountRepository
    private final JwtTokenGenerator jwtTokenGenerator
    private final AccountService accountService

    AuthService(AccountRepository accountRepository, JwtTokenGenerator jwtTokenGenerator, AccountService accountService) {
        this.accountRepository = accountRepository
        this.jwtTokenGenerator = jwtTokenGenerator
        this.accountService = accountService
    }

    @Cacheable(cacheNames = ["auth-cache"], parameters = ["username"])
    Optional<Account> findAccountForAuthentication(String username) {
        // Try finding by email
        Optional<Account> accountOpt = accountRepository.findByEmailAndActive(username, true)

        // If not found, try by nickname
        if (!accountOpt.isPresent()) {
            accountOpt = accountRepository.findByNickname(username)
                    .filter(account -> account.active)
        }

        return accountOpt
    }

    @CacheInvalidate(cacheNames = ["auth-cache"], parameters = ["email"])
    void invalidateAuthCache(String email) {
        // Method body is empty because the annotation does the cache invalidation
    }

    AuthResponse authenticate(SignInRequest request) {
        Optional<Account> accountOpt = findAccountForAuthentication(request.username)

        Account account = accountOpt.orElseThrow(() ->
                new AuthenticationException("Invalid credentials"))

        // Verify password
        BCrypt.Result result = BCrypt.verifyer().verify(
                request.password.toCharArray(),
                account.password.toCharArray()
        )

        if (!result.verified) {
            LOG.warn("Failed login attempt for user: {}", request.username)
            throw new AuthenticationException("Invalid credentials")
        }

        // Generate tokens
        Map<String, Object> claims = [
                "sub": account.id.toString(),
                "email": account.email,
                "nickname": account.nickname,
                "emailConfirmed": account.emailConfirmed,
                "roles": ["ROLE_USER"]
        ]

        Optional<String> accessToken = jwtTokenGenerator.generateToken(claims)

        Map<String, Object> refreshClaims = [
                "sub": account.id.toString(),
                "type": "refresh"
        ] as Map<String, Object>
        Optional<String> refreshToken = jwtTokenGenerator.generateToken(refreshClaims)

        if (!accessToken.isPresent() || !refreshToken.isPresent()) {
            throw new AuthenticationException("Error generating authentication tokens")
        }

        // Update last login timestamp
        accountService.recordLoginTimestamp(account.id)

        return new AuthResponse(
                accessToken: accessToken.get(),
                refreshToken: refreshToken.get(),
                accountId: account.id,
                nickname: account.nickname,
                emailConfirmed: account.emailConfirmed
        )
    }

    String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    boolean verifyPassword(String rawPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(
                rawPassword.toCharArray(),
                hashedPassword.toCharArray()
        )
        return result.verified
    }
}
