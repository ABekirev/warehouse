package com.abekirev.dbd.dal.repository

import com.abekirev.dbd.dal.entity.UserDto
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<UserDto, String>{
    fun findByName(name: String): Optional<UserDto>
}