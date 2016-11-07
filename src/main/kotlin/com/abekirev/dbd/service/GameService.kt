package com.abekirev.dbd.service

import com.abekirev.dbd.dal.dao.GameDao
import com.abekirev.dbd.dal.dao.PlayerDao
import com.abekirev.dbd.dal.dao.TournamentDao
import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.PlayerGame
import com.abekirev.dbd.entity.TournamentGame

class GameService(
        private val gameDao: GameDao,
        private val tournamentDao: TournamentDao,
        private val playerDao: PlayerDao
) {
    fun registerGame(game: Game) {
        val gameId = gameDao.create(game)
        val savedGame = game.withId(gameId)
        tournamentDao.get(game.tournamentId)?.addGame(TournamentGame(savedGame))?.let { tournament ->
            tournamentDao.update(tournament)
        }
        playerDao.get(game.whitePlayer.id)?.let { player ->
            player.addGame(PlayerGame(player, savedGame))
        }?.let { player ->
            playerDao.update(player)
        }
    }
}

private fun Game.withId(id: String): Game {
    return Game(id, tournamentId, tournamentName, whitePlayer, blackPlayer, result)
}
