package com.abekirev.dbd.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class TournamentTable(val playersCount: Int,
                           val tablesCount: Int,
                           val turnsCount: Int,
                           val turnRows: Map<Turn, Map<Table, TurnRowGame>>) {
    fun genSchedule(players: Collection<TournamentPlayer>): List<Schedule> {
        val playerArr = players.toTypedArray()
        return turnRows.entries.flatMap { turnEntry ->
            val turn = turnEntry.key
            turnEntry.value
                    .filter { gameEntry ->
                        !(gameEntry.value.white.index == playerArr.size || gameEntry.value.black.index == playerArr.size)
                    }
                    .map { gameEntry ->
                        val table = gameEntry.key
                        Schedule(turn.number, table.number, playerArr[gameEntry.value.white.index], playerArr[gameEntry.value.black.index])
                    }
        }
    }
}

data class JsonTournamentTable @JsonCreator constructor(
        @JsonProperty("playersCount") val playersCount: Int,
        @JsonProperty("tablesCount") val tablesCount: Int,
        @JsonProperty("turnsCount") val turnsCount: Int,
        @JsonProperty("turnRows") val turnRows: Map<Int, Map<Int, List<Int>>>
)

data class JsonTournamentGame @JsonCreator constructor(
        @JsonProperty("turn") val turn: Int,
        @JsonProperty("table") val table: Int,
        @JsonProperty("whitePlayerIndex") val whitePlayerIndex: Int,
        @JsonProperty("blackPlayerIndex") val blackPlayerIndex: Int
)

fun TournamentTable.toJsonTournamentTable(): JsonTournamentTable {
    return JsonTournamentTable(
            playersCount,
            tablesCount,
            turnsCount,
            turnRows.map { it.key.number to it.value.entries.map { it.key.number to it.value.let { listOf(it.white.index, it.black.index) } }.toMap() }.toMap()
//            turnRows.flatMap {
//                val turn = it.key
//                it.value.entries.map { JsonTournamentGame(turn.number, it.key.number, it.value.white.index, it.value.black.index) }
//            }
    )
}

fun JsonTournamentTable.toTournamentTable(): TournamentTable {
    return TournamentTable(
            playersCount,
            tablesCount,
            turnsCount,
            turnRows.map { Turn(it.key) to it.value.map { Table(it.key) to TurnRowGame(TurnRowGamePlayer(it.value[0]), TurnRowGamePlayer(it.value[1])) }.toMap() }.toMap()
//            turnRows.groupBy { it.turn }
//                    .map { Turn(it.key) to it.value.groupBy { it.table }.map { Table(it.key) to it.value.first().let { TurnRowGame(TurnRowGamePlayer(it.whitePlayerIndex), TurnRowGamePlayer(it.blackPlayerIndex ))} }.toMap() }
//                    .toMap()
    )
}

data class TurnRowGame(val white: TurnRowGamePlayer, val black: TurnRowGamePlayer)
data class TurnRowGamePlayer(val index: Int)
data class Turn(val number: Int)
data class Table(val number: Int)
