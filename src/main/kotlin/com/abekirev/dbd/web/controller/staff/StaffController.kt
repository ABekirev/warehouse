package com.abekirev.dbd.web.controller.staff

import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.GameResult.BlackWon
import com.abekirev.dbd.entity.GameResult.Draw
import com.abekirev.dbd.entity.GameResult.WhiteWon
import com.abekirev.dbd.entity.Player
import com.abekirev.dbd.entity.otherPlayer
import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.web.controller.LocalizedMessageSource
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
                                             private val localizedMessageSource: LocalizedMessageSource) {
    @GetMapping("register/player/")
    fun registerPlayer(): String {
        return "home"
    }

    private val gameRegistrationViewName = "staff/registerGame"

    class Player(val id: String, val firstName: String, val secondName: String)

    fun ModelMap.fillMapWithPlayers() {
        put("players", playerService.getAll().map { Player(it.id, it.firstName, it.secondName) })
    }

    @GetMapping("register/game/")
    fun registerGame(gameRegistrationForm: GameRegistrationForm,
                     modelMap: ModelMap): String {
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
                val whitePlayerId = gameRegistrationForm.whitePlayerId!!
                val blackPlayerId = gameRegistrationForm.blackPlayerId!!
                val result = gameRegistrationForm.result!!
                when (whitePlayerId) {
                    blackPlayerId -> {
                        modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.samePlayer"))
                        return gameRegistrationViewName
                    }
                    else -> {
                        val whitePlayer = playerService.get(whitePlayerId)
                        val blackPlayer = playerService.get(blackPlayerId)
                        val gameResult = when (result) {
                            "white" -> WhiteWon()
                            "black" -> BlackWon()
                            "draw" -> Draw()
                            else -> {
                                throw IllegalStateException("Result is unknown: $result")
                            }
                        }
                        when {
                            whitePlayer == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unregisteredWhitePlayer"))
                            blackPlayer == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unregisteredBlackPlayer"))
                            else -> {
                                val game = Game(whitePlayer, blackPlayer, gameResult)
                                playerService.update(Player(
                                        whitePlayer.id,
                                        whitePlayer.firstName,
                                        whitePlayer.secondName,
                                        whitePlayer.games.filter { game ->
                                            whitePlayer.otherPlayer(game).id != blackPlayer.id
                                        }.plus(game)
                                ))
                                playerService.update(Player(
                                        blackPlayer.id,
                                        blackPlayer.firstName,
                                        blackPlayer.secondName,
                                        blackPlayer.games.filter { game ->
                                            blackPlayer.otherPlayer(game).id != whitePlayer.id
                                        }.plus(game)
                                ))
                                modelMap.addAttribute("success", localizedMessageSource.getMessage("staff.register.game.success"))
                            }
                        }
                    }
                }
                return gameRegistrationViewName
            }
        }
    }
}