package com.abekirev.dbd.dal.dao

import com.abekirev.dbd.dal.entity.UserDto
import com.abekirev.dbd.dal.repository.UserRepository
import com.abekirev.dbd.entity.Authority
import com.abekirev.dbd.entity.User

class UserDao(private val userRepository: UserRepository) {
    fun getByName(name: String) = userRepository.findByName(name)
            .thenApplyAsync { it?.let(::userDtoToUser) }

    fun getAll() = userRepository.findAll().map(::userDtoToUser)

    fun create(user: User) = userRepository.save(userToUserDto(user))
            .thenApplyAsync(::userDtoToUser)

    fun update(user: User) = userRepository.save(userToUserDto(user))
            .thenApplyAsync(::userDtoToUser)

    fun delete(id: String) = userRepository.delete(id)
}

internal fun userDtoToUser(user: UserDto): User {
    return User(
            user.id!!,
            user.name!!,
            user.credentialsNonExpired ?: false,
            user.accountNonExpired ?: false,
            user.accountNonLocked ?: false,
            user.authoritiesCollection?.map { Authority.invoke(it) ?: throw IllegalArgumentException() } ?: emptySet(),
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
            user.authoritiesCollection.map { it.authority },
            user.enabled,
            user.pass
    )
}
