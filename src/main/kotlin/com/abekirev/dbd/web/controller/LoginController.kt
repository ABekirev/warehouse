package com.abekirev.dbd.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
open class LoginController {
    @RequestMapping("/login")
    fun login() = "login"

    @RequestMapping("/logout")
    fun logout() = "logout"
}