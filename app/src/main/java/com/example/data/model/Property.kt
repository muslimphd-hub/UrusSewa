package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val unitNumber: String,
    val type: String, // "Kondominium", "Rumah Teres", "Apartmen", "Studio", "Kedai"
    val address: String,
    val monthlyRent: Double,
    val depositAmount: Double,
    val bankLoanPayment: Double = 0.0,
    val isOccupied: Boolean = false,
    val googleMapsUrl: String = "",
    val notes: String = ""
)
