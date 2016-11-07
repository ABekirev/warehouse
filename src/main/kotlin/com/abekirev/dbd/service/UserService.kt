package com.abekirev.dbd.service

import com.abekirev.dbd.dal.dao.UserDao
import com.abekirev.dbd.entity.User

class UserService(private val userDao: UserDao) {
    fun getByName(name: String): User? {
        return userDao.getByName(name)
    }

    fun create(user: User): String {
        return userDao.create(user)
    }

    fun update(user: User) {
        return userDao.update(user)
    }

    fun delete(id: String) {
        return userDao.delete(id)
    }
}