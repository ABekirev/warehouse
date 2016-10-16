package com.abekirev.dbd.service

import com.abekirev.dbd.dao.PlayerDao
import com.abekirev.dbd.entity.Player

class PlayerService(private val playerDao: PlayerDao) {
    fun getAll(): Collection<Player> {
        return playerDao.getAll()
    }
}