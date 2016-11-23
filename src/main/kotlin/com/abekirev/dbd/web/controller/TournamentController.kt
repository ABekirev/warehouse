package com.abekirev.dbd.web.controller

import com.abekirev.dbd.entity.GameResult
import com.abekirev.dbd.entity.GameResult.BlackWon
import com.abekirev.dbd.entity.GameResult.Draw
import com.abekirev.dbd.entity.GameResult.WhiteWon
import com.abekirev.dbd.entity.PlayerGameResult
import com.abekirev.dbd.entity.PlayerGameResult.Lost
import com.abekirev.dbd.entity.PlayerGameResult.Won
import com.abekirev.dbd.entity.PlayerGameSide
import com.abekirev.dbd.entity.PlayerGameSide.Black
import com.abekirev.dbd.entity.PlayerGameSide.White
import com.abekirev.dbd.entity.TournamentGame
import com.abekirev.dbd.entity.TournamentPlayer
import com.abekirev.dbd.isOdd
import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.service.TournamentService
import com.abekirev.dbd.toList
import com.abekirev.dbd.web.LocalizedMessageSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.WebApplicationContext
import kotlin.comparisons.compareBy
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
        val tournament = tournamentService.getById(id).get()
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
                is WhiteWon -> Won()
                is BlackWon -> Lost()
                is Draw -> PlayerGameResult.Draw()
            }
            is Black -> when (result) {
                is WhiteWon -> PlayerGameResult.Draw()
                is BlackWon -> Won()
                is Draw -> PlayerGameResult.Draw()
            }
        }
    }

    private fun TournamentPlayer.side(game: TournamentGame): PlayerGameSide? {
        return when (id) {
            game.whitePlayer.id -> PlayerGameSide.White()
            game.blackPlayer.id -> PlayerGameSide.Black()
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
                        is PlayerGameSide.White -> game.whitePlayer
                        is PlayerGameSide.Black -> game.blackPlayer
                    } to playerGameResult(game.result, side)
                }
            }.filterNotNull().toMap()
        }.toMap()
        val pointsMap: Map<TournamentPlayer, Double> = gameResultsMap.map { e -> e.key to e.value.values.map { points(it) }.sum() }.toMap()
        val bergerCoefMap: Map<TournamentPlayer, Double> = gameResultsMap.map { e ->
            e.key to e.value.map { points(it.value) * (pointsMap[it.key] ?: .0) }.sum()
        }.toMap()
        val playersSortedByBergerCoef = bergerCoefMap.entries
                .groupBy { it.value }
                .mapValues { e -> e.value.map { it.key } }
                .toSortedMap(reverseOrder())
                .values.toList()
        var nextPlace = 1
        val placeMap = playersSortedByBergerCoef
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
        val tournament = tournamentService.getById(id).get()
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
        val tournament = tournamentService.getById(id).get()
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
        val tournament = tournamentService.getById(id).get()
        if (tournament != null) {
            modelMap.addAttribute("tournament", tournament)
            modelMap.addAttribute("schedule", tournament.schedule)
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return "tournament/schedule"
    }

    @GetMapping("schedule/gen", params = arrayOf("id"))
    fun getSchedule(modelMap: ModelMap, id: String): String {
        val tournament = tournamentService.getById(id).get()
        if (tournament != null) {
            val updatedTournament = tournamentService.update(tournament.genSchedule()).get()
            modelMap.addAttribute("tournament", updatedTournament)
            val schedule: List<Array<Pair<TournamentPlayer, TournamentPlayer>?>> = updatedTournament.schedule
                    .groupBy { it.turn }
                    .let {
                        val count = it.count()
                        it.mapValues { p ->
                            val array = kotlin.arrayOfNulls<Pair<TournamentPlayer, TournamentPlayer>>(count)
                            p.value.forEach { schedule ->
                                array[schedule.table - 1] = schedule.whitePlayer to schedule.blackPlayer
                            }
                            array
                        }.toSortedMap(compareBy({ it }, { it })).values.toList()
                    }

            modelMap.addAttribute("tableSize", tournament.players.size.let { if (it.isOdd()) it else (it + 1) })
            modelMap.addAttribute("schedule", schedule)
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return "tournament/schedule"
    }
}