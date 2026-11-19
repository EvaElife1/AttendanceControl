package com.example.attendancecontrol.data.model

/** Доменная модель студента (не привязана к Room) */
data class Student(
    val id: Int,
    val firstName: String,  // имя
    val lastName: String,   // фамилия
    val groupName: String   // группа
)

/** Доменная модель записи посещаемости (не привязана к Room) */
data class Attendance(
    val studentId: Int,
    val lessonDate: String,         // дата занятия в формате ГГГГ-ММ-ДД
    val subject: String,            // дисциплина
    val status: String,             // статус: «Присутствовал» / «Отсутствовал» / «Опоздал»
    val comment: String? = null     // опциональный комментарий
)
