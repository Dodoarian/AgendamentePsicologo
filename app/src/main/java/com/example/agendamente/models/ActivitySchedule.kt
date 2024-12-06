package com.example.agendamente.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ActivitySchedule(
    var id: String = "",
    val title: String = "",
    val description: String,
    val hour: String = "",
    val frequency: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val psychologistId: String = "",
    val patientId: String = ""
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "frequency" to frequency,
            "hour" to hour,
            "startDate" to startDate,
            "endDate" to endDate,
            "psychologistId" to psychologistId,
            "patientId" to patientId,
        )
    }
}

data class Schedule(
    var id: String = "",
    var activityScheduleId: String = "",
    var note: String = "",
    var done: Int = 0,
    var scheduled: Long = 0L,
    var dateDone: Long? = null,
    var createdAt: Long = 0L,
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "activityScheduleId" to activityScheduleId,
            "note" to note,
            "done" to done,
            "scheduled" to scheduled,
            "dateDone" to dateDone,
            "createdAt" to createdAt,
        )
    }
}