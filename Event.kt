package com.example.calendar.model

data class Event(
    val id: String,
    val title: String,
    val date: String, // ISO format: "yyyy-MM-dd"
    val startTime: String, // Format: "HH:mm AM/PM"
    val endTime: String, // Format: "HH:mm AM/PM"
    val location: String? = null,
    val description: String? = null
)
