package com.example.basic_location.ui.main.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MeteocoolLocation(
    @PrimaryKey val uid: Int,
    val longitude: Double,
    val latitude: Double,
    val altitude: Double
)
