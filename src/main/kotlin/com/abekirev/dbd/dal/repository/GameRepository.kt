package com.abekirev.dbd.dal.repository

import com.abekirev.dbd.dal.entity.GameDto
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : StreamAndAsyncCrudRepository<GameDto, String> {
}