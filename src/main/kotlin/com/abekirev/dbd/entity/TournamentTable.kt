package com.abekirev.dbd.entity

data class TournamentTable(val playersCount: Int, val turnRows: Map<Turn, Map<Table, TurnRowGame>>) {
    fun genSchedule(players: Collection<TournamentPlayer>): List<Schedule> {
        val playerArr = players.toTypedArray()
        return turnRows.entries.flatMap { turnEntry ->
            val turn = turnEntry.key
            turnEntry.value
                    .filter { gameEntry ->
                        !(gameEntry.value.white.index == playerArr.size || gameEntry.value.black.index == playerArr.size)
                    }
                    .map { gameEntry ->
                        val table = gameEntry.key
                        Schedule(turn.number, table.number, playerArr[gameEntry.value.white.index], playerArr[gameEntry.value.black.index])
                    }
        }
    }
}

data class TurnRowGame(val white: TurnRowGamePlayer, val black: TurnRowGamePlayer)
data class TurnRowGamePlayer(val index: Int)
data class Turn(val number: Int)
data class Table(val number: Int)
