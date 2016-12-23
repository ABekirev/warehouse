package com.abekirev.dbd.web.controller

import com.abekirev.dbd.entity.GameResult
import com.abekirev.dbd.entity.GameResult.BlackWon
import com.abekirev.dbd.entity.GameResult.Draw
import com.abekirev.dbd.entity.GameResult.WhiteWon
import com.abekirev.dbd.entity.PlayerGameResult
import com.abekirev.dbd.entity.PlayerGameSide
import com.abekirev.dbd.entity.PlayerGameSide.Black
import com.abekirev.dbd.entity.PlayerGameSide.White
import com.abekirev.dbd.entity.Schedule
import com.abekirev.dbd.entity.TournamentGame
import com.abekirev.dbd.entity.TournamentPlayer
import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.service.TournamentService
import com.abekirev.dbd.toList
import com.abekirev.dbd.web.LocalizedMessageSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.WebApplicationContext
import java.util.*
import kotlin.comparisons.reverseOrder

val tournamentViewName = "tournament/tournament"

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/tournament/")
class TournamentController @Autowired constructor(
        private val tournamentService: TournamentService,
        private val playerService: PlayerService,
        private val localizedMessageSource: LocalizedMessageSource
) {
    @GetMapping("list/")
    fun list(modelMap: ModelMap): String {
        modelMap.addAttribute("tournaments", tournamentService.getAll().toList())
        return "tournament/list"
    }

    @GetMapping(params = arrayOf("id"))
    fun getById(modelMap: ModelMap, id: String): String {
        return getInfoById(modelMap, id)
    }

    @GetMapping("info/", params = arrayOf("id"))
    fun getInfoById(modelMap: ModelMap, id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            modelMap.addAttribute("tournament", tournament)
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return tournamentViewName
    }

    private fun TournamentPlayer.playerGameResult(game: TournamentGame): PlayerGameResult? {
        return side(game)?.let { playerGameSide ->
            playerGameResult(game.result, playerGameSide)
        }
    }

    private fun playerGameResult(result: GameResult, side: PlayerGameSide): PlayerGameResult {
        return when (side) {
            is White -> when (result) {
                is WhiteWon -> PlayerGameResult.Won
                is BlackWon -> PlayerGameResult.Lost
                is Draw -> PlayerGameResult.Draw
            }
            is Black -> when (result) {
                is WhiteWon -> PlayerGameResult.Lost
                is BlackWon -> PlayerGameResult.Won
                is Draw -> PlayerGameResult.Draw
            }
        }
    }

    private fun TournamentPlayer.side(game: TournamentGame): PlayerGameSide? {
        return when (id) {
            game.whitePlayer.id -> PlayerGameSide.White
            game.blackPlayer.id -> PlayerGameSide.Black
            else -> null
        }
    }

    private fun points(result: PlayerGameResult): Double {
        return when (result) {
            is PlayerGameResult.Won -> 1.0
            is PlayerGameResult.Lost -> .0
            is PlayerGameResult.Draw -> .5
        }
    }

    private class ResultTableRow(val player: TournamentPlayer,
                                 val gameResults: Map<TournamentPlayer, PlayerGameResult>,
                                 val points: Double,
                                 val bergerCoef: Double,
                                 val place: Place?)

    private sealed class Place {
        class Single(val place: Int) : Place()
        class Range(val start: Int, val end: Int) : Place()
    }

    private fun resultTable(players: Collection<TournamentPlayer>, games: Collection<TournamentGame>): Collection<ResultTableRow> {
        val gameResultsMap: Map<TournamentPlayer, Map<TournamentPlayer, PlayerGameResult>> = players.map { player ->
            player to games.map { game ->
                player.side(game)?.let { side ->
                    when (side) {
                        is PlayerGameSide.White -> game.blackPlayer
                        is PlayerGameSide.Black -> game.whitePlayer
                    } to playerGameResult(game.result, side)
                }
            }.filterNotNull().toMap()
        }.toMap()
        val pointsMap: Map<TournamentPlayer, Double> = gameResultsMap.map { e -> e.key to e.value.values.map { points(it) }.sum() }.toMap()
        val bergerCoefMap: Map<TournamentPlayer, Double> = gameResultsMap.map { e ->
            e.key to e.value.map { points(it.value) * (pointsMap[it.key] ?: .0) }.sum()
        }.toMap()

        data class Points(val points: Double,
                          val bergerCoef: Double) : Comparable<Points> {
            override fun compareTo(other: Points): Int {
                return when {
                    points == other.points -> bergerCoef.compareTo(other.bergerCoef)
                    else -> points.compareTo(other.points)
                }
            }

        }
        val playersSortedByPoints = players.map { player ->
            player to Points(pointsMap[player]!!, bergerCoefMap[player]!!)
        }
                .groupBy { it.second }
                .mapValues { e -> e.value.map { it.first } }
                .toSortedMap(reverseOrder())
                .values.toList()
        var nextPlace = 1
        val placeMap = playersSortedByPoints
                .flatMap { players ->
                    val place = if (players.size == 1) {
                        Place.Single(nextPlace)
                    } else {
                        Place.Range(nextPlace, nextPlace + players.size - 1)
                    }
                    nextPlace += players.size
                    players.map { it to place }
                }.toMap()
        return players.map { player ->
            ResultTableRow(
                    player,
                    gameResultsMap[player] ?: emptyMap(),
                    pointsMap[player] ?: .0,
                    bergerCoefMap[player] ?: .0,
                    placeMap[player]
            )
        }
    }

    @GetMapping("table/", params = arrayOf("id"))
    fun table(modelMap: ModelMap, id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            modelMap.addAttribute("tournament", tournament)
            val results = resultTable(tournament.players, tournament.games).map {
                ResultRow(
                        it.player,
                        it.gameResults.map { e ->
                            e.key.id to when (e.value) {
                                is PlayerGameResult.Won -> "1"
                                is PlayerGameResult.Lost -> "0"
                                is PlayerGameResult.Draw -> "1/2"
                            }
                        }.toMap(),
                        it.points,
                        it.bergerCoef,
                        it.place?.let {
                            when (it) {
                                is Place.Single -> it.place.toString()
                                is Place.Range -> "${it.start}-${it.end}"
                            }
                        }
                )
            }
            modelMap.addAttribute("results", results)
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return "tournament/table"
    }

    @GetMapping("players/", params = arrayOf("id"))
    fun getPlayersById(modelMap: ModelMap, id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            modelMap.addAttribute("tournament", tournament)
            modelMap.addAttribute("players", tournament.players)
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return "tournament/players"
    }

    class ResultRow(val player: TournamentPlayer,
                    val gameResults: Map<String, String>,
                    val points: Double,
                    val bergerCoef: Double,
                    val place: String?)

    @GetMapping("schedule/", params = arrayOf("id"))
    fun schedule(modelMap: ModelMap, id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            modelMap.addAttribute("tournament", tournament)
            modelMap.addAttribute("schedule", tournament.schedule.toViewSchedule())
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return "tournament/schedule"
    }

    @PostMapping("schedule/gen", params = arrayOf("id"))
    fun genSchedule(modelMap: ModelMap, id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            val updatedTournament = tournamentService.getScheduleTable(tournament.players.count())?.let { table ->
                tournamentService.update(tournament.changeSchedule(table.genSchedule(tournament.players))).get()
            } ?: tournament
            modelMap.addAttribute("tournament", updatedTournament)
            modelMap.addAttribute("schedule", updatedTournament.schedule.toViewSchedule())
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return "tournament/schedule"
    }

    class ScheduleView(val tablesCount: Int,
                       val turnsCount: Int,
                       val games: List<List<String>>)

    private fun Collection<Schedule>.toViewSchedule(): ScheduleView {
        val tablesCount = map { it.table }.max()?.plus(1) ?: 0
        val turnsCount = map { it.turn }.max()?.plus(1) ?: 0
        val map = HashMap<Int, HashMap<Int, Pair<TournamentPlayer, TournamentPlayer>>>()
        this.forEach { schedule ->
            map.compute(schedule.turn) { k, v ->
                val mapAdder: (HashMap<Int, Pair<TournamentPlayer, TournamentPlayer>>) -> Unit = { map -> map.put(schedule.table, schedule.whitePlayer to schedule.blackPlayer) }
                if (v == null) {
                    HashMap<Int, Pair<TournamentPlayer, TournamentPlayer>>().apply(mapAdder)
                } else {
                    mapAdder(v)
                    v
                }
            }
        }
        val games = when {
            turnsCount > 0 && tablesCount > 0 -> (0..turnsCount-1).map { turn ->
                val turnGames = map[turn]!!
                turnGames.let { map ->
                    (0..tablesCount-1).map { table ->
                        turnGames[table]?.let { "${it.first.shortName()} - ${it.second.shortName()}" } ?: "-"
                    }
                }
            }
            else -> emptyList()
        }

        return ScheduleView(tablesCount, turnsCount, games)
    }
}

fun TournamentPlayer.shortName(): String = "$lastName ${firstName.first()}."