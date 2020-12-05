package com.example.basic_location.ui.main.database

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeteocoolLocationDao {

    @Insert
    fun insertLocation(location: MeteocoolLocation)

    @Query("SELECT * FROM MeteocoolLocation ORDER BY timestamp DESC LIMIT 1")
    fun getLastLocation() : Flow<MeteocoolLocation>

}