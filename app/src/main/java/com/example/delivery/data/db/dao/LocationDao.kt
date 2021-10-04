package com.example.delivery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.delivery.data.entity.LocationLatLngEntity

@Dao
interface LocationDao {

    @Query("SELECT * FROM LocationLatLngEntity WHERE id=:id")
    suspend fun getUserLocationLatLngEntity(id: Long) : LocationLatLngEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocationLatLngEntity(locationLatLngEntity: LocationLatLngEntity)

    @Query("DELETE FROM LocationLatLngEntity WHERE id=:id")
    suspend fun deleteUserInfo(id: Long)

}