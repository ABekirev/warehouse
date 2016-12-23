package com.abekirev.dbd.web.controller.staff

import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.GamePlayer
import com.abekirev.dbd.entity.GameResult.BlackWon
import com.abekirev.dbd.entity.GameResult.Draw
import com.abekirev.dbd.entity.GameResult.WhiteWon
import com.abekirev.dbd.entity.Player
import com.abekirev.dbd.entity.PlayerGame
import com.abekirev.dbd.entity.Tournament
import com.abekirev.dbd.entity.TournamentGame
import com.abekirev.dbd.entity.TournamentPlayer
import com.abekirev.dbd.service.GameService
//import com.abekirev.dbd.entity.otherPlayer
import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.service.TournamentService
import com.abekirev.dbd.web.LocalizedMessageSource
import com.abekirev.dbd.web.controller.tournamentViewName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.validation.Valid

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/staff/")
class StaffController @Autowired constructor(private val playerService: PlayerService,
                                             private val gameService: GameService,
                                             private val tournamentService: TournamentService,
                                             private val localizedMessageSource: LocalizedMessageSource) {

    private val playerRegistrationViewName = "staff/registerPlayer"
    private val gameRegistrationViewName = "tournament/registerGame"

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

    fun ModelMap.fillMapWithPlayers(tournament: Tournament) {
        put("players", tournament.players.map { Player(it.id, it.firstName, it.lastName) })
    }

    @GetMapping("tournament/registerGame/", params = arrayOf("id"))
    fun gameForTournamentRegistration(
            modelMap: ModelMap,
            gameRegistrationForm: GameRegistrationForm,
            id: String
    ): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            modelMap.fillMapWithPlayers(tournament)
            modelMap.addAttribute("tournament", tournament)
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return gameRegistrationViewName
    }

    @PostMapping("tournament/registerGame/", params = arrayOf("id"))
    fun registerGameForTournament(
            modelMap: ModelMap,
            @Valid gameRegistrationForm: GameRegistrationForm,
            bindingResult: BindingResult,
            id: String
    ): String {
        val tournament = tournamentService.get(id).get()!!
        modelMap.fillMapWithPlayers(tournament)
        modelMap.addAttribute("tournament", tournament)
        when {
            bindingResult.hasErrors() -> return gameRegistrationViewName
            else -> {
                val whitePlayerId = gameRegistrationForm.whitePlayerId!!
                val blackPlayerId = gameRegistrationForm.blackPlayerId!!
                val result = gameRegistrationForm.result!!
                if (whitePlayerId == blackPlayerId) {
                    modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.samePlayer"))
                    return gameRegistrationViewName
                } else {
                    val (whitePlayer, blackPlayer) = playerService.get(whitePlayerId)
                            .thenCombine(playerService.get(blackPlayerId)) { w, b -> w to b }
                            .get()
                    val gameResult = when (result) {
                        "white" -> WhiteWon
                        "black" -> BlackWon
                        "draw" -> Draw
                        else -> null
                    }
                    when {
                        gameResult == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unknownGameResult"))
                        whitePlayer == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unregisteredWhitePlayer"))
                        blackPlayer == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unregisteredBlackPlayer"))
                        tournament == null -> modelMap.addAttribute("error", localizedMessageSource.getMessage("staff.register.game.unregisteredTournament"))
                        else -> {
                            gameService.registerGame(Game(tournament.id!!, tournament.name, GamePlayer(whitePlayer), GamePlayer(blackPlayer), gameResult)).join()
                            modelMap.addAttribute("tournament", tournamentService.get(id).get()!!)
                            modelMap.addAttribute("success", localizedMessageSource.getMessage("staff.register.game.success"))
                        }
                    }
                }
                return gameRegistrationViewName
            }
        }
    }

    @GetMapping("tournament/create")
    fun tournamentCreation(
            @Valid tournamentCreationForm: TournamentCreationForm
    ): String {
        return "tournament/creation"
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private fun CharSequence.tryParseDate(): LocalDate? {
        return try {
            LocalDate.parse(this, dateFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    private val tournamentCreationViewName = "tournament/creation"

    @PostMapping("tournament/create")
    fun createTournament(
            @Valid tournamentCreationForm: TournamentCreationForm,
            bindingResult: BindingResult,
            modelMap: ModelMap
    ): Any {
        when {
            bindingResult.hasErrors() -> tournamentCreationViewName
            else -> {
                val name = tournamentCreationForm.name
                when (name) {
                    null -> modelMap.addAttribute("error", "Name is null")
                    else -> {
                        val dateFrom: LocalDate? = tournamentCreationForm.dateFrom?.tryParseDate()
                        when (dateFrom) {
                            null -> modelMap.addAttribute("error", "Date from is invalid")
                            else -> {
                                val dateTo: LocalDate? = tournamentCreationForm.dateTo?.tryParseDate()
                                when (dateTo) {
                                    null -> modelMap.addAttribute("error", "Date to is invalid")
                                    else -> {
                                        val tournament = tournamentService.create(Tournament(name, dateFrom, dateTo)).get()
                                        modelMap.addAttribute("tournament", tournament)
                                        return ModelAndView(tournamentViewName, modelMap)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return tournamentCreationViewName
    }

    private val registerExistingPlayerViewName = "tournament/registerExistingPlayer"

    @GetMapping("tournament/registerExistingPlayer/", params = arrayOf("id"))
    fun existingPlayerForTournamentRegistration(modelMap: ModelMap,
                                                tournamentExistingPlayerRegistrationForm: TournamentExistingPlayerRegistrationForm,
                                                id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            modelMap.addAttribute("tournament", tournament)
            modelMap.addAttribute("players", playerService.getAllProjectionsWithIdNotInCollection(tournament.players.map(TournamentPlayer::id)))
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return registerExistingPlayerViewName
    }

    @PostMapping("tournament/registerExistingPlayer/", params = arrayOf("id"))
    fun registerExistingPlayerForTournament(modelMap: ModelMap,
                                            tournamentExistingPlayerRegistrationForm: TournamentExistingPlayerRegistrationForm,
                                            id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            val id = tournamentExistingPlayerRegistrationForm.playerId
            when (id) {
                null -> modelMap.addAttribute("error", "Id is null")
                else -> {
                    val updatedTournament = playerService.get(id)
                            .thenApply { player ->
                                tournament.addPlayer(TournamentPlayer(player!!))
                            }
                            .thenCompose { tournament ->
                                tournamentService.update(tournament)
                            }.get()
                    modelMap.addAttribute("tournament", updatedTournament)
                    val players = playerService.getAllProjectionsWithIdNotInCollection(tournament.players.map(TournamentPlayer::id))
                    modelMap.addAttribute("players", players)
                    tournamentExistingPlayerRegistrationForm.playerId = players.map(Player::id).firstOrNull()
                    modelMap.addAttribute("success", "Success")
                }
            }
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return registerExistingPlayerViewName
    }

    private val registerPlayerViewName = "tournament/registerPlayer"

    @GetMapping("tournament/registerPlayer/", params = arrayOf("id"))
    fun playerForTournamentRegistration(modelMap: ModelMap,
                                        tournamentPlayerRegistrationForm: TournamentPlayerRegistrationForm,
                                        id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            modelMap.addAttribute("tournament", tournament)
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return registerPlayerViewName
    }

    @PostMapping("tournament/registerPlayer/", params = arrayOf("id"))
    fun registerPlayerForTournament(modelMap: ModelMap,
                                    tournamentPlayerRegistrationForm: TournamentPlayerRegistrationForm,
                                    id: String): String {
        val tournament = tournamentService.get(id).get()
        if (tournament != null) {
            val firstName = tournamentPlayerRegistrationForm.firstName
            val lastName = tournamentPlayerRegistrationForm.lastName
            when (firstName) {
                null -> modelMap.addAttribute("error", "First name is null")
                else -> {
                    when (lastName) {
                        null -> modelMap.addAttribute("error", "Last name is null")
                        else -> {
                            val updatedTournament = playerService.create(Player(firstName, lastName))
                                    .thenApply { player ->
                                        tournament.addPlayer(TournamentPlayer(player))
                                    }
                                    .thenCompose { tournament ->
                                        tournamentService.update(tournament)
                                    }.get()
                            modelMap.addAttribute("tournament", updatedTournament)
                            tournamentPlayerRegistrationForm.firstName = null
                            tournamentPlayerRegistrationForm.lastName = null
                            modelMap.addAttribute("success", "Success")
                        }
                    }
                }
            }
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return registerPlayerViewName
    }
}