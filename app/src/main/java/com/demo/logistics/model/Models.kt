package com.demo.logistics.model

import java.time.LocalDate
import java.time.LocalTime

data class UserDemo(
    val username: String,
    val pin: String
)

data class TimeSlice(
    val jobName: String,
    val costCenterName: String,
    val startTime: LocalTime,
    val endTime: LocalTime
)

data class WorkDay(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val breakMinutes: Int,
    val slices: List<TimeSlice>
)
