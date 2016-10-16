package com.abekirev.dbd.security

import com.abekirev.dbd.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class ChessUserDetailsService(private val userService: UserService) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        return username?.let {
            userService.getByName(username) ?: throw UsernameNotFoundException("No users found with username: $username")
        } ?: throw IllegalAccessException("Username must not be null")
    }
}
