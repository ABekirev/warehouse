package com.abekirev.dbd.web.controller.staff

import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.GamePlayer
import com.abekirev.dbd.entity.GameResult.BlackWon
import com.abekirev.dbd.entity.GameResult.Draw
import com.abekirev.dbd.entity.GameResult.WhiteWon
import com.abekirev.dbd.entity.Player
import com.abekirev.dbd.entity.PlayerGame
import com.abekirev.dbd.service.GameService
//import com.abekirev.dbd.entity.otherPlayer
import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.service.TournamentService
import com.abekirev.dbd.web.LocalizedMessageSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.WebApplicationContext
import javax.validation.Valid

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/staff/")
class StaffController @Autowired constructor(private val playerService: PlayerService,
                                             private val gameService: GameService,
                                             private val tournamentService: TournamentService,
                                             private val localizedMessageSource: LocalizedMessageSource) {

    private val playerRegistrationViewName = "staff/registerPlayer"
    private val gameRegistrationViewName = "staff/registerGame"

    @GetMapping("register/player/")
    fun registerPlayer(playerRegistrationForm: PlayerRegistrationForm): String {
        return playerRegistrationViewName
    }

    @PostMapping("register/player/")
    fun registerPlayerAction(
            @Valid playerRegistrationForm: PlayerRegistrationForm,
            bindingResult: BindingResult,
            modelMap: ModelMap
    ): String {
        if (!bindingResult.hasErrors()) {
            playerService.create(com.abekirev.dbd.entity.Player(null, playerRegistrationForm.firstName!!, playerRegistrationForm.lastName!!, emptySet()))
            modelMap.addAttribute("success", localizedMessageSource.getMessage("staff.register.player.success"))
            playerRegistrationForm.clear()
        }
        return playerRegistrationViewName
    }

    class Player(val id: String, val firstName: String, val lastName: String)

    fun ModelMap.fillMapWithPlayers() {
        put("players", playerService.getAllProjections().map { Player(it.id!!, it.firstName, it.lastName) })
    }

    @GetMapping("register/game/")
    fun registerGame(
            gameRegistrationForm: GameRegistrationForm,
            modelMap: ModelMap
    ): String {
        modelMap.fillMapWithPlayers()
        return gameRegistrationViewName
    }

    @PostMapping("register/game/")
    fun registerGameAction(
            @Valid gameRegistrationForm: GameRegistrationForm,
            bindingResult: BindingResult,
            modelMap: ModelMap
    ): String {
        modelMap.fillMapWithPlayers()
        when {
            bindingResult.hasErrors() -> return gameRegistrationViewName
            else -> {
                val tournamentId = gameRegistrationForm.tournamentId!!
                val whitePlayerId = gameRegistrationForm.whitePlayerId!!
                val blackPlayerId = gameRegistrationForm.blackPlayerId!!
                val result = gameRegistrationForm.result!!
                if (whitePlayerId == blackPlayerId) {
                    modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.samePlayer"))
                    return gameRegistrationViewName
                } else {
                    val (whitePlayer, blackPlayer, tournament) = playerService.get(whitePlayerId)
                            .thenCombine(playerService.get(blackPlayerId)) { w, b -> w to b }
                            .thenCombine(tournamentService.getById(tournamentId)) { p, t -> Triple(p.first, p.second, t) }
                            .get()
                    val gameResult = when (result) {
                        "white" -> WhiteWon()
                        "black" -> BlackWon()
                        "draw" -> Draw()
                        else -> null
                    }
                    when {
                        gameResult == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unknownGameResult"))
                        whitePlayer == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unregisteredWhitePlayer"))
                        blackPlayer == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unregisteredBlackPlayer"))
                        tournament == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unregisteredTournament"))
                        else -> {
                            gameService.registerGame(Game(tournament.id!!, tournament.name, GamePlayer(whitePlayer), GamePlayer(blackPlayer), gameResult))
                            modelMap.addAttribute("success", localizedMessageSource.getMessage("staff.register.game.success"))
                        }
                    }
                }
                return gameRegistrationViewName
            }
        }
    }
}