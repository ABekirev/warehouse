package com.abekirev.dbd.service

import com.abekirev.dbd.dal.dao.UserDao
import com.abekirev.dbd.entity.User
import java.util.concurrent.CompletableFuture

class UserService(private val userDao: UserDao) {
    fun getByName(name: String): CompletableFuture<User?> {
        return userDao.getByName(name)
    }

    fun create(user: User): CompletableFuture<User> {
        return userDao.create(user)
    }

    fun update(user: User): CompletableFuture<User> {
        return userDao.update(user)
    }

    fun delete(id: String): CompletableFuture<Void> {
        return userDao.delete(id)
    }
}