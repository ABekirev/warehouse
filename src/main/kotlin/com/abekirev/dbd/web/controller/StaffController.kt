package com.abekirev.dbd.web.controller

import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.GameResult
import com.abekirev.dbd.entity.Player
import com.abekirev.dbd.entity.otherPlayer
import com.abekirev.dbd.service.PlayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Component
@RequestMapping("/staff/")
class StaffController @Autowired constructor(private val playerService: PlayerService) {
    @GetMapping("register/player/")
    fun registerPlayer(): String {
        return "home"
    }

    private val registerGameViewName = "staff/registerGame"

    class Player (val id: String, val firstName: String, val secondName: String)

    fun ModelMap.fillMapWithPlayers() {
        put("players", playerService.getAll().map { Player(it.id, it.firstName, it.secondName) })
    }

    @GetMapping("register/game/")
    fun registerGame(modelMap: ModelMap): String {
        modelMap.fillMapWithPlayers()
        return registerGameViewName
    }

    @PostMapping("register/game/")
    fun registerGameAction(
            @RequestParam("whitePlayerId") whitePlayerId: String?,
            @RequestParam("blackPlayerId") blackPlayerId: String?,
            @RequestParam("result") result: String?,
            modelMap: ModelMap
    ): String {
        modelMap.fillMapWithPlayers()
        if (whitePlayerId == null || blackPlayerId == null || result == null) {
            modelMap.addAttribute("error", "Все поля должны быть заполнены")
            return registerGameViewName
        } else if (whitePlayerId == blackPlayerId) {
            modelMap.addAttribute("error", "Нельзя добавить результат встречи игрока с самим собой")
            return registerGameViewName
        } else {
            val whitePlayer = playerService.get(whitePlayerId)
            val blackPlayer = playerService.get(blackPlayerId)
            val gameResult = when (result) {
                "white" -> GameResult.WhiteWon()
                "black" -> GameResult.BlackWon()
                "draw" -> GameResult.Draw()
                else -> {
                    modelMap.addAttribute("error", "Неивестнпе значение результата матча")
                    return registerGameViewName
                }
            }
            if (whitePlayer == null)
                modelMap.addAttribute("error", "Игрок, играющий за белых, не зарегистрирован в системе")
            else if (blackPlayer == null)
                modelMap.addAttribute("error", "Игрок, играющий за черных, не зарегистрирован в системе")
            else {
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
                modelMap.addAttribute("success", true)
            }
        }
        return registerGameViewName
    }
}