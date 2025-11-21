package com.demo.logistics.server.repo

import com.demo.logistics.server.model.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Duration
import java.util.UUID

private const val DATA_FILE = "server/data/data.json"

@Serializable
private data class PersistedState(
    val users: List<User>,
    val projects: List<Project>,
    val costCenters: List<CostCenter>,
    val shifts: List<WorkShift>,
    val logs: List<ChangeLog>,
    val config: AppConfig
)

class DataStore {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private var state: PersistedState

    init {
        val file = File(DATA_FILE)
        state = if (file.exists()) {
            runCatching { json.decodeFromString<PersistedState>(file.readText()) }
                .getOrElse { seedDefaults(file) }
        } else {
            seedDefaults(file)
        }
    }

    private fun seedDefaults(file: File): PersistedState {
        file.parentFile?.mkdirs()
        val seeded = PersistedState(
            users = listOf(
                User("u-operator", "operatore1", "1111", "Operatore Demo", Role.OPERATOR),
                User("u-manager", "responsabile", "2222", "Responsabile Demo", Role.MANAGER),
                User("u-admin", "admin", "9999", "Admin Demo", Role.ADMIN)
            ),
            projects = listOf(
                Project("p-1", "COMM-A", "Commessa A"),
                Project("p-2", "COMM-B", "Commessa B"),
                Project("p-3", "COMM-C", "Commessa C")
            ),
            costCenters = listOf(
                CostCenter("c-1", "Magazzino", "Hub Nord"),
                CostCenter("c-2", "Ufficio", "Palazzina"),
                CostCenter("c-3", "Banchina", "Piano terra")
            ),
            shifts = emptyList(),
            logs = emptyList(),
            config = AppConfig(alertRecipients = listOf("responsabile@example.com"))
        )
        file.writeText(json.encodeToString(seeded))
        return seeded
    }

    fun authenticate(username: String, pin: String): User? =
        state.users.firstOrNull { it.username == username && it.pin == pin }

    fun options(): OptionsResponse = OptionsResponse(
        projects = state.projects,
        costCenters = state.costCenters,
        config = state.config
    )

    fun recentWeekFor(userId: String): List<WorkShift> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
        val weekStart = today.minus(DatePeriod(days = 6))
        return state.shifts.filter { it.userId == userId && it.date >= weekStart && it.date <= today }
            .sortedByDescending { it.date }
    }

    fun dashboard(): DashboardSummary {
        val pending = state.shifts.filter { it.status == ShiftStatus.CONFIRMED }
        val anomalies = state.shifts.filter { it.anomalies.isNotEmpty() }
        val recent = state.shifts.sortedByDescending { it.date }.take(10)
        return DashboardSummary(pending, anomalies, recent)
    }

    fun allLogs(): List<ChangeLog> = state.logs.sortedByDescending { it.timestamp }

    fun saveShift(submission: ShiftSubmission): WorkShift {
        val id = UUID.randomUUID().toString()
        val slices = submission.slices.map { slice ->
            TimeSlice(
                id = UUID.randomUUID().toString(),
                workShiftId = id,
                projectId = slice.projectId,
                costCenterId = slice.costCenterId,
                startTime = slice.startTime,
                endTime = slice.endTime,
                notes = slice.notes
            )
        }
        val anomalies = validateShift(submission, slices)
        val status = if (submission.submitAsConfirmed && anomalies.isEmpty()) ShiftStatus.CONFIRMED else ShiftStatus.DRAFT
        val workShift = WorkShift(
            id = id,
            userId = submission.userId,
            date = submission.date,
            startTime = submission.startTime,
            endTime = submission.endTime,
            breakMinutes = submission.breakMinutes,
            slices = slices,
            status = status,
            anomalies = anomalies,
            lastUpdatedBy = submission.userId
        )
        state = state.copy(shifts = state.shifts + workShift, logs = state.logs + log("create", workShift.id, submission.userId))
        persist()
        return workShift
    }

    fun confirmShift(id: String, actorId: String): WorkShift? {
        val existing = state.shifts.firstOrNull { it.id == id } ?: return null
        val updated = existing.copy(status = ShiftStatus.CONFIRMED, lastUpdatedBy = actorId)
        replaceShift(updated)
        state = state.copy(logs = state.logs + log("confirm", id, actorId))
        persist()
        return updated
    }

    fun approveShift(id: String, approverId: String): WorkShift? {
        val existing = state.shifts.firstOrNull { it.id == id } ?: return null
        val updated = existing.copy(status = ShiftStatus.APPROVED, approverId = approverId, lastUpdatedBy = approverId)
        replaceShift(updated)
        state = state.copy(logs = state.logs + log("approve", id, approverId))
        persist()
        return updated
    }

    fun updateConfig(newConfig: AppConfig, actorId: String): AppConfig {
        state = state.copy(config = newConfig, logs = state.logs + log("update-config", newConfig.id, actorId))
        persist()
        return newConfig
    }

    private fun replaceShift(updated: WorkShift) {
        state = state.copy(shifts = state.shifts.map { if (it.id == updated.id) updated else it })
    }

    private fun validateShift(submission: ShiftSubmission, slices: List<TimeSlice>): List<String> {
        val errors = mutableListOf<String>()
        val shiftMinutes = duration(submission.startTime, submission.endTime) - submission.breakMinutes
        val sliceMinutes = slices.sumOf { duration(it.startTime, it.endTime) }
        if (sliceMinutes != shiftMinutes) errors.add("Somma fasce ($sliceMinutes min) diversa dal turno ($shiftMinutes min)")
        slices.forEach { slice ->
            if (slice.startTime < submission.startTime || slice.endTime > submission.endTime) {
                errors.add("Fascia ${slice.id} fuori dal turno")
            }
        }
        val overlaps = slices.any { a ->
            slices.filter { it.id != a.id }.any { b -> a.startTime < b.endTime && b.startTime < a.endTime }
        }
        if (overlaps) errors.add("Fasce sovrapposte")
        return errors
    }

    private fun duration(start: LocalTime, end: LocalTime): Int {
        val startJava = start.toJavaLocalTime()
        val endJava = end.toJavaLocalTime()
        val minutes = Duration.between(startJava, endJava).toMinutes().toInt()
        return if (minutes < 0) 0 else minutes
    }

    private fun log(action: String, targetId: String, actorId: String): ChangeLog = ChangeLog(
        id = UUID.randomUUID().toString(),
        timestamp = Clock.System.now(),
        actorId = actorId,
        action = action,
        targetId = targetId,
        description = "$action on $targetId by $actorId"
    )

    private fun persist() {
        File(DATA_FILE).writeText(json.encodeToString(state))
    }
}
