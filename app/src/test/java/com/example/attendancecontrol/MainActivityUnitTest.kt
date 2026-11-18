package com.example.attendancecontrol

import org.junit.Assert.*
import org.junit.Test
import com.example.attendancecontrol.util.StudentValidator

class MainActivityUnitTest {

    // ТЕСТ 1: Валидация корректных данных
    @Test
    fun tc01_validator_correctData_returnsNull() {
        val result = StudentValidator.validate("Иванов", "Иван", "ИС-21")
        assertNull(result)
    }

    // ТЕСТ 2: Валидация пустых полей (проверка приоритета ошибок)
    @Test
    fun tc02_validator_allEmpty_returnsLastNameError() {
        val result = StudentValidator.validate("", "", "")
        assertEquals("Введите фамилию", result)
    }

    // ТЕСТ 3: Валидация пробелов
    @Test
    fun tc03_validator_spacesOnly_returnsError() {
        val result = StudentValidator.validate("   ", "Иван", "ИС-21")
        assertEquals("Введите фамилию", result)
    }
}