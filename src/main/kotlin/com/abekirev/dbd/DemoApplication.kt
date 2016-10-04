package com.abekirev.dbd

import com.abekirev.dbd.schema.Authorities
import com.abekirev.dbd.schema.Users
import com.abekirev.dbd.service.MyUserDetailsService
import com.mongodb.ServerAddress
import kotlinx.nosql.mongodb.MongoDB
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.security.core.userdetails.UserDetailsService

@SpringBootApplication
open class DemoApplication {
    @Bean open fun kotlinPropertyConfigurer() : PropertySourcesPlaceholderConfigurer {
        val propertyConfigurer = PropertySourcesPlaceholderConfigurer()
        propertyConfigurer.setPlaceholderPrefix("&{")
        propertyConfigurer.setIgnoreUnresolvablePlaceholders(true)
        return propertyConfigurer
    }

    @Bean open fun defaultPropertyConfigurer() : PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }

    @Bean open fun mongoDb(
            @Value("\${db.host}") host: String,
            @Value("\${db.port}") port: Int,
            @Value("\${db.name}") name: String
    ): MongoDB? = null
//    MongoDB(
//            seeds = arrayOf(ServerAddress(host, port)),
//            database = name,
//            schemas = arrayOf(Users, Authorities)
//    )

    @Bean open fun userDetailsService(mongoDb: MongoDB?): UserDetailsService {
        return MyUserDetailsService(mongoDb)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}