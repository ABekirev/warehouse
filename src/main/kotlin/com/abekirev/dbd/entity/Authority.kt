package com.abekirev.dbd.entity

import org.springframework.security.core.GrantedAuthority

class Authority(val name: String) : GrantedAuthority {
    override fun getAuthority(): String = name
}
