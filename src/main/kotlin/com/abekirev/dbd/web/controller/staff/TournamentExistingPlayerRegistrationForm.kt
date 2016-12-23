package com.abekirev.dbd.web.controller.staff

import javax.validation.constraints.NotNull

class TournamentExistingPlayerRegistrationForm(
        @NotNull
        var playerId: String? = null
)
