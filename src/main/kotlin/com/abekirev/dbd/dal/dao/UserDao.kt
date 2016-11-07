package com.abekirev.dbd.dal.dao

import com.abekirev.dbd.dal.entity.UserDto
import com.abekirev.dbd.dal.repository.UserRepository
import com.abekirev.dbd.entity.Authority
import com.abekirev.dbd.entity.User

class UserDao(private val userRepository: UserRepository) {
    fun getByName(name: String): User? {
        return userRepository.findByName(name).map(::userDtoToUser).orElse(null)
    }

    fun create(user: User): String {
        return userRepository.save(userToUserDto(user)).id!!
    }

    fun update(user: User) {
        userRepository.save(userToUserDto(user))
    }

    fun delete(id: String) {
        return userRepository.delete(id)
    }
}

internal fun userDtoToUser(user: UserDto): User {
    return User(
            user.id!!,
            user.name!!,
            user.credentialsNonExpired ?: false,
            user.accountNonExpired ?: false,
            user.accountNonLocked ?: false,
            user.authoritiesCollection?.map{ Authority.invoke(it) ?: throw IllegalArgumentException() } ?: emptySet(),
            user.enabled ?: false,
            user.pass!!
    )
}

internal fun userToUserDto(user: User): UserDto {
    return UserDto(
            user.id,
            user.name,
            user.credentialsNonExpired,
            user.accountNonExpired,
            user.accountNonLocked,
            user.authoritiesCollection.map{ it.authority },
            user.enabled,
            user.pass
    )
}
