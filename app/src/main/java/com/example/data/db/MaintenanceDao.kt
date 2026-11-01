package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Maintenance
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance ORDER BY dateReported DESC")
    fun getAllMaintenance(): Flow<List<Maintenance>>

    @Query("SELECT * FROM maintenance WHERE propertyId = :propertyId ORDER BY dateReported DESC")
    fun getMaintenanceByProperty(propertyId: Long): Flow<List<Maintenance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(item: Maintenance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenances(items: List<Maintenance>)

    @Update
    suspend fun updateMaintenance(item: Maintenance)

    @Delete
    suspend fun deleteMaintenance(item: Maintenance)

    @Query("DELETE FROM maintenance")
    suspend fun clearAll()
}
