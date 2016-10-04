package com.abekirev.dbd.schema

import com.abekirev.dbd.entity.Authority
import kotlinx.nosql.long
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Authorities : DocumentSchema<Authority>("authorities", Authority::class) {
    val id = long("id")
    val name = string("name")
}
