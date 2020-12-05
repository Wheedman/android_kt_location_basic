package com.example.basic_location.ui.main.database

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MeteocoolLocationDao {

    @Insert
    fun insertLocation(location: MeteocoolLocation)

    @Update
    fun updateLocation(location: MeteocoolLocation)

    @Query("SELECT * FROM MeteocoolLocation LIMIT 1")
    fun getLastLocation() : Flow<MeteocoolLocation>

    @Query("SELECT EXISTS(SELECT * FROM MeteocoolLocation)")
    fun isExists() : Boolean

}