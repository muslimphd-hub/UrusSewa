package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tenants",
    foreignKeys = [
        ForeignKey(
            entity = Property::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["propertyId"])]
)
data class Tenant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val propertyId: Long? = null,
    val name: String,
    val phone: String, // e.g. "+60123456789"
    val email: String,
    val icNumber: String,
    val contractStartDate: String, // YYYY-MM-DD
    val contractEndDate: String,   // YYYY-MM-DD
    val monthlyRent: Double,
    val securityDeposit: Double = 0.0,
    val utilityDeposit: Double = 0.0,
    val dueDayOfMonth: Int = 1,     // e.g. 1st or 7th of month
    val emergencyContact: String = "",
    val photoUri: String = "",
    val isContractActive: Boolean = true,
    val notes: String = ""
)
