package com.abekirev.dbd.security

import com.abekirev.dbd.entity.Authority
import com.abekirev.dbd.entity.User
import org.springframework.context.annotation.Scope
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
open class AuthService {
    private val auth: Authentication
        get() = SecurityContextHolder.getContext().authentication

    val anonymous: Boolean
        get() = auth.principal == AnonymousPrincipal

    val user: User
        get() = auth.principal as User

    val admin: Boolean
        get() = !anonymous && user.authorities.contains(Authority.ROLE_ADMIN)

    val staff: Boolean
        get() = !anonymous && user.authorities.contains(Authority.ROLE_STAFF)

    val player: Boolean
        get() = !anonymous && user.authorities.contains(Authority.ROLE_PLAYER)
}