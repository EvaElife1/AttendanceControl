package com.example.attendancecontrol

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Простые unit-тесты для Спринта 1.
 * Проверяют, что классы Activity существуют и имеют правильные имена.
 * Полноценная проверка запуска Activity выполняется в UI-тестах (Espresso).
 */
class MainActivityUnitTest {

    @Test
    fun mainActivity_classExists() {
        assertEquals("MainActivity", MainActivity::class.java.simpleName)
    }

    @Test
    fun studentsActivity_classExists() {
        assertEquals("StudentsActivity", StudentsActivity::class.java.simpleName)
    }

    @Test
    fun attendanceActivity_classExists() {
        assertEquals("AttendanceActivity", AttendanceActivity::class.java.simpleName)
    }

    @Test
    fun statisticsActivity_classExists() {
        assertEquals("StatisticsActivity", StatisticsActivity::class.java.simpleName)
    }
}