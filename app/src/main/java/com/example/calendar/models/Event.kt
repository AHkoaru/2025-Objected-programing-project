package com.example.calendar.models

import java.util.Date

data class Event(
    val id: String,
    val title: String,
    val date: Date,
    val startTime: String,
    val endTime: String,
    val location: String? = null,
    val description: String? = null,
    val color: String? = null
)
