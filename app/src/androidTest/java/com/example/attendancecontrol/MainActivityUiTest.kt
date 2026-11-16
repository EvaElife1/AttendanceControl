package com.example.attendancecontrol

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI-тесты для Спринта 1 (роль QA/DevOps).
 * Покрывают три тест-кейса:
 *  TC-01 — запуск приложения (MainActivity)
 *  TC-02 — отображение главного экрана и кнопок
 *  TC-03 — реакция кнопок (навигация на экраны-заготовки)
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityUiTest {

    /**
     * TC-01. Запуск приложения.
     * MainActivity должна запуститься без аварийного завершения.
     */
    @Test
    fun tc01_mainActivity_launches() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Если Activity запустилась — тест пройден.
            // Дополнительно проверяем, что главный экран отображается.
            onView(withId(R.id.btnStudents)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-02. Отображение главного экрана.
     * На экране должны быть видны три кнопки.
     */
    @Test
    fun tc02_mainScreen_showsAllButtons() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnStudents)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAttendance)).check(matches(isDisplayed()))
            onView(withId(R.id.btnStatistics)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-03. Реакция кнопок.
     * Нажатие на кнопку «Студенты» открывает StudentsActivity.
     */
    @Test
    fun tc03_clickStudents_opensStudentsActivity() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnStudents)).perform(click())
            // Проверяем, что открылся экран студентов — ищем его корневой layout.
            onView(withId(R.id.studentsRoot)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-03. Реакция кнопок.
     * Нажатие на кнопку «Отметить посещаемость» открывает AttendanceActivity.
     */
    @Test
    fun tc03_clickAttendance_opensAttendanceActivity() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnAttendance)).perform(click())
            onView(withId(R.id.attendanceRoot)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-03. Реакция кнопок.
     * Нажатие на кнопку «Статистика» открывает StatisticsActivity.
     */
    @Test
    fun tc03_clickStatistics_opensStatisticsActivity() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnStatistics)).perform(click())
            onView(withId(R.id.statisticsRoot)).check(matches(isDisplayed()))
        }
    }
}