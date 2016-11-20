package com.abekirev.dbd.web.controller.staff

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class TournamentPlayerRegistrationForm(
        @NotNull
        @Min(1)
        var firstName: String? = null,

        @NotNull
        @Min(1)
        var lastName: String? = null
)