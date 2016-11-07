package com.abekirev.dbd.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class User(val id: String?,
                val name: String,
                val credentialsNonExpired: Boolean,
                val accountNonExpired: Boolean,
                val accountNonLocked: Boolean,
                val authoritiesCollection: Collection<Authority>,
                val enabled: Boolean,
                val pass: String) : UserDetails {
    override fun getUsername(): String = name

    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired

    override fun isAccountNonExpired(): Boolean = accountNonExpired

    override fun isAccountNonLocked(): Boolean = accountNonLocked

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authoritiesCollection.toMutableSet()

    override fun isEnabled(): Boolean = enabled

    override fun getPassword(): String = pass
}
