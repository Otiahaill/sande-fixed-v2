package com.example.sande.data.repository

import com.example.sande.data.db.AppDatabase
import com.example.sande.data.daos.MovementDao
import com.example.sande.data.entities.Movement
import com.example.sande.data.entities.MovementType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID


class MovementRepository constructor(
    private val database: AppDatabase,
    private val productRepository: ProductRepository
) {
    private val dao: MovementDao = database.movementDao()

    val getAll: Flow<List<Movement>> = dao.getAll()

    fun getByProduct(productId: String): Flow<List<Movement>> = dao.getByProduct(productId)

    suspend fun getStockBalance(productId: String): Long = dao.getStockBalance(productId)

    fun getUnsynced(): Flow<List<Movement>> = dao.getUnsynced()

    suspend fun recordSale(productId: String, quantity: Long, unitPriceCents: Long?, referenceId: String?, deviceId: String): String {
        val id = UUID.randomUUID().toString()
        val movement = Movement(
            id = id,
            productId = productId,
            type = MovementType.SALE,
            quantity = quantity,
            unitPriceCents = unitPriceCents,
            referenceId = referenceId,
            deviceId = deviceId,
            synced = false
        )
        dao.insert(movement)
        return id
    }

    suspend fun recordReceive(productId: String, quantity: Long, unitPriceCents: Long?, referenceId: String?, deviceId: String): String {
        val id = UUID.randomUUID().toString()
        val movement = Movement(
            id = id,
            productId = productId,
            type = MovementType.RECEIVE,
            quantity = quantity,
            unitPriceCents = unitPriceCents,
            referenceId = referenceId,
            deviceId = deviceId,
            synced = false
        )
        dao.insert(movement)
        return id
    }

    suspend fun recordAdjustment(productId: String, quantity: Long, referenceId: String?, deviceId: String): String {
        val id = UUID.randomUUID().toString()
        val movement = Movement(
            id = id,
            productId = productId,
            type = MovementType.ADJUST,
            quantity = quantity,
            unitPriceCents = null,
            referenceId = referenceId,
            deviceId = deviceId,
            synced = false
        )
        dao.insert(movement)
        return id
    }

    suspend fun recordVoid(originalMovementId: String, productId: String, quantity: Long, referenceId: String?, deviceId: String): String {
        val id = UUID.randomUUID().toString()
        val movement = Movement(
            id = id,
            productId = productId,
            type = MovementType.VOID,
            quantity = quantity,
            unitPriceCents = null,
            referenceId = referenceId,
            deviceId = deviceId,
            synced = false
        )
        dao.insert(movement)
        return id
    }

    suspend fun markSynced(ids: List<String>) = dao.markSynced(ids)
}
