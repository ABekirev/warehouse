package com.abekirev.dbd.dal.repository

import com.abekirev.dbd.dal.entity.TournamentDto
import org.springframework.stereotype.Repository

@Repository
interface TournamentRepository : StreamAndAsyncCrudRepository<TournamentDto, String> {
}