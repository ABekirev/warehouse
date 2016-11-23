package com.abekirev.dbd.entity

import com.abekirev.dbd.isOdd
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other
import java.time.LocalDate

data class Tournament(val id: String?,
                      val name: String,
                      val dateFrom: LocalDate,
                      val dateTo: LocalDate,
                      val players: Collection<TournamentPlayer>,
                      val schedule: Collection<Schedule>,
                      val games: Collection<TournamentGame>,
                      val winner: TournamentPlayer?) {
    constructor(
            name: String,
            dateFrom: LocalDate,
            dateTo: LocalDate
    ) : this(name, dateFrom, dateTo, emptySet(), emptySet(), emptySet(), null)

    constructor(
            name: String,
            dateFrom: LocalDate,
            dateTo: LocalDate,
            players: Collection<TournamentPlayer>,
            schedule: Collection<Schedule>,
            games: Collection<TournamentGame>,
            winner: TournamentPlayer?
    ) : this(null, name, dateFrom, dateTo, players, schedule, games, winner)

    fun addGame(game: TournamentGame): Tournament {
        return Tournament(id, name, dateFrom, dateTo, players, schedule, games.filter { !(game.whitePlayer.playerOf(it) && game.blackPlayer.playerOf(game)) }.plus(game), winner)
    }

    fun addPlayer(player: TournamentPlayer): Tournament {
        return Tournament(id, name, dateFrom, dateTo, players.filter { player.id != it.id }.plus(player), schedule, games, winner)
    }

    fun genSchedule(): Tournament {
        val playersCount = players.count()
        val playersArray = players.toTypedArray()
        val schedule = genSchedule(playersCount).map { Schedule(it.turn, it.table, playersArray[it.white], playersArray[it.black]) }
        return Tournament(id, name, dateFrom, dateTo, players, schedule, games, winner)
    }
}

data class TournamentPlayer(val id: String,
                            val firstName: String,
                            val lastName: String) {
    constructor(player: GamePlayer) : this(player.id, player.firstName, player.lastName)
    constructor(player: Player) : this(player.id!!, player.firstName, player.lastName)
}

fun TournamentPlayer.playerOf(game: TournamentGame): Boolean {
    return game.whitePlayer.id == id || game.blackPlayer.id == id
}

fun TournamentPlayer.otherPlayer(game: TournamentGame): TournamentPlayer {
    return if (game.whitePlayer.id == id) game.blackPlayer else game.whitePlayer
}

data class TournamentGame(val id: String,
                          val whitePlayer: TournamentPlayer,
                          val blackPlayer: TournamentPlayer,
                          val result: GameResult) {
    constructor(game: Game) : this(game.id!!, TournamentPlayer(game.whitePlayer), TournamentPlayer(game.blackPlayer), game.result)
}

data class Schedule(val turn: Int,
                    val table: Int,
                    val whitePlayer: TournamentPlayer,
                    val blackPlayer: TournamentPlayer)

data class SchedulePos(val turn: Int,
                       val table: Int,
                       val white: Int,
                       val black: Int)

fun genSchedule(playersCount: Int): Collection<SchedulePos> {
    if (playersCount < 2) return emptySet()
    val scheduleSize = if (playersCount.isOdd()) playersCount else (playersCount + 1)
    val tableSize = scheduleSize / 2
    return (1..tableSize).flatMap { turn ->
        (1..tableSize).map { table ->
            turn to table
        }
    }.flatMap { p ->
        val (turn, table) = p
        val oneIndex = table - 1
        val otherIndex = (tableSize + turn - 1) % tableSize + tableSize
        if (scheduleSize != playersCount && otherIndex == scheduleSize)
            emptyList()
        else
            listOf(SchedulePos(
                    turn,
                    table,
                    if (!turn.isOdd()) oneIndex else otherIndex,
                    if (!turn.isOdd()) otherIndex else oneIndex
            ))
    }
}