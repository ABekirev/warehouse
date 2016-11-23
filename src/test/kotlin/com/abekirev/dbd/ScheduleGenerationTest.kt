package com.abekirev.dbd

import com.abekirev.dbd.entity.SchedulePos
import com.abekirev.dbd.entity.genSchedule
import org.junit.Assert
import org.junit.Test

private fun <T> Collection<T>.identical(collection: Collection<T>): Boolean {
    return this.containsAll(collection) && collection.containsAll(this)
}

class ScheduleGenerationTest {
    @Test
    fun genForNoPlayers() {
        Assert.assertTrue(emptySet<SchedulePos>().identical(genSchedule(0)))
    }

    @Test
    fun genForOnePlayer() {
        Assert.assertTrue(emptySet<SchedulePos>().identical(genSchedule(1)))
    }

    @Test
    fun genForTwoPlayer() {
        Assert.assertTrue(setOf(SchedulePos(1,1,0,1)).identical(genSchedule(2)))
    }

    @Test
    fun genForThreePlayer() {
        Assert.assertTrue(
                setOf(
                        SchedulePos(1,1,0,2),
                        SchedulePos(1,2,)
                ).identical(genSchedule(3)))
    }
}