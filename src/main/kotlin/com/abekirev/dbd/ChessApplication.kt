package com.abekirev.dbd

import com.abekirev.dbd.dao.PlayerDao
import com.abekirev.dbd.dao.UserDao
import com.abekirev.dbd.schema.Players
import com.abekirev.dbd.schema.Users
import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.service.UserService
import com.mongodb.ServerAddress
import kotlinx.nosql.mongodb.MongoDB
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

fun main(args: Array<String>) {
    SpringApplication.run(ChessApplication::class.java, *args)
}

@SpringBootApplication
open class ChessApplication {
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
    ): MongoDB = MongoDB(
            seeds = arrayOf(ServerAddress(host, port)),
            database = name,
            schemas = arrayOf(Users, Players)
    )

    @Bean open fun userDao(mongoDb: MongoDB): UserDao {
        return UserDao(mongoDb)
    }

    @Bean open fun userService(userDao: UserDao): UserService {
        return UserService(userDao)
    }

    @Bean open fun playerDao(mongoDb: MongoDB): PlayerDao {
        return PlayerDao(mongoDb)
    }

    @Bean open fun playerService(playerDao: PlayerDao): PlayerService {
        return PlayerService(playerDao)
    }
}