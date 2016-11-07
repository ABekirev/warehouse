package com.abekirev.dbd.dal.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "players")
open class PlayerDto(
        @Id
        var id: String?,
        var firstName: String?,
        var lastName: String?,
        var games: Collection<PlayerGameDto>?
) {
    internal constructor() : this(null, null, emptySet())
    constructor(firstName: String?, lastName: String?, games: Collection<PlayerGameDto>?) : this(null, firstName, lastName, games)
}

data class PlayerGameDto(
        var id: String?,
        var tournamentId: String?,
        var tournamentName: String?,
        var side: String?,
        var opponent: OpponentDto?,
        var result: String?
) {
    internal constructor() : this(null, null, null, null, null, null)
}

data class OpponentDto(
        var id: String?,
        var firstName: String?,
        var lastName: String?
) {
    internal constructor() : this(null, null, null)
}

class PlayerProjectionDto(
        id: String?,
        firstName: String?,
        lastName: String?
) : PlayerDto(id,firstName, lastName, emptySet()){
    internal constructor() : this(null, null, null)
}