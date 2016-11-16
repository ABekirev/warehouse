package com.abekirev.dbd.dal.repository

import com.abekirev.dbd.dal.entity.PlayerDto
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : StreamAndAsyncCrudRepository<PlayerDto, String>
