package com.abekirev.dbd.web.controller.staff

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class TournamentCreationForm(
        @NotNull
        @Min(1)
        var name: String? = null,

        @NotNull
        var dateFrom: String? = null,

        @NotNull
        var dateTo: String? = null
)