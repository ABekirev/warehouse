package com.abekirev.dbd.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/")
class RootController {
    @GetMapping("/")
    fun root(): ModelAndView {
        return ModelAndView(REDIRECT + "/home/")
    }

    @GetMapping("home/")
    fun home(): String {
        return "home"
    }
}
