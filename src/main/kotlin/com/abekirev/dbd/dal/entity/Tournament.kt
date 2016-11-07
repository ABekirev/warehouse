package com.abekirev.dbd.dal.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "tournaments")
data class TournamentDto(
        var id: String?,
        var name: String?,
        var dateFrom: LocalDate?,
        var dateTo: LocalDate?,
        var players: Collection<TournamentPlayerDto>?,
        var schedule: Collection<TournamentScheduleDto>?,
        var games: Collection<TournamentGameDto>?,
        var winner: TournamentPlayerDto?
) {
    internal constructor() : this(null, null, null, null, null, null, null)
    constructor(
            name: String?,
            dateFrom: LocalDate?,
            dateTo: LocalDate?,
            players: Collection<TournamentPlayerDto>?,
            schedule: Collection<TournamentScheduleDto>?,
            games: Collection<TournamentGameDto>?,
            winner: TournamentPlayerDto?
    ) : this(null, name, dateFrom, dateTo, players, schedule, games, winner)
}

data class TournamentPlayerDto(var id: String?,
                               var firstName: String?,
                               var lastName: String?) {
    internal constructor() : this(null, null, null)
}

data class TournamentScheduleDto(var turn: Int?,
                                 var whitePlayer: TournamentPlayerDto?,
                                 var blackPlayer: TournamentPlayerDto?) {
    internal constructor() : this(null, null, null)
}

data class TournamentGameDto(var id: String?,
                             var whitePlayer: TournamentPlayerDto?,
                             var blackPlayer: TournamentPlayerDto?,
                             var result: String?) {
    internal constructor() : this(null, null, null, null)
}
