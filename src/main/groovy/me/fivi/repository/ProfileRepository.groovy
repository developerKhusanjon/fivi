package me.fivi.repository

import me.fivi.domain.Profile
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface ProfileRepository extends CrudRepository<Profile, Long> {

    Optional<Profile> findByAccountId(Long accountId)

    void deleteByAccountId(Long accountId)
}