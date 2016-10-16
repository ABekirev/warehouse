package com.abekirev.dbd.schema

import kotlinx.nosql.*
import kotlinx.nosql.mongodb.DocumentSchema

object Users : DocumentSchema<UserDto>("users", UserDto::class) {
    val _id = string("_id")
    val credentialsNonExpired = boolean("credentialsNonExpired")
    val accountNonExpired = boolean("accountNonExpired")
    val accountNonLocked = boolean("accountNonLocked")
    val authoritiesCollection = listOfString("authoritiesCollection")
    val enabled = boolean("enabled")
    val pass = string("pass")
}

data class UserDto(val _id: String,
                   val credentialsNonExpired: Boolean,
                   val accountNonExpired: Boolean,
                   val accountNonLocked: Boolean,
                   val authoritiesCollection: List<String>,
                   val enabled: Boolean,
                   val pass: String)