package com.abekirev.dbd.service

import com.abekirev.dbd.dal.dao.PlayerDao
import com.abekirev.dbd.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

class PlayerService(private val playerDao: PlayerDao) {
    fun getAll(): Stream<Player> {
        return playerDao.getAll()
    }

    fun getAllProjections(): Stream<Player> {
        return playerDao.getAllProjections()
    }

    fun getAllProjectionsWithIdNotInCollection(playerIds: Collection<String>): List<Player> {
        return playerDao.getAllProjectionsWithIdNotInCollection(playerIds)
    }

    fun get(id: String): CompletableFuture<Player?> {
        return playerDao.get(id)
    }

    fun create(player: Player): CompletableFuture<Player>{
        return playerDao.create(player)
    }

    fun update(player: Player): CompletableFuture<Player> {
        return playerDao.update(player)
    }

    fun delete(id: String): CompletableFuture<Void> {
        return playerDao.delete(id)
    }
}