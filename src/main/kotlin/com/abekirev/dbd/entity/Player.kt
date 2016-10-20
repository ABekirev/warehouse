package com.abekirev.dbd.entity

interface IPlayer {
    val id: String
    val firstName: String
    val lastName: String
    val games: Collection<Game>
}

data class Player(override val id: String,
                  override val firstName: String,
                  override val lastName: String,
                  override val games: Collection<Game>) : IPlayer

fun Player.isWhite(game: Game): Boolean {
    return game.whitePlayer.id == id
}

fun Player.points(): Double {
    return games.fold(.0) { points, game ->
        points + points(game)
    }
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

fun Player.gameResult(game: Game): PlayerGameResult {
    return when(game.result) {
        is GameResult.WhiteWon -> if (isWhite(game)) PlayerGameResult.Won() else PlayerGameResult.Lost()
        is GameResult.BlackWon -> if (isWhite(game)) PlayerGameResult.Lost() else PlayerGameResult.Won()
        is GameResult.Draw -> PlayerGameResult.Draw()
    }
}

fun Player.points(game: Game): Double {
    return when (gameResult(game)) {
        is PlayerGameResult.Won -> 1.0
        is PlayerGameResult.Lost -> .0
        is PlayerGameResult.Draw -> .5
    }
}

fun Player.otherPlayer(game: Game): Player {
    return if (isWhite(game)) game.blackPlayer else game.whitePlayer
}

fun Player.bergerCoef(players: Collection<Player>): Double {
    return games.fold(.0) { points, game ->
        points + points(game) * players.filter { it.id == otherPlayer(game).id }.single().points()
    }
}
