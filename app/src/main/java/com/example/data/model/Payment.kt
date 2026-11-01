package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val propertyId: Long,
    val tenantId: Long,
    val billingMonth: String, // e.g. "2026-10"
    val dueDate: String,      // YYYY-MM-DD
    val amountDue: Double,
    val amountPaid: Double = 0.0,
    val balanceDue: Double = amountDue - amountPaid,
    val status: String = "BELUM_BAYAR", // "LUNAS", "SEPARA", "TERTUNGGAK", "BELUM_BAYAR"
    val paymentDate: String? = null,
    val receiptNumber: String = "",
    val receiptImagePath: String? = null,
    val ocrReferenceNo: String? = null,
    val ocrBankName: String? = null,
    val cloudStorageEncryptedRef: String? = null,
    val notes: String = ""
)
