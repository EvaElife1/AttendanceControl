package com.example.attendancecontrol

import org.junit.Assert.*
import org.junit.Test

class MainActivityUnitTest {

    @Test
    fun correctData_returnsNull() {
        val result = StudentValidator.validate("Иванов", "Иван", "ИС-21")
        assertNull(result)
    }

    @Test
    fun emptyLastName_returnsError() {
        val result = StudentValidator.validate("", "Иван", "ИС-21")
        assertEquals("Введите фамилию", result)
    }

    @Test
    fun spacesInLastName_returnsError() {
        val result = StudentValidator.validate("   ", "Иван", "ИС-21")
        assertEquals("Введите фамилию", result)
    }

    @Test
    fun emptyFirstName_returnsError() {
        val result = StudentValidator.validate("Иванов", "", "ИС-21")
        assertEquals("Введите имя", result)
    }

    @Test
    fun spacesInFirstName_returnsError() {
        val result = StudentValidator.validate("Иванов", "   ", "ИС-21")
        assertEquals("Введите имя", result)
    }

    @Test
    fun emptyGroup_returnsError() {
        val result = StudentValidator.validate("Иванов", "Иван", "")
        assertEquals("Введите группу", result)
    }

    @Test
    fun spacesInGroup_returnsError() {
        val result = StudentValidator.validate("Иванов", "Иван", "     ")
        assertEquals("Введите группу", result)
    }

    @Test
    fun allEmpty_returnsLastNameError() {
        val result = StudentValidator.validate("", "", "")
        assertEquals("Введите фамилию", result)
    }

    @Test
    fun allSpaces_returnsLastNameError() {
        val result = StudentValidator.validate("  ", "  ", "  ")
        assertEquals("Введите фамилию", result)
    }
}