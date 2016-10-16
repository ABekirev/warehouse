package com.abekirev.dbd.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

val ERROR_PATH = "/error"
private val ERROR_VIEW_PATH = "error"

@Controller
open class HTTPErrorHandler {
//    @RequestMapping(value="/error/400")
//    fun error400() = ERROR_PATH.path("400")

    @RequestMapping(value="/error/404")
    fun error404() = ERROR_VIEW_PATH.path("404")

//    @RequestMapping(value="/error/500")
//    fun error500() = ERROR_PATH.path("500")
}
