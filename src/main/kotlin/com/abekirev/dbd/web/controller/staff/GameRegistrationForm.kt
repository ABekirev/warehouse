package com.abekirev.dbd.web.controller.staff

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE

class GameRegistrationForm {
    @NotNull
    var whitePlayerId: String? = null

    @NotNull
    var blackPlayerId: String? = null

    @NotNull
    @Pattern(regexp = "white|black|draw", flags = arrayOf(CASE_INSENSITIVE))
    var result: String? = null
}