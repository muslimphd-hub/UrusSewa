package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance")
data class Maintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val propertyId: Long,
    val title: String,
    val category: String, // "Aircond", "Paip & Saliran", "Elektrik", "Kunci & Pintu", "Struktur", "Lain-lain"
    val cost: Double = 0.0,
    val dateReported: String, // YYYY-MM-DD
    val status: String = "MENUNGGU", // "MENUNGGU", "DALAM_PROSES", "SELESAI"
    val contractorContact: String = "",
    val notes: String = ""
)
