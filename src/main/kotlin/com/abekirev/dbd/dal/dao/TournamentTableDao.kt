package com.abekirev.dbd.dal.dao

import com.abekirev.dbd.entity.JsonTournamentTable
import com.abekirev.dbd.entity.TournamentTable
import com.abekirev.dbd.entity.toTournamentTable
import com.fasterxml.jackson.databind.ObjectMapper

class TournamentTableDao {
    private val mapper = ObjectMapper()

    val tournamentTables: Map<Int, Lazy<TournamentTable>> = (2..32).map { playersCount ->
        playersCount to lazy { loadTournamentTable(playersCount) }
    }.toMap()

    private fun loadTournamentTable(playersCount: Int): TournamentTable {
        return ClassLoader.getSystemResourceAsStream("tables/table$playersCount.json").use {
            mapper.readValue(it, JsonTournamentTable::class.java)
                    .let(JsonTournamentTable::toTournamentTable)
        }
    }

    fun getByPlayersCount(playersCount: Int) = tournamentTables[playersCount]?.value
}