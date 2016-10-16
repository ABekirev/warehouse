package com.abekirev.dbd.web.controller

import org.springframework.boot.autoconfigure.web.DefaultErrorViewResolver
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@Component
class ChessErrorViewResolver(
        applicationContext: ApplicationContext?,
        resourceProperties: ResourceProperties?) : DefaultErrorViewResolver(applicationContext, resourceProperties) {
    override fun resolveErrorView(request: HttpServletRequest?, status: HttpStatus?, model: MutableMap<String, Any>?): ModelAndView {
        if (status != null) {
            return when (status) {
                HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR -> ModelAndView(REDIRECT + ERROR_PATH.path(status.value().toString()))
                else -> super.resolveErrorView(request, status, model)
            }
        }
        throw IllegalArgumentException("Status must not be null")
    }
}
