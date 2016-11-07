package com.abekirev.dbd.entity

data class Game(val id: String?,
                val tournamentId: String,
                val tournamentName: String,
                val whitePlayer: GamePlayer,
                val blackPlayer: GamePlayer,
                val result: GameResult)

sealed class GameResult {
    class WhiteWon : GameResult() {
        override fun toString() = "White"
    }
    class BlackWon : GameResult() {
        override fun toString() = "Black"
    }
    class Draw : GameResult() {
        override fun toString() = "Draw"
    }
}

data class GamePlayer(val id: String,
                      val firstName: String,
                      val lastName: String)

