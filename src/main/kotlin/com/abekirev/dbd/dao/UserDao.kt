package com.abekirev.dbd.dao

import com.abekirev.dbd.entity.Authority
import com.abekirev.dbd.entity.User
import com.abekirev.dbd.schema.Users
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB

class UserDao(private val db: MongoDB) {
    fun getByName(name: String): User? {
        return db.withSession {
            Users.find { Users._id.equal(name) }.singleOrNull()?.let {
                User(
                        it._id,
                        it.credentialsNonExpired,
                        it.accountNonExpired,
                        it.accountNonLocked,
                        it.authoritiesCollection.map{ Authority(it) ?: throw IllegalArgumentException("Unknown authority: $it") },
                        it.enabled,
                        it.pass
                )
            }
        }
    }
}