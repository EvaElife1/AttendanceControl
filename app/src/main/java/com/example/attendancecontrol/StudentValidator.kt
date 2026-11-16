package com.example.attendancecontrol

object StudentValidator {

    fun validate(lastName: String, firstName: String, group: String): String? {
        return when {
            lastName.trim().isEmpty() -> "Введите фамилию"
            firstName.trim().isEmpty() -> "Введите имя"
            group.trim().isEmpty() -> "Введите группу"
            else -> null
        }
    }
}