package com.abekirev.dbd.service

import com.abekirev.dbd.dal.dao.GameDao
import com.abekirev.dbd.dal.dao.PlayerDao
import com.abekirev.dbd.dal.dao.TournamentDao
import com.abekirev.dbd.entity.Game
import com.abekirev.dbd.entity.GamePlayer
import com.abekirev.dbd.entity.PlayerGame
import com.abekirev.dbd.entity.TournamentGame
import java.util.concurrent.CompletableFuture

class GameService(
        private val gameDao: GameDao,
        private val tournamentDao: TournamentDao,
        private val playerDao: PlayerDao
) {
    fun registerGame(game: Game) = gameDao.create(game)
            .thenComposeAsync { savedGame ->
                CompletableFuture.allOf(
                        registerGameForTournament(savedGame),
                        registerGameForPlayer(game, game.whitePlayer),
                        registerGameForPlayer(game, game.blackPlayer)
                ).thenApply { ignore -> savedGame }
            }

    private fun registerGameForTournament(game: Game) = tournamentDao.get(game.tournamentId)
            .thenApplyAsync { tournament ->
                if (tournament != null) {
                    tournament.addGame(TournamentGame(game)).let { tournament ->
                        tournamentDao.update(tournament)
                    }
                }
            }

    private fun registerGameForPlayer(game: Game, player: GamePlayer) = playerDao.get(player.id)
            .thenApplyAsync { player ->
                if (player != null) {
                    player.addGame(PlayerGame(player, game)).let { player ->
                        playerDao.update(player)
                    }
                }
            }
}
