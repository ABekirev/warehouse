package com.abekirev.dbd.dal.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserDto(
        @Id
        var id: String?,
        var name: String?,
        var credentialsNonExpired: Boolean?,
        var accountNonExpired: Boolean?,
        var accountNonLocked: Boolean?,
        var authoritiesCollection: Collection<String>?,
        var enabled: Boolean?,
        var pass: String?
) {
    internal constructor() : this(null, null, null, null, null, null, null)
    constructor(
            name: String?,
            credentialsNonExpired: Boolean?,
            accountNonExpired: Boolean?,
            accountNonLocked: Boolean?,
            authoritiesCollection: Collection<String>?,
            enabled: Boolean?,
            pass: String?
    ) : this(null, name, credentialsNonExpired, accountNonExpired, accountNonLocked, authoritiesCollection, enabled, pass)
}