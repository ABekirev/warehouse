package com.abekirev.dbd.entity

sealed class GameResult {
    class WhiteWon : GameResult()
    class BlackWon : GameResult()
    class Draw : GameResult()
}
