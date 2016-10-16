package com.abekirev.dbd.security

import com.abekirev.dbd.entity.Authority
import com.abekirev.dbd.entity.User
import org.springframework.context.annotation.Scope
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext

fun Authentication.anonymous(): Boolean {
    return principal == AnonymousPrincipal
}

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
open class AuthService {
    val auth: Authentication?
        get() = SecurityContextHolder.getContext().authentication

    val anonymous: Boolean
        get() = with(auth) {
            if (this != null) anonymous()
            else true
        }

    val user: User?
        get() = with(auth) {
            if (this != null) principal as User
            else null
        }

    val admin: Boolean
        get() = !anonymous && user?.authorities?.contains(Authority.ROLE_ADMIN) ?: false

    val staff: Boolean
        get() = !anonymous && user?.authorities?.contains(Authority.ROLE_STAFF) ?: false

    val player: Boolean
        get() = !anonymous && user?.authorities?.contains(Authority.ROLE_PLAYER) ?: false
}