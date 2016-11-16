package com.abekirev.dbd.dal.repository

import com.abekirev.dbd.dal.entity.UserDto
import org.springframework.stereotype.Repository
import java.util.concurrent.CompletableFuture

@Repository
interface UserRepository : StreamAndAsyncCrudRepository<UserDto, String>{
    fun findByName(name: String): CompletableFuture<UserDto?>
}