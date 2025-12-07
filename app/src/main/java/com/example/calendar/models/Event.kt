package com.example.calendar.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: Date,
    val startTime: String,
    val endTime: String,
    val location: String? = null,
    val description: String? = null,
    val color: String? = null
)
