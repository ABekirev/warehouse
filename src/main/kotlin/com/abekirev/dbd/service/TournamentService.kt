package com.abekirev.dbd.service

import com.abekirev.dbd.dal.dao.TournamentDao
import com.abekirev.dbd.entity.Tournament


class TournamentService(private val tournamentDao: TournamentDao) {
    fun getAll(): Collection<Tournament> {
        return tournamentDao.getAll()
    }

    fun getById(id: String): Tournament? {
        return tournamentDao.get(id)
    }

    fun create(tournament: Tournament): String {
        return tournamentDao.create(tournament)
    }

    fun update(tournament: Tournament) {
        return tournamentDao.update(tournament)
    }

    fun delete(id: String) {
        return tournamentDao.delete(id)
    }
}