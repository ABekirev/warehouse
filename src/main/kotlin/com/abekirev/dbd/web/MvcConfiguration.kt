package com.abekirev.dbd.web

import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.thymeleaf.dialect.IDialect

@Configuration
@EnableWebMvc
open class MvcConfiguration {

    private class WarehouseWebMvcConfigurer : WebMvcConfigurerAdapter() {
        override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
            if (registry != null)
                with(registry) {
                    addResourceHandler("/static/**").addResourceLocations("classpath:/static/")
                    addResourceHandler("/tiles/**").addResourceLocations("classpath:/tiles/")
                }
            else throw IllegalArgumentException("registry must be not null.")
        }
    }

    @Bean open fun webMvcConfigurer(): WebMvcConfigurer {
        return WarehouseWebMvcConfigurer()
    }

    @Bean open fun layoutDialect(): IDialect {
        return LayoutDialect()
    }
}
