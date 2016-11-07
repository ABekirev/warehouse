package com.abekirev.dbd.dal.repository

import com.abekirev.dbd.dal.entity.TournamentDto
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TournamentRepository : CrudRepository<TournamentDto, String> {
}