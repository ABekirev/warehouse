package com.abekirev.dbd.dal.repository

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.io.Serializable
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

@NoRepositoryBean
interface StreamAndAsyncCrudRepository<T, ID : Serializable> : Repository<T, ID> {
    fun <S : T> save(entity: S): CompletableFuture<S>
    fun <S : T> save(entities: Iterable<S>): Stream<S>
    fun findOne(id: ID): CompletableFuture<T?>
    fun exists(id: ID): CompletableFuture<Boolean>
    fun findAll(): Stream<T>
    fun findAll(ids: Iterable<ID>): Stream<T>
    fun count(): CompletableFuture<Long>
    fun delete(id: ID): CompletableFuture<Void>
    fun delete(entity: T): CompletableFuture<Void>
    fun delete(entities: Iterable<T>): CompletableFuture<Void>
    fun deleteAll(): CompletableFuture<Void>
}
