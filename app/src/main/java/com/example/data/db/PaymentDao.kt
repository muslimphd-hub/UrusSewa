package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY dueDate DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE propertyId = :propertyId ORDER BY dueDate DESC")
    fun getPaymentsByProperty(propertyId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE tenantId = :tenantId ORDER BY dueDate DESC")
    fun getPaymentsByTenant(tenantId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE status = 'TERTUNGGAK' OR balanceDue > 0 ORDER BY dueDate ASC")
    fun getOverduePayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    suspend fun getPaymentById(id: Long): Payment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<Payment>)

    @Update
    suspend fun updatePayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)

    @Query("DELETE FROM payments")
    suspend fun clearAll()
}
