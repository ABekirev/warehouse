package com.abekirev.dbd.dal.repository

import com.abekirev.dbd.dal.entity.PlayerDto
import com.abekirev.dbd.dal.entity.PlayerProjectionDto
import org.springframework.data.repository.Repository
import java.util.stream.Stream

@org.springframework.stereotype.Repository
interface PlayerProjectionRepository : Repository<PlayerDto, String> {
    fun findAll(): Stream<PlayerProjectionDto>
}
