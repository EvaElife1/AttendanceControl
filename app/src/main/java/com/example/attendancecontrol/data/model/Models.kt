package com.example.attendancecontrol.data.model

data class Student(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val groupName: String
)

/** Доменная модель данных для хранения информации о посещаемости. */
data class Attendance(
    val studentId: Int,
    val lessonDate: String,
    val subject: String,
    val status: String,
    val comment: String? = null
)
