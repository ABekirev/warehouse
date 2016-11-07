package com.abekirev.dbd.entity

import com.abekirev.dbd.entity.PlayerGameResult.Draw
import com.abekirev.dbd.entity.PlayerGameResult.Lost
import com.abekirev.dbd.entity.PlayerGameResult.Won
import com.abekirev.dbd.entity.PlayerGameSide.Black
import com.abekirev.dbd.entity.PlayerGameSide.White

interface IPlayer {
    val id: String?
    val firstName: String
    val lastName: String
    val games: Collection<PlayerGame>
}

data class Player(override val id: String?,
                  override val firstName: String,
                  override val lastName: String,
                  override val games: Collection<PlayerGame>) : IPlayer {
    fun addGame(game: PlayerGame): Player {
        return Player(id, firstName, lastName, games.filter { game.opponent.id != it.opponent.id }.plus(game))
    }
}

data class PlayerGame(val id: String,
                      val tournamentId: String,
                      val tournamentName: String,
                      val side: PlayerGameSide,
                      val opponent: Opponent,
                      val result: PlayerGameResult) {
    constructor(
            player: Player,
            game: Game
    ) : this(
            game.id!!,
            game.tournamentId,
            game.tournamentName,
            if (player.isWhite(game)) White() else Black(),
            Opponent(if (player.isWhite(game)) game.whitePlayer else game.blackPlayer),
            when (player.isWhite(game)) {
                true -> when (game.result) {
                    is GameResult.WhiteWon -> Won()
                    is GameResult.BlackWon -> Lost()
                    is GameResult.Draw -> Draw()
                }
                false -> when (game.result) {
                    is GameResult.WhiteWon -> Lost()
                    is GameResult.BlackWon -> Won()
                    is GameResult.Draw -> Draw()
                }
            }
    )
}

fun Player.isWhite(game: Game): Boolean {
    return id == game.whitePlayer.id
}

data class Opponent(val id: String,
                    val firstName: String,
                    val lastName: String) {
    constructor(player: GamePlayer) : this(player.id, player.firstName, player.lastName)
}

sealed class PlayerGameResult {
    class Won : PlayerGameResult() {
        override fun toString() = "Won"
    }

    class Lost : PlayerGameResult() {
        override fun toString() = "Lost"
    }

    class Draw : PlayerGameResult() {
        override fun toString() = "Draw"
    }
}

sealed class PlayerGameSide {
    class White : PlayerGameSide()
    class Black : PlayerGameSide()
}

fun Player.points(): Double {
    return games.fold(.0) { points, game ->
        points + points(game)
    }
}

fun points(game: PlayerGame): Double {
    return when (game.result) {
        is PlayerGameResult.Won -> 1.0
        is PlayerGameResult.Lost -> .0
        is PlayerGameResult.Draw -> .5
    }
}

fun Player.bergerCoef(players: Collection<Player>): Double {
    return games.fold(.0) { points, game ->
        points + points(game) * players.filter { it.id == game.opponent.id }.single().points()
    }
}
