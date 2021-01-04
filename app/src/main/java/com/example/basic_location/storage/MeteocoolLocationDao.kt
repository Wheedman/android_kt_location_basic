package com.example.basic_location.storage

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MeteocoolLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: MeteocoolLocation)

    @Update
    fun updateLocation(location: MeteocoolLocation)

    @Query("SELECT * FROM MeteocoolLocation LIMIT 1")
    fun getLastLocation() : Flow<MeteocoolLocation>

    @Query("SELECT EXISTS(SELECT * FROM MeteocoolLocation)")
    fun isExists() : Boolean

}