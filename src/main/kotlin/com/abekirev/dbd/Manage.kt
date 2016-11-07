package com.abekirev.dbd

//import com.abekirev.dbd.dal.dao.PlayerDao
//import com.abekirev.dbd.dal.dao.TournamentDao
//import com.abekirev.dbd.dal.dao.UserDao
//import com.abekirev.dbd.entity.Authority
//import com.abekirev.dbd.entity.Game
//import com.abekirev.dbd.entity.Player
//import com.abekirev.dbd.entity.Schedule
//import com.abekirev.dbd.entity.Tournament
//import com.abekirev.dbd.entity.User
//import com.abekirev.dbd.schema.Players
//import com.abekirev.dbd.schema.Tournaments
//import com.abekirev.dbd.schema.Users
//import com.mongodb.ServerAddress
//import kotlinx.nosql.mongodb.DocumentSchema
//import kotlinx.nosql.mongodb.MongoDB
//import java.time.LocalDateTime
//
//fun main(args: Array<String>) {
//    val host = "127.0.0.1"
//    val port = 27017
//    val name = "test"
//    val db = MongoDB(
//            seeds = arrayOf(ServerAddress(host, port)),
//            database = name,
//            schemas = arrayOf(Users, Tournaments, Players)
//    )
//    createSchema(db, Users, Tournaments, Players)
//    val userDao = UserDao(db)
//    val playerDao = PlayerDao(db)
//    val tournamentDao = TournamentDao(db)
//    createAdminUser(userDao, "admin", "password")
//    createPlayers(playerDao)
//    val players = playerDao.getAll().toList()
//    val player1 = players[0]
//    val player2 = players[1]
//    createTournament(tournamentDao, players, listOf(Schedule(1, player1, player2)), emptySet())
//}
//
//private fun createSchema(db: MongoDB, vararg documents: DocumentSchema<*>) {
//    db.withSession {
//        documents.forEach {
//            it.drop()
//            it.create()
//        }
//    }
//}
//
//private fun createAdminUser(userDao: UserDao, name: String, password: String) {
//    userDao.create(User(
//            null,
//            name,
//            true,
//            true,
//            true,
//            listOf(Authority("ROLE_ADMIN")!!),
//            true,
//            password
//    ))
//}
//
//private fun createPlayers(playerDao: PlayerDao) {
//    playerDao.create(
//            Player(
//                    null,
//                    "firstName_1",
//                    "lastName_1",
//                    emptySet()
//            )
//    )
//    playerDao.create(
//            Player(
//                    null,
//                    "firstName_2",
//                    "lastName_2",
//                    emptySet()
//            )
//    )
//}
//
//private fun createTournament(tournamentDao: TournamentDao, players: Collection<Player>, schedule: Collection<Schedule>, games: Collection<Game>) {
//    tournamentDao.create(
//            Tournament(
//                    null,
//                    "tournament_1",
//                    LocalDateTime.now(),
//                    LocalDateTime.now().plusDays(10),
//                    players,
//                    schedule,
//                    games
//            )
//    )
//}
