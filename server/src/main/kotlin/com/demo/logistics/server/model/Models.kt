package com.demo.logistics.server.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
enum class Role { OPERATOR, MANAGER, ADMIN }

@Serializable
data class User(
    val id: String,
    val username: String,
    val pin: String,
    val fullName: String,
    val role: Role
)

@Serializable
data class Project(
    val id: String,
    val code: String,
    val name: String
)

@Serializable
data class CostCenter(
    val id: String,
    val name: String,
    val location: String
)

@Serializable
data class TimeSlice(
    val id: String,
    val workShiftId: String,
    val projectId: String,
    val costCenterId: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val notes: String? = null
)

@Serializable
enum class ShiftStatus { DRAFT, CONFIRMED, APPROVED }

@Serializable
data class WorkShift(
    val id: String,
    val userId: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val breakMinutes: Int,
    val slices: List<TimeSlice>,
    val status: ShiftStatus,
    val anomalies: List<String> = emptyList(),
    val approverId: String? = null,
    val lastUpdatedBy: String? = null
)

@Serializable
data class ChangeLog(
    val id: String,
    val timestamp: Instant,
    val actorId: String,
    val action: String,
    val targetId: String,
    val description: String
)

@Serializable
data class AppConfig(
    val id: String = "default",
    val dailyAlertHour: Int = 17,
    val alertRecipients: List<String> = emptyList()
)

// DTOs

@Serializable
data class LoginRequest(val username: String, val pin: String)

@Serializable
data class LoginResponse(val user: User)

@Serializable
data class TimeSliceInput(
    val projectId: String,
    val costCenterId: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val notes: String? = null
)

@Serializable
data class ShiftSubmission(
    val userId: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val breakMinutes: Int,
    val slices: List<TimeSliceInput>,
    val submitAsConfirmed: Boolean = false
)

@Serializable
data class DashboardSummary(
    val pendingApproval: List<WorkShift>,
    val anomalies: List<WorkShift>,
    val recent: List<WorkShift>
)

@Serializable
data class OptionsResponse(
    val projects: List<Project>,
    val costCenters: List<CostCenter>,
    val config: AppConfig
)

@Serializable
data class WeekHistoryResponse(
    val days: List<WorkShift>
)
