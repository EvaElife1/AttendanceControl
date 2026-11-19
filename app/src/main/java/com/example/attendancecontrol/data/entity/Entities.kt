package com.example.attendancecontrol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room-сущность: строка таблицы students */
@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,  // имя студента
    val lastName: String,   // фамилия студента
    val groupName: String   // номер/название группы
)

/** Room-сущность: строка таблицы attendance (одна запись = один студент на одном занятии) */
@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,     // внешний ключ → students.id
    val lessonDate: String, // дата занятия в формате ГГГГ-ММ-ДД
    val subject: String,    // название дисциплины
    val status: String      // статус: «Присутствовал» / «Отсутствовал» / «Опоздал»
)
