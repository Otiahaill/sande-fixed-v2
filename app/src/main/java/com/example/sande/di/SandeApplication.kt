package com.example.sande.di

import android.app.Application
import com.example.sande.data.db.AppDatabase
import com.example.sande.data.repository.AuthRepository
import com.example.sande.data.repository.MemberRepository
import com.example.sande.data.repository.MovementRepository
import com.example.sande.data.repository.OutboxRepository
import com.example.sande.data.repository.ProductRepository

class SandeApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val productRepository: ProductRepository by lazy {
        ProductRepository(database)
    }
    val movementRepository: MovementRepository by lazy {
        MovementRepository(database, productRepository)
    }
    val outboxRepository: OutboxRepository by lazy {
        OutboxRepository(database)
    }
    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }
    val memberRepository: MemberRepository by lazy {
        MemberRepository(database)
    }
}
