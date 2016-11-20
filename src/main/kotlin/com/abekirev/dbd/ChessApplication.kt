package com.abekirev.dbd

import com.abekirev.dbd.dal.DalConfiguration
import com.abekirev.dbd.dal.dao.GameDao
import com.abekirev.dbd.dal.dao.PlayerDao
import com.abekirev.dbd.dal.dao.TournamentDao
import com.abekirev.dbd.dal.dao.UserDao
import com.abekirev.dbd.service.GameService
import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.service.TournamentService
import com.abekirev.dbd.service.UserService
import com.abekirev.dbd.web.WebConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

fun main(args: Array<String>) {
    SpringApplication.run(ChessApplication::class.java, *args)
}

@SpringBootApplication
@Import(
        DalConfiguration::class,
        WebConfiguration::class
)
open class ChessApplication {
    @Bean open fun kotlinPropertyConfigurer(): PropertySourcesPlaceholderConfigurer {
        val propertyConfigurer = PropertySourcesPlaceholderConfigurer()
        propertyConfigurer.setPlaceholderPrefix("&{")
        propertyConfigurer.setIgnoreUnresolvablePlaceholders(true)
        return propertyConfigurer
    }

    @Bean open fun defaultPropertyConfigurer(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }

    @Bean open fun userService(userDao: UserDao): UserService {
        return UserService(userDao)
    }

    @Bean open fun playerService(playerDao: PlayerDao): PlayerService {
        return PlayerService(playerDao)
    }

    @Bean open fun tournamentService(tournamentDao: TournamentDao): TournamentService {
        return TournamentService(tournamentDao)
    }

    @Bean open fun gameService(gameDao: GameDao, tournamentDao: TournamentDao, playerDao: PlayerDao): GameService {
        return GameService(gameDao, tournamentDao, playerDao)
    }
}