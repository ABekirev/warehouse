package com.abekirev.dbd.web.controller.staff

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class PlayerRegistrationForm {
    @NotNull
    @Size(min = 1)
    var firstName: String? = null

    @NotNull
    @Size(min = 1)
    var lastName: String? = null

    fun clear() {
        firstName = null
        lastName = null
    }
}
