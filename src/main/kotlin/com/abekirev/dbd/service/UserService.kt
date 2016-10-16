package com.abekirev.dbd.service

import com.abekirev.dbd.dao.UserDao
import com.abekirev.dbd.entity.User

class UserService(private val userDao: UserDao) {
    fun getByName(name: String): User? {
        return userDao.getByName(name)
    }
}