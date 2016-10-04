package com.abekirev.dbd.schema

import com.abekirev.dbd.entity.User
import kotlinx.nosql.boolean
import kotlinx.nosql.listOfString
import kotlinx.nosql.long
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Users : DocumentSchema<User>("users", User::class) {
    val userId = long("id")
    val username = string("username")
    val credentialsNonExpired = boolean("credentialsNonExpired")
    val accountNonExpired = boolean("accountNonExpired")
    val accountNonLocked = boolean("accountNonLocked")
    val authoritiesCollection = listOfString("authoritiesCollection")
    val enabled = boolean("enabled")
    val password = string("password")
}
