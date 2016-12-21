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

    constructor(firstName: String, lastName: String) : this(null, firstName, lastName)

    constructor(id: String?, firstName: String, lastName: String) : this(id, firstName, lastName, emptySet())

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
                    is GameResult.WhiteWon -> Won
                    is GameResult.BlackWon -> Lost
                    is GameResult.Draw -> Draw
                }
                false -> when (game.result) {
                    is GameResult.WhiteWon -> Lost
                    is GameResult.BlackWon -> Won
                    is GameResult.Draw -> Draw
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
    object Won : PlayerGameResult() {
        override fun toString() = "Won"
    }

    object Lost : PlayerGameResult() {
        override fun toString() = "Lost"
    }

    object Draw : PlayerGameResult() {
        override fun toString() = "Draw"
    }
}

sealed class PlayerGameSide {
    class White : PlayerGameSide()
    class Black : PlayerGameSide()
}
