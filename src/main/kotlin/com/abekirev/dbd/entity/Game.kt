package com.abekirev.dbd.entity

data class Game(val whitePlayer: Player,
                val blackPlayer: Player,
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
