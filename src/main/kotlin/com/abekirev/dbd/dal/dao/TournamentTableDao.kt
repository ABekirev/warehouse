package com.abekirev.dbd.dal.dao

import com.abekirev.dbd.entity.Table
import com.abekirev.dbd.entity.TournamentTable
import com.abekirev.dbd.entity.Turn
import com.abekirev.dbd.entity.TurnRowGame
import com.abekirev.dbd.entity.TurnRowGamePlayer
import org.jsoup.Jsoup
import rx.lang.kotlin.toObservable

private val queryStr = "http://chess.sainfo.ru/tabl/tabl%d.html"

class TournamentTableDao {
    val tournamentTables: Map<Int, Lazy<TournamentTable>> = (4..32 step 2).map {
        it to lazy {
            TournamentTable(
                    it,
                    String.format(queryStr, it).let {
                        Jsoup.connect(it).get().select("body pre").first().text().replace("\r", "").split("\n").toObservable()
                                .skip(1)
                                .map(String::toTurnRow)
                                .toList().toBlocking().single().toMap()
                    }
            )
        }
    }.toMap()

    fun getByPlayersCount(playersCount: Int) = tournamentTables[playersCount]?.value
}

fun String.toTurnRow(): Pair<Turn, Map<Table, TurnRowGame>> = split(" тур ").let { parts ->
    Pair(
            Turn(parts[0].trim().toInt() - 1),
            parts[1].trim()
                    .replace(" - ", "-")
                    .replace("(", "")
                    .replace(")", "")
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