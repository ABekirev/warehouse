package com.abekirev.dbd.service

import com.abekirev.dbd.dao.PlayerDao
import com.abekirev.dbd.entity.Player
import java.util.*

class PlayerService(private val playerDao: PlayerDao) {
    fun getAll(): Collection<Player> {
        return playerDao.getAll()
    }

    fun get(id: String): Player? {
        return playerDao.get(id)
    }

    fun update(player: Player) {
        return playerDao.update(player)
    }

    fun create(firstName: String, lastName: String) {
        return playerDao.create(Player(UUID.randomUUID().let { it.mostSignificantBits.toHex().toString() + it.leastSignificantBits.toHex().toString() }, firstName, lastName, emptySet()))
    }
}

private fun Long.toHex(): String {
    return java.lang.Long.toHexString(this)
}