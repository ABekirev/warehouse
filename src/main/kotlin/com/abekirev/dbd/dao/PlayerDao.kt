package com.abekirev.dbd.dao

import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.GameResult.BlackWon
import com.abekirev.dbd.entity.GameResult.Draw
import com.abekirev.dbd.entity.GameResult.WhiteWon
import com.abekirev.dbd.entity.Player
import com.abekirev.dbd.schema.GameDto
import com.abekirev.dbd.schema.OpponentDto
import com.abekirev.dbd.schema.PlayerDto
import com.abekirev.dbd.schema.Players
import kotlinx.nosql.mongodb.MongoDB

class PlayerDao(private val db: MongoDB) {
    fun getAll(): Collection<Player> {
        return db.withSession {
            Players.find()
                    .map { playerDtoToPlayer(it) }
        }
    }

    private fun playerDtoToPlayer(playerDto: PlayerDto): Player {
        return Player(
                playerDto._id,
                playerDto.firstName,
                playerDto.secondName,
                playerDto.games.map { gameDtoToGame(playerDto, it) }
        )
    }

    private fun gameDtoToGame(playerDto: PlayerDto, gameDto: GameDto): Game {
        return Game(
                when (gameDto.side) {
                    "white" -> playerdDtoToPlayerWithEmptyGames(playerDto)
                    "black" -> opponentDtoToPlayer(gameDto.opponent)
                    else -> throw IllegalArgumentException("Unknown side: ${gameDto.side}")
                },
                when (gameDto.side) {
                    "white" -> opponentDtoToPlayer(gameDto.opponent)
                    "black" -> playerdDtoToPlayerWithEmptyGames(playerDto)
                    else -> throw IllegalArgumentException("Unknown side: ${gameDto.side}")
                },
                when (gameDto.result) {
                    "won" -> WhiteWon()
                    "lost" -> BlackWon()
                    "draw" -> Draw()
                    else -> throw IllegalArgumentException("Unknown result: ${gameDto.result}")
                }
        )
    }

    private fun playerdDtoToPlayerWithEmptyGames(playerDto: PlayerDto): Player {
        return Player(
                playerDto._id,
                playerDto.firstName,
                playerDto.secondName,
                emptySet()
        )
    }

    private fun opponentDtoToPlayer(opponentDto: OpponentDto): Player {
        return Player(
                opponentDto._id,
                opponentDto.firstName,
                opponentDto.secondName,
                emptySet()
        )
    }
}
