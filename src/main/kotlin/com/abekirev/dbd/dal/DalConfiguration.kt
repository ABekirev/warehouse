package com.abekirev.dbd.dal

import com.abekirev.dbd.dal.dao.GameDao
import com.abekirev.dbd.dal.dao.PlayerDao
import com.abekirev.dbd.dal.dao.TournamentDao
import com.abekirev.dbd.dal.dao.UserDao
import com.abekirev.dbd.dal.repository.GameRepository
import com.abekirev.dbd.dal.repository.PlayerProjectionRepository
import com.abekirev.dbd.dal.repository.PlayerRepository
import com.abekirev.dbd.dal.repository.TournamentRepository
import com.abekirev.dbd.dal.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories
open class DalConfiguration {

    @Bean open fun userDao(userRepository: UserRepository): UserDao {
        return UserDao(userRepository)
    }

    @Bean open fun playerDao(playerRepository: PlayerRepository,
                             playerProjectionRepository: PlayerProjectionRepository): PlayerDao {
        return PlayerDao(playerRepository, playerProjectionRepository)
    }

    @Bean open fun tournamentDao(tournamentRepository: TournamentRepository): TournamentDao {
        return TournamentDao(tournamentRepository)
    }

    @Bean open fun gameDao(gameRepository: GameRepository): GameDao {
        return GameDao(gameRepository)
    }

    @Bean open fun defaultMongoConverter(mongoDbFactory: MongoDbFactory): MongoConverter {
        return MappingMongoConverter(
                DefaultDbRefResolver(mongoDbFactory),
                MongoMappingContext()
        ).apply {
            typeMapper = DefaultMongoTypeMapper(null)
        }
    }

    @Bean open fun mongoTemplate(
            mongoDbFactory: MongoDbFactory,
            defaultMongoConverter: MongoConverter
    ): MongoTemplate {
        return MongoTemplate(mongoDbFactory, defaultMongoConverter)
    }
}