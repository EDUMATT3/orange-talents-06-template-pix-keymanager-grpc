package br.com.edumatt3.pix.register

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface PixKeyRepository : JpaRepository<PixKey, UUID>{

    fun existsByKey(key: String): Boolean
    fun findByKey(key: String): Optional<PixKey>
}
