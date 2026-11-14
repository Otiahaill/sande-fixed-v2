package com.example.sande.data.repository

import androidx.room.Transaction
import com.example.sande.data.db.AppDatabase
import com.example.sande.data.daos.MovementDao
import com.example.sande.data.entities.Movement
import com.example.sande.data.entities.MovementType
import com.example.sande.data.entities.SaleHeader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID


class MovementRepository constructor(
    private val database: AppDatabase,
    private val productRepository: ProductRepository,
    private val saleHeaderRepository: SaleHeaderRepository,
    private val outboxRepository: OutboxRepository
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

    /**
     * Atomically records a complete sale in one Room transaction:
     * 1. Creates a SaleHeader with receipt number
     * 2. Inserts all SALE movements with shared referenceId
     * 3. Creates an Outbox entry for sync
     */
    @Transaction
    suspend fun recordSaleAtomic(
        items: List<com.example.sande.ui.viewmodel.SalesViewModel.CartItem>,
        receiptNumber: String,
        deviceId: String
    ): String {
        val totalCents = items.sumOf { it.quantity * it.unitPriceCents }
        val saleHeaderId = UUID.randomUUID().toString()
        val saleHeader = SaleHeader(
            id = saleHeaderId,
            receiptNumber = receiptNumber,
            totalCents = totalCents,
            deviceId = deviceId,
            synced = false
        )
        saleHeaderRepository.insert(saleHeader)

        val referenceId = saleHeaderId
        items.forEach { line ->
            dao.insert(
                Movement(
                    id = UUID.randomUUID().toString(),
                    productId = line.productId,
                    type = MovementType.SALE,
                    quantity = line.quantity,
                    unitPriceCents = line.unitPriceCents,
                    referenceId = referenceId,
                    deviceId = deviceId,
                    synced = false
                )
            )
        }

        // Create outbox entry for sync
        val outboxPayload = """
            {
                "type": "SALE",
                "saleHeaderId": "$saleHeaderId",
                "receiptNumber": "$receiptNumber",
                "totalCents": $totalCents,
                "deviceId": "$deviceId",
                "items": ${items.map { """{"productId":"${it.productId}","quantity":${it.quantity},"unitPriceCents":${it.unitPriceCents}}""" }.joinToString(",")}
            }
        """.trimIndent()
        outboxRepository.insert("SALE", saleHeaderId, "CREATE", outboxPayload)

        return saleHeaderId
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
