package com.abekirev.dbd.web.controller

import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/players/")
class PlayersController @Autowired constructor(private val playerService: PlayerService) {
    @GetMapping("/")
    fun players(modelMap: ModelMap): String {
        modelMap.addAttribute(
                "players",
                playerService.getAllProjections().toList().sortedWith(Comparator { p1, p2 ->
                    (p1.firstName + p1.lastName).compareTo(p2.firstName + p2.lastName)
                })
        )
        return "players/list"
    }

    @GetMapping("/{id}")
    fun player(modelMap: ModelMap,
               @PathVariable("id") id: String): String {
        modelMap.addAttribute("player", playerService.get(id).get()!!)
        return "players/player"
    }
}
