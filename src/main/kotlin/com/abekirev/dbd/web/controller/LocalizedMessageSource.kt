package com.abekirev.dbd.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Scope
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
open class LocalizedMessageSource @Autowired constructor(private val messageSource: MessageSource) {
    fun getMessage(code: String): String {
        return getMessage(code, emptyArray())
    }

    fun getMessage(code: String, args: Array<Any>): String {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale())
    }
}
