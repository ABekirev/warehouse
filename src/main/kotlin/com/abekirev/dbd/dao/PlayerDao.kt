package com.abekirev.dbd.dao

import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.GameResult
import com.abekirev.dbd.entity.Player
import com.abekirev.dbd.entity.PlayerGameResult
import com.abekirev.dbd.entity.gameResult
import com.abekirev.dbd.entity.isWhite
import com.abekirev.dbd.entity.otherPlayer
import com.abekirev.dbd.schema.GameDto
import com.abekirev.dbd.schema.OpponentDto
import com.abekirev.dbd.schema.PlayerDto
import com.abekirev.dbd.schema.Players
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import kotlinx.nosql.update

class PlayerDao(private val db: MongoDB) {
    fun getAll(): Collection<Player> {
        return db.withSession {
            Players.find()
                    .map { playerDtoToPlayer(it) }
        }
    }

    fun get(id: String): Player? {
        return db.withSession {
            Players.find { Players._id.equal(id) }.singleOrNull()?.let {
                playerDtoToPlayer(it)
            }
        }
    }

    fun update(player: Player) {
        val playerDto = playerToPlayerDto(player)
        db.withSession {
            Players.find({ Players._id.equal(playerDto._id) })
                    .projection { _id + firstName + lastName + games }
                    .update(playerDto._id, playerDto.firstName, playerDto.lastName, playerDto.games)
        }
    }

    fun create(player: Player) {
        val playerDto = playerToPlayerDto(player)
        db.withSession {
            Players.insert(playerDto)
        }
    }

    private fun playerDtoToPlayer(playerDto: PlayerDto): Player {
        return Player(
                playerDto._id,
                playerDto.firstName,
                playerDto.lastName,
                playerDto.games.map { gameDtoToGame(playerDto, it) }
        )
    }

    private sealed class Side(val dbValue: String) {
        class White : Side("white")
        class Black : Side("black")
    }


    private sealed class Result(val dbValue: String) {
        class Won : Result("won")
        class Lost : Result("lost")
        class Draw : Result("draw")
    }

    private fun gameDtoToGame(playerDto: PlayerDto, gameDto: GameDto): Game {
        val player = playerdDtoToPlayerWithEmptyGames(playerDto)
        val opponent = opponentDtoToPlayer(gameDto.opponent)
        val side = when (gameDto.side) {
            Side.White().dbValue -> Side.White()
            Side.Black().dbValue -> Side.Black()
            else -> throw IllegalArgumentException("Unknown side: ${gameDto.side}")
        }
        val result = when (gameDto.result) {
            Result.Won().dbValue -> Result.Won()
            Result.Lost().dbValue -> Result.Lost()
            Result.Draw().dbValue -> Result.Draw()
            else -> throw IllegalArgumentException("Unknown result: ${gameDto.result}")
        }
        return Game(
                when (side) {
                    is Side.White -> player
                    is Side.Black -> opponent
                },
                when (side) {
                    is Side.White -> opponent
                    is Side.Black -> player
                },
                when (result) {
                    is Result.Won -> when (side) {
                        is Side.White -> GameResult.WhiteWon()
                        is Side.Black -> GameResult.BlackWon()
                    }
                    is Result.Lost -> when (side) {
                        is Side.White -> GameResult.BlackWon()
                        is Side.Black -> GameResult.WhiteWon()
                    }
                    is Result.Draw -> GameResult.Draw()
                }
        )
    }

    private fun playerdDtoToPlayerWithEmptyGames(playerDto: PlayerDto): Player {
        return Player(
                playerDto._id,
                playerDto.firstName,
                playerDto.lastName,
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

    private fun playerToPlayerDto(player: Player): PlayerDto {
        return PlayerDto(
                player.id,
                player.firstName,
                player.lastName,
                player.games.map { gameToGameDto(player, it) }
        )
    }

    private fun gameToGameDto(player: Player, game: Game): GameDto {
        return GameDto(
                if (player.isWhite(game)) Side.White().dbValue else Side.Black().dbValue,
                playerToOpponentDto(player.otherPlayer(game)),
                when (player.gameResult(game)) {
                    is PlayerGameResult.Won -> Result.Won().dbValue
                    is PlayerGameResult.Lost -> Result.Lost().dbValue
                    is PlayerGameResult.Draw -> Result.Draw().dbValue
                }
        )
    }

    private fun playerToOpponentDto(player: Player): OpponentDto {
        return OpponentDto(
                player.id,
                player.firstName,
                player.lastName
        )
    }
}
