package me.fivi.service

import at.favre.lib.crypto.bcrypt.BCrypt
import io.micronaut.transaction.annotation.Transactional
import me.fivi.domain.Account
import me.fivi.domain.Profile
import me.fivi.dto.SignUpRequest
import me.fivi.exception.AccountAlreadyExistsException
import me.fivi.exception.EmailConfirmationException
import me.fivi.exception.NotFoundException
import me.fivi.repository.AccountRepository
import me.fivi.repository.ProfileRepository
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Instant

@CompileStatic
@Singleton
class AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class)

    private final AccountRepository accountRepository
    private final ProfileRepository profileRepository
    private final EmailService emailService

    @Value('${email.confirmation.expiration:86400}')
    private int emailConfirmationExpirationSeconds

    AccountService(AccountRepository accountRepository, ProfileRepository profileRepository, EmailService emailService) {
        this.accountRepository = accountRepository
        this.profileRepository = profileRepository
        this.emailService = emailService
    }

    @Transactional
    Account createAccount(SignUpRequest request) {
        // Check if email or nickname already exists
        if (accountRepository.existsByEmail(request.email)) {
            throw new AccountAlreadyExistsException("Email already in use")
        }

        if (accountRepository.existsByNickname(request.nickname)) {
            throw new AccountAlreadyExistsException("Nickname already in use")
        }

        // Hash password
        String hashedPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

        // Generate email confirmation token
        String confirmationToken = UUID.randomUUID().toString()

        // Create account
        Account account = new Account(
                firstName: request.firstName,
                lastName: request.lastName,
                nickname: request.nickname,
                email: request.email,
                password: hashedPassword,
                emailConfirmed: false,
                emailConfirmationToken: confirmationToken,
                emailConfirmationTokenExpiry: Instant.now().plusSeconds(emailConfirmationExpirationSeconds)
        )

        account = accountRepository.save(account)

        // Create default profile for the account
        Profile profile = new Profile(
                account: account
        )
        profileRepository.save(profile)

        // Send confirmation email
        try {
            emailService.sendEmailConfirmation(account.email, confirmationToken)
        } catch (Exception e) {
            LOG.error("Failed to send confirmation email", e)
            // Continue with account creation even if email fails
        }

        return account
    }

    @Transactional
    Account confirmEmail(String token) {
        Account account = accountRepository.findByEmailConfirmationToken(token)
                .orElseThrow(() -> new EmailConfirmationException("Invalid confirmation token"))

        // Check if token is expired
        if (account.emailConfirmationTokenExpiry.isBefore(Instant.now())) {
            throw new EmailConfirmationException("Confirmation token expired")
        }

        // Confirm email
        account.emailConfirmed = true
        account.emailConfirmationToken = null
        account.emailConfirmationTokenExpiry = null

        return accountRepository.update(account)
    }

    Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found"))
    }

    Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Account not found"))
    }

    boolean isNicknameAvailable(String nickname) {
        return !accountRepository.existsByNickname(nickname)
    }

    boolean isEmailAvailable(String email) {
        return !accountRepository.existsByEmail(email)
    }

    @Transactional
    void recordLoginTimestamp(Long accountId) {
        Account account = getAccountById(accountId)
        account.lastLoginAt = Instant.now()
        accountRepository.update(account)
    }
}
