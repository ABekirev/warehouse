package com.abekirev.dbd.schema

import kotlinx.nosql.Column
import kotlinx.nosql.ListColumn
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Players : DocumentSchema<PlayerDto>("players", PlayerDto::class) {
    val _id = string("_id")
    val firstName = string("firstName")
    val secondName = string("secondName")
    val games = GameColumn()

    class GameColumn : ListColumn<GameDto, Players>("games", GameDto::class) {
        val side = string("side")
        val opponent = OpponentColumn()
        val result = string("result")

        class OpponentColumn : Column<OpponentDto, Players>("opponent", OpponentDto::class) {
            val _id = string("_id")
            val firstName = string("firstName")
            val secondName = string("secondName")
        }
    }
}

data class PlayerDto(val _id: String,
                     val firstName: String,
                     val secondName: String,
                     val games: List<GameDto>)

data class OpponentDto(val _id: String,
                       val firstName: String,
                       val secondName: String)

data class GameDto(val side: String,
                   val opponent: OpponentDto,
                   val result: String)
