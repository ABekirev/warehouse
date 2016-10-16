package com.abekirev.dbd.entity

data class Game(val whitePlayer: Player,
                val blackPlayer: Player,
                val result: GameResult)