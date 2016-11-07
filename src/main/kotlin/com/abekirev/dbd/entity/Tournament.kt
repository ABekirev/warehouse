package com.abekirev.dbd.entity

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
            dateTo: LocalDate,
            players: Collection<TournamentPlayer>,
            schedule: Collection<Schedule>,
            games: Collection<TournamentGame>,
            winner: TournamentPlayer?
    ) : this(null, name, dateFrom, dateTo, players, schedule, games, winner)

    fun addGame(game: TournamentGame): Tournament {
        return Tournament(id, name, dateFrom, dateTo, players, schedule, games.filter { !(game.whitePlayer.playerOf(it) && game.blackPlayer.playerOf(game)) }.plus(game), winner)
    }
}

data class TournamentPlayer(val id: String,
                            val firstName: String,
                            val lastName: String) {
    constructor(player: GamePlayer) : this(player.id, player.firstName, player.lastName)
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
                    val whitePlayer: TournamentPlayer,
                    val blackPlayer: TournamentPlayer)