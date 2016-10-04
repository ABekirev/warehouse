package com.abekirev.dbd.service

import com.abekirev.dbd.entity.User
import com.abekirev.dbd.schema.Users
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class MyUserDetailsService constructor(private val db: MongoDB?) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        return if (db != null) db.withSession {
            Users.find { this.username.equal(username) }.singleOrNull()
        }
        else if (username.equals("admin"))
            User(
                    0,
                    "Admin",
                    true,
                    true,
                    true,
                    emptyList(),
                    true,
                    "password"
            )
        else null
    }
}
