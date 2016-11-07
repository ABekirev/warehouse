package com.abekirev.dbd.service

import com.abekirev.dbd.dal.dao.PlayerDao
import com.abekirev.dbd.entity.Player
import java.util.*

class PlayerService(private val playerDao: PlayerDao) {
    fun getAll(): Collection<Player> {
        return playerDao.getAll()
    }

    fun getAllProjections(): Collection<Player> {
        return playerDao.getAllProjections()
    }

    fun get(id: String): Player? {
        return playerDao.get(id)
    }

    fun create(player: Player): String {
        return playerDao.create(player)
    }

    fun update(player: Player) {
        return playerDao.update(player)
    }

    fun delete(id: String) {
        return playerDao.delete(id)
    }
}