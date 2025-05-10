package me.fivi.security

import io.micronaut.core.annotation.Nullable
import me.fivi.domain.Account
import me.fivi.service.AuthService
import groovy.transform.CompileStatic
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

@CompileStatic
@Singleton
class JwtAuthenticationProvider implements AuthenticationProvider {

    private final AuthService authService

    JwtAuthenticationProvider(AuthService authService) {
        this.authService = authService
    }

    @Override
    Publisher<AuthenticationResponse> authenticate(@Nullable Object httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create({ emitter ->
            String identity = authenticationRequest.identity.toString()
            String password = authenticationRequest.secret.toString()

            try {
                Optional<Account> accountOpt = authService.findAccountForAuthentication(identity)

                if (!accountOpt.isPresent()) {
                    emitter.error(AuthenticationResponse.exception("Invalid credentials"))
                    return
                }

                Account account = accountOpt.get()

                // Verify password
                if (!authService.verifyPassword(password, account.password)) {
                    emitter.error(AuthenticationResponse.exception("Invalid credentials"))
                    return
                }

                // Successful authentication
                List<String> roles = ["ROLE_USER"]

                Map<String, Object> attributes = [
                        "id": account.id,
                        "email": account.email,
                        "firstName": account.firstName,
                        "lastName": account.lastName,
                        "nickname": account.nickname,
                        "emailConfirmed": account.emailConfirmed
                ] as Map<String, Object>

                emitter.next(
                        AuthenticationResponse.success(account.id.toString(), roles, attributes)
                )
                emitter.complete()

            } catch (Exception e) {
                emitter.error(AuthenticationResponse.exception("Authentication failed: " + e.message))
            }
        }, FluxSink.OverflowStrategy.ERROR)
    }
}
