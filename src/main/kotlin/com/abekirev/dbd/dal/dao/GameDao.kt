package com.abekirev.dbd.dal.dao

import com.abekirev.dbd.dal.entity.GameDto
import com.abekirev.dbd.dal.entity.GamePlayerDto
import com.abekirev.dbd.dal.repository.GameRepository
import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.GamePlayer

class GameDao(private val gameRepository: GameRepository) {
    fun get(id: String) = gameRepository.findOne(id)
            .thenApplyAsync { it?.let(::gameDtoToGame) }

    fun create(game: Game) = gameRepository.save(gameToGameDto(game))
            .thenApplyAsync(::gameDtoToGame)
}

internal sealed class GameResult(val dbValue: String) {
    object WhiteWon : GameResult("white")
    object BlackWon : GameResult("black")
    object Draw : GameResult("draw")
}

internal fun gameDtoToGame(game: GameDto): Game {
    return Game(
            game.id!!,
            game.tournamentId!!,
            game.tournamentName!!,
            game.whitePlayer?.let(::gamePlayerDtoToGamePlayer) ?: throw IllegalArgumentException(),
            game.blackPlayer?.let(::gamePlayerDtoToGamePlayer) ?: throw IllegalArgumentException(),
            game.result?.let { result ->
                when (result) {
                    GameResult.WhiteWon.dbValue -> com.abekirev.dbd.entity.GameResult.WhiteWon
                    GameResult.BlackWon.dbValue -> com.abekirev.dbd.entity.GameResult.BlackWon
                    GameResult.Draw.dbValue -> com.abekirev.dbd.entity.GameResult.Draw
                    else -> throw IllegalArgumentException()
                }
            } ?: throw IllegalArgumentException()
    )
}

internal fun gamePlayerDtoToGamePlayer(player: GamePlayerDto): GamePlayer {
    return GamePlayer(
            player.id!!,
            player.firstName!!,
            player.lastName!!
    )
}

internal fun gameToGameDto(game: Game): GameDto {
    return GameDto(
            game.id,
            game.tournamentId,
            game.tournamentName,
            gamePlayerToGamePlayerDto(game.whitePlayer),
            gamePlayerToGamePlayerDto(game.blackPlayer),
            when (game.result) {
                is com.abekirev.dbd.entity.GameResult.WhiteWon -> GameResult.WhiteWon.dbValue
                is com.abekirev.dbd.entity.GameResult.BlackWon -> GameResult.BlackWon.dbValue
                is com.abekirev.dbd.entity.GameResult.Draw -> GameResult.Draw.dbValue
            }
    )
}

internal fun gamePlayerToGamePlayerDto(player: GamePlayer): GamePlayerDto {
    return GamePlayerDto(
            player.id,
            player.firstName,
            player.lastName
    )
}
