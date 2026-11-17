package com.example.attendancecontrol

/**
 * Модель данных для хранения информации о посещаемости.
 * На данном этапе (Спринт 3) используется для работы в памяти.
 * В Спринте 4 будет преобразована в Room Entity.
 */
data class Attendance(
    val studentId: Int,
    val lessonDate: String,
    val subject: String,
    val status: String,
    val comment: String? = null
)