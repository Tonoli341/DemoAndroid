package com.demo.logistics.data

import com.demo.logistics.model.WorkDay
import java.time.LocalDate

class WorkDayRepository {
    private val workDays = mutableListOf<WorkDay>()

    fun saveWorkDay(workDay: WorkDay) {
        workDays.removeAll { it.date == workDay.date }
        workDays.add(workDay)
    }

    fun getWorkDay(date: LocalDate): WorkDay? = workDays.find { it.date == date }

    fun lastSevenDays(reference: LocalDate = LocalDate.now()): List<LocalDate> {
        return (0..6).map { reference.minusDays(it.toLong()) }
    }

    fun allWorkDays(): List<WorkDay> = workDays.toList()
}
