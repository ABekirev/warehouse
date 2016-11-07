package com.abekirev.dbd.dal.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "games")
data class GameDto private constructor(
        @Id
        var id: String?,
        var tournamentId: String?,
        var tournamentName: String?,
        var whitePlayer: GamePlayerDto?,
        var blackPlayer: GamePlayerDto?,
        var result: String?
) {
    internal constructor() : this(null, null, null, null, null)
    constructor(
            tournamentId: String?,
            tournamentName: String?,
            whitePlayer: GamePlayerDto?,
            blackPlayer: GamePlayerDto?,
            result: String?
    ) : this(null, tournamentId, tournamentName, whitePlayer, blackPlayer, result)
}

data class GamePlayerDto(var id: String?,
                         var firstName: String?,
                         var lastName: String?) {
    internal constructor() : this(null, null, null)
}
