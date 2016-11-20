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
import java.util.*

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
                                 val place: Int?)

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
            e.key to e.value.map { e ->
                points(e.value) * (pointsMap[e.key] ?: .0)
            }.sum()
        }.toMap()
        val placeMap = players.sortedWith(Comparator { p1, p2 -> -bergerCoefMap[p1]!!.compareTo(bergerCoefMap[p2]!!) })
                .mapIndexed { i, player -> player to i }
                .toMap()
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
                        it.place
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
                    val place: Int?)

    @GetMapping("schedule/")
    fun schedule(modelMap: ModelMap): String {
        return "home"
    }
}