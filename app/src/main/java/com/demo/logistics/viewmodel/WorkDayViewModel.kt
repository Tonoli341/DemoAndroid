package com.demo.logistics.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.demo.logistics.data.WorkDayRepository
import com.demo.logistics.model.TimeSlice
import com.demo.logistics.model.WorkDay
import java.time.LocalDate
import java.time.LocalTime

class WorkDayViewModel(
    private val repository: WorkDayRepository = WorkDayRepository()
) : ViewModel() {

    var date: LocalDate by mutableStateOf(LocalDate.now())
        private set
    var startTimeText by mutableStateOf("08:00")
        private set
    var endTimeText by mutableStateOf("17:00")
        private set
    var breakMinutesText by mutableStateOf("60")
        private set
    var stepError by mutableStateOf<String?>(null)
        private set

    private val sliceDefaults = listOf("Commessa A", "Commessa B", "Commessa C")
    private val costCenters = listOf("Magazzino", "Ufficio", "Altro")

    val slices = mutableStateListOf<EditableSlice>()

    init {
        addSlice()
    }

    fun updateDate(newDate: LocalDate) {
        date = newDate
    }

    fun updateStartTime(value: String) { startTimeText = value }
    fun updateEndTime(value: String) { endTimeText = value }
    fun updateBreakMinutes(value: String) { breakMinutesText = value }

    fun addSlice() {
        slices.add(
            EditableSlice(
                jobName = sliceDefaults.first(),
                costCenterName = costCenters.first(),
                startTime = "09:00",
                endTime = "11:00"
            )
        )
    }

    fun updateSlice(index: Int, updater: (EditableSlice) -> EditableSlice) {
        if (index in slices.indices) {
            slices[index] = updater(slices[index])
        }
    }

    fun removeSlice(index: Int) {
        if (index in slices.indices && slices.size > 1) {
            slices.removeAt(index)
        }
    }

    fun validateStep1(): Boolean {
        val start = startTimeText.toLocalTimeOrNull()
        val end = endTimeText.toLocalTimeOrNull()
        val breakMinutes = breakMinutesText.toIntOrNull()?.takeIf { it >= 0 }
        return if (start == null || end == null || breakMinutes == null || end <= start) {
            stepError = "Controlla orari e pausa"
            false
        } else {
            stepError = null
            true
        }
    }

    fun saveWorkDay(): Boolean {
        val start = startTimeText.toLocalTimeOrNull() ?: return false
        val end = endTimeText.toLocalTimeOrNull() ?: return false
        val breakMinutes = breakMinutesText.toIntOrNull() ?: return false
        if (end <= start) return false

        val sliceModels = slices.mapNotNull {
            val sliceStart = it.startTime.toLocalTimeOrNull()
            val sliceEnd = it.endTime.toLocalTimeOrNull()
            if (sliceStart != null && sliceEnd != null && sliceEnd > sliceStart) {
                TimeSlice(it.jobName, it.costCenterName, sliceStart, sliceEnd)
            } else {
                null
            }
        }

        val totalSliceMinutes = sliceModels.sumOf { durationMinutes(it.startTime, it.endTime) }
        val totalShiftMinutes = durationMinutes(start, end) - breakMinutes
        if (totalSliceMinutes > totalShiftMinutes + 30) {
            stepError = "Fasce oltre il turno (tolleranza 30 min)"
            return false
        }

        repository.saveWorkDay(
            WorkDay(
                date = date,
                startTime = start,
                endTime = end,
                breakMinutes = breakMinutes,
                slices = sliceModels
            )
        )
        stepError = null
        return true
    }

    fun history(): List<Pair<LocalDate, WorkDay?>> {
        return repository.lastSevenDays().map { it to repository.getWorkDay(it) }
    }

    fun resetForToday() {
        date = LocalDate.now()
        startTimeText = "08:00"
        endTimeText = "17:00"
        breakMinutesText = "60"
        slices.clear()
        addSlice()
        stepError = null
    }

    private fun durationMinutes(start: LocalTime, end: LocalTime): Int {
        return ((end.toSecondOfDay() - start.toSecondOfDay()) / 60)
    }

    data class EditableSlice(
        val jobName: String,
        val costCenterName: String,
        val startTime: String,
        val endTime: String
    )

    fun availableJobs() = sliceDefaults
    fun availableCostCenters() = costCenters
}

private fun String.toLocalTimeOrNull(): LocalTime? = try {
    LocalTime.parse(this)
} catch (e: Exception) {
    null
}
