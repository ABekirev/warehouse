package com.abekirev.dbd.dal.dao

import com.abekirev.dbd.dal.entity.TournamentDto
import com.abekirev.dbd.dal.entity.TournamentGameDto
import com.abekirev.dbd.dal.entity.TournamentPlayerDto
import com.abekirev.dbd.dal.entity.TournamentScheduleDto
import com.abekirev.dbd.dal.repository.TournamentRepository
import com.abekirev.dbd.entity.GameResult.BlackWon
import com.abekirev.dbd.entity.GameResult.Draw
import com.abekirev.dbd.entity.GameResult.WhiteWon
import com.abekirev.dbd.entity.Schedule
import com.abekirev.dbd.entity.Tournament
import com.abekirev.dbd.entity.TournamentGame
import com.abekirev.dbd.entity.TournamentPlayer
import java.util.stream.Stream

class TournamentDao(private val tournamentRepository: TournamentRepository) {
    fun getAll(): Stream<Tournament> = tournamentRepository.findAll()
            .map(::tournamentDtoToTournament)

    fun get(id: String) = tournamentRepository.findOne(id)
            .thenApplyAsync { it?.let(::tournamentDtoToTournament) }

    fun create(tournament: Tournament) = tournamentRepository.save(tournamentToTournamentDto(tournament))
            .thenApplyAsync(::tournamentDtoToTournament)

    fun update(tournament: Tournament) = tournamentRepository.save(tournamentToTournamentDto(tournament))
            .thenApplyAsync(::tournamentDtoToTournament)

    fun delete(id: String) = tournamentRepository.delete(id)
}

internal fun tournamentDtoToTournament(tournament: TournamentDto): Tournament {
    return Tournament(
            tournament.id!!,
            tournament.name!!,
            tournament.dateFrom!!,
            tournament.dateTo!!,
            tournament.players?.map(::tournamentPlayerDtoToTournamentPlayer) ?: throw IllegalArgumentException(),
            tournament.schedule?.map(::tournamentScheduleDtoToSchedule) ?: throw IllegalArgumentException(),
            tournament.games?.map(::tournamentGameDtoToTournamentGame) ?: throw IllegalArgumentException(),
            tournament.winner?.let(::tournamentPlayerDtoToTournamentPlayer)
    )
}

internal fun tournamentPlayerDtoToTournamentPlayer(player: TournamentPlayerDto): TournamentPlayer {
    return TournamentPlayer(
            player.id!!,
            player.firstName!!,
            player.lastName!!
    )
}

internal fun tournamentScheduleDtoToSchedule(schedule: TournamentScheduleDto): Schedule {
    return Schedule(
            schedule.turn!!,
            schedule.whitePlayer?.let(::tournamentPlayerDtoToTournamentPlayer) ?: throw IllegalArgumentException(),
            schedule.blackPlayer?.let(::tournamentPlayerDtoToTournamentPlayer) ?: throw IllegalArgumentException()
    )
}

internal fun tournamentGameDtoToTournamentGame(game: TournamentGameDto): TournamentGame {
    return TournamentGame(
            game.id!!,
            game.whitePlayer?.let(::tournamentPlayerDtoToTournamentPlayer) ?: throw IllegalArgumentException(),
            game.blackPlayer?.let(::tournamentPlayerDtoToTournamentPlayer) ?: throw IllegalArgumentException(),
            game.result?.let { result ->
                when (result) {
                    GameResult.WhiteWon().dbValue -> WhiteWon()
                    GameResult.BlackWon().dbValue -> BlackWon()
                    GameResult.Draw().dbValue -> Draw()
                    else -> throw IllegalAccessException()
                }
            } ?: throw IllegalArgumentException()
    )
}


internal fun tournamentToTournamentDto(tournament: Tournament): TournamentDto {
    return TournamentDto(
            tournament.id,
            tournament.name,
            tournament.dateFrom,
            tournament.dateTo,
            tournament.players.map(::tournamentPlayerToTournamentPlayerDto),
            tournament.schedule.map(::scheduleToTournamentScheduleDto),
            tournament.games.map(::tournamentGameToTournamentGameDto),
            tournament.winner?.let(::tournamentPlayerToTournamentPlayerDto)
    )
}

internal fun tournamentPlayerToTournamentPlayerDto(player: TournamentPlayer): TournamentPlayerDto {
    return TournamentPlayerDto(
            player.id,
            player.firstName,
            player.lastName
    )
}

internal fun scheduleToTournamentScheduleDto(schedule: Schedule): TournamentScheduleDto {
    return TournamentScheduleDto(
            schedule.turn,
            schedule.whitePlayer.let(::tournamentPlayerToTournamentPlayerDto),
            schedule.blackPlayer.let(::tournamentPlayerToTournamentPlayerDto)
    )
}

internal fun tournamentGameToTournamentGameDto(game: TournamentGame): TournamentGameDto {
    return TournamentGameDto(
            game.id,
            game.whitePlayer.let(::tournamentPlayerToTournamentPlayerDto),
            game.blackPlayer.let(::tournamentPlayerToTournamentPlayerDto),
            when (game.result) {
                is WhiteWon -> GameResult.WhiteWon().dbValue
                is BlackWon -> GameResult.BlackWon().dbValue
                is Draw -> GameResult.Draw().dbValue
            }
    )
}
