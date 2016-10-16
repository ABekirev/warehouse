package com.abekirev.dbd.security

import java.security.Principal

object AnonymousPrincipal : Principal {
    override fun getName(): String {
        return "anonymousUser"
    }
}
