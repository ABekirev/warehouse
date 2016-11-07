package com.abekirev.dbd.dal.repository

import com.abekirev.dbd.dal.entity.PlayerDto
import com.abekirev.dbd.dal.entity.PlayerProjectionDto
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import java.util.stream.Stream

@Repository
interface PlayerRepository : CrudRepository<PlayerDto, String> {
    fun findAllProjections(): Stream<PlayerProjectionDto>
    fun findById(id: String): Optional<PlayerDto>
}