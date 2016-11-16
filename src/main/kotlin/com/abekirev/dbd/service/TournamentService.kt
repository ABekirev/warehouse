package com.abekirev.dbd.service

import com.abekirev.dbd.dal.dao.TournamentDao
import com.abekirev.dbd.entity.Tournament
import java.util.stream.Stream


class TournamentService(private val tournamentDao: TournamentDao) {
    fun getAll(): Stream<Tournament> = tournamentDao.getAll()

    fun getById(id: String) = tournamentDao.get(id)

    fun create(tournament: Tournament) = tournamentDao.create(tournament)

    fun update(tournament: Tournament) = tournamentDao.update(tournament)

    fun delete(id: String) = tournamentDao.delete(id)
}