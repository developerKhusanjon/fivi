package me.fivi.repository

import me.fivi.domain.Account
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByEmail(String email)

    Optional<Account> findByNickname(String nickname)

    Optional<Account> findByEmailConfirmationToken(String token)

    boolean existsByEmail(String email)

    boolean existsByNickname(String nickname)

    Optional<Account> findByEmailAndActive(String email, boolean active)
}
