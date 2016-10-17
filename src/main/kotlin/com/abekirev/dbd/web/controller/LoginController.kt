package com.abekirev.dbd.web.controller

import com.abekirev.dbd.security.AuthService
import com.abekirev.dbd.security.anonymous
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.WebApplicationContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
open class LoginController @Autowired constructor(private val authService: AuthService) {
    @RequestMapping("/login")
    fun login() = "login"

    @RequestMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): String {
        with(authService.auth) {
            if (this != null && !anonymous())
                    SecurityContextLogoutHandler().logout(request, response, this)
        }
        return "home"
    }
}