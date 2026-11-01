package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Tenant
import kotlinx.coroutines.flow.Flow

@Dao
interface TenantDao {
    @Query("SELECT * FROM tenants ORDER BY name ASC")
    fun getAllTenants(): Flow<List<Tenant>>

    @Query("SELECT * FROM tenants WHERE id = :id LIMIT 1")
    suspend fun getTenantById(id: Long): Tenant?

    @Query("SELECT * FROM tenants WHERE propertyId = :propertyId LIMIT 1")
    suspend fun getTenantByPropertyId(propertyId: Long): Tenant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenant(tenant: Tenant): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenants(tenants: List<Tenant>)

    @Update
    suspend fun updateTenant(tenant: Tenant)

    @Delete
    suspend fun deleteTenant(tenant: Tenant)

    @Query("DELETE FROM tenants")
    suspend fun clearAll()
}
