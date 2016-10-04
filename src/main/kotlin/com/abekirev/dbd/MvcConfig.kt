package com.abekirev.dbd

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@EnableWebMvc
@Configuration
open class MvcConfig : WebMvcConfigurerAdapter() {
    override fun addViewControllers(registry: ViewControllerRegistry?) {
        if (registry != null) {
            registry.addViewController("/home").setViewName("home")
            registry.addViewController("/").setViewName("home")
            registry.addViewController("/hello").setViewName("hello")
            registry.addViewController("/login").setViewName("login")
        } else {
            throw IllegalArgumentException("registry must be not null.")
        }
    }
}
