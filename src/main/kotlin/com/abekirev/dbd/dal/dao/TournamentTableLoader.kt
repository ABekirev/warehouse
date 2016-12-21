package com.abekirev.dbd.dal.dao

import com.abekirev.dbd.entity.Table
import com.abekirev.dbd.entity.TournamentTable
import com.abekirev.dbd.entity.Turn
import com.abekirev.dbd.entity.TurnRowGame
import com.abekirev.dbd.entity.TurnRowGamePlayer
import com.abekirev.dbd.entity.toJsonTournamentTable
import com.abekirev.dbd.isOdd
import com.abekirev.dbd.streamEx
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import org.jsoup.Jsoup
import java.io.File

fun main(args: Array<String>) {
    val mapper = ObjectMapper().apply {
        enable(INDENT_OUTPUT)
    }
    (3..32).forEach { playersCount ->
        val tournamentTable = loadTournamentTable(playersCount).toJsonTournamentTable()
        val filePath = "D:\\Projects\\warehouse\\src\\main\\resources\\tables\\table${playersCount}.json"
        mapper.writeValue(File(filePath), tournamentTable)
    }
}

private val queryStr = "http://chess.sainfo.ru/tabl/tabl%d.html"

fun loadTournamentTable(playersCount: Int): TournamentTable {
    val map: Map<Turn, Map<Table, TurnRowGame>> = String.format(queryStr, if (playersCount.isOdd()) playersCount else (playersCount + 1)).let {
        Jsoup.connect(it).get().select("body pre").first().text().replace("\r", "").split("\n").streamEx()
                .skip(1)
                .map { it.toTurnRow( if (playersCount.isOdd()) false else true) }
                .toList()
                .toMap()
    }
    val turnsCount = map.entries.map { it.key.number }.max()!!
    val tablesCount = map.flatMap { it.value.entries.map { it.key.number } }.max()!!
    return TournamentTable(
            playersCount,
            tablesCount,
            turnsCount,
            map
    )
}

val parenteciesSkipRegex = Regex("([\\p{Digit}]+-\\([\\p{Digit}]+\\))|(\\([\\p{Digit}]+\\)-[\\p{Digit}]+)")

fun String.toTurnRow(skipWithParentecies: Boolean): Pair<Turn, Map<Table, TurnRowGame>> = split(" тур ").let { parts ->
    Pair(
            Turn(parts[0].trim().toInt() - 1),
            parts[1].trim()
                    .replace(" - ", "-")
                    .let { str ->
                        if (skipWithParentecies) {
                            str.replace(parenteciesSkipRegex, "")
                        } else {
                            str.replace("(", "").replace(")", "")
                        }
                    }
                    .split(" ")
                    .filter(String::isNotEmpty)
                    .map(String::toGame)
                    .mapIndexed { i, turnRowGame ->
                        Table(i) to turnRowGame
                    }.toMap()
    )
}

fun String.toGame(): TurnRowGame = split("-").let { parts ->
    TurnRowGame(TurnRowGamePlayer(parts[0].toInt() - 1), TurnRowGamePlayer(parts[1].toInt() - 1))
}