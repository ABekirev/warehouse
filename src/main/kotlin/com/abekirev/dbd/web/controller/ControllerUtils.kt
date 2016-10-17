package com.abekirev.dbd.web.controller

val REDIRECT = "redirect:"
fun String.path(path: String): String = this + "/" + path
fun String.resourcePath(path: String): String = this + "\\" + path