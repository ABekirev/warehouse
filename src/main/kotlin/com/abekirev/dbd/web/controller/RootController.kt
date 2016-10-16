package com.abekirev.dbd.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/")
class RootController {
    @RequestMapping("/")
    fun root(): ModelAndView {
        return ModelAndView(REDIRECT + "/home/")
    }

    @RequestMapping("home/", method = arrayOf(RequestMethod.GET))
    fun home(): String {
        return "home"
    }
}
