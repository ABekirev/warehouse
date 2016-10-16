package com.abekirev.dbd.entity

import org.springframework.security.core.GrantedAuthority

enum class Authority : GrantedAuthority {
    ROLE_ADMIN,
    ROLE_STAFF,
    ROLE_PLAYER,
    ;

    companion object Factory {
        operator fun invoke(name: String): Authority? {
            return Authority.values()
                    .filter { it.name == name }
                    .singleOrNull()
        }
    }

    override fun getAuthority(): String = name
}
