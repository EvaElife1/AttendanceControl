package com.example.attendancecontrol

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.attendancecontrol.ui.AttendanceActivity
import com.example.attendancecontrol.ui.adapter.AttendanceAdapter
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AttendanceActivityUiTest {

    @Test
    fun tc01_displayMultipleStudents() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            onView(withText("Иванов Иван (ИС-21)")).check(matches(isDisplayed()))
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.scrollToPosition<AttendanceAdapter.AttendanceViewHolder>(2)
            )
            onView(withText("Сидорова Анна (ИС-21)")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun tc02_selectAllStatuses() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            // Выбираем "Присутствовал" для 0-го элемента
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.actionOnItemAtPosition<AttendanceAdapter.AttendanceViewHolder>(
                    0, clickChildViewWithId(R.id.rbPresent)
                )
            )
            // Проверяем, что внутри RecyclerView есть выбранная кнопка "Присутствовал"
            onView(withId(R.id.rvAttendance)).check(matches(hasDescendant(allOf(withId(R.id.rbPresent), isChecked()))))

            // Меняем на "Отсутствовал"
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.actionOnItemAtPosition<AttendanceAdapter.AttendanceViewHolder>(
                    0, clickChildViewWithId(R.id.rbAbsent)
                )
            )
            onView(withId(R.id.rvAttendance)).check(matches(hasDescendant(allOf(withId(R.id.rbAbsent), isChecked()))))
            onView(withId(R.id.rvAttendance)).check(matches(not(hasDescendant(allOf(withId(R.id.rbPresent), isChecked())))))
        }
    }

    @Test
    fun tc03_changeStatusBeforeSave() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.actionOnItemAtPosition<AttendanceAdapter.AttendanceViewHolder>(
                    1, clickChildViewWithId(R.id.rbPresent)
                )
            )
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.actionOnItemAtPosition<AttendanceAdapter.AttendanceViewHolder>(
                    1, clickChildViewWithId(R.id.rbLate)
                )
            )
            onView(withId(R.id.rvAttendance)).check(matches(hasDescendant(allOf(withId(R.id.rbLate), isChecked()))))
            onView(withId(R.id.rvAttendance)).check(matches(not(hasDescendant(allOf(withId(R.id.rbPresent), isChecked())))))
        }
    }

    @Test
    fun tc04_repeatedMarking() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.actionOnItemAtPosition<AttendanceAdapter.AttendanceViewHolder>(
                    0, clickChildViewWithId(R.id.rbPresent)
                )
            )
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.actionOnItemAtPosition<AttendanceAdapter.AttendanceViewHolder>(
                    0, clickChildViewWithId(R.id.rbPresent)
                )
            )
            // Проверяем, что кнопка всё еще нажата
            onView(withId(R.id.rvAttendance)).check(matches(hasDescendant(allOf(withId(R.id.rbPresent), isChecked()))))
        }
    }

    @Test
    fun tc05_emptyFieldsValidation() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            onView(withId(R.id.btnSaveAttendance)).perform(click())
            // Если поля пустые, приложение не переходит на другой экран, а остается на текущем
            onView(withId(R.id.etSubject)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun tc06_invalidDateValidation() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            // Используем replaceText вместо typeText, чтобы избежать фильтрации ввода
            onView(withId(R.id.etSubject)).perform(replaceText("Математика"))
            onView(withId(R.id.etDate)).perform(replaceText("2026-13-45"))

            onView(withId(R.id.btnSaveAttendance)).perform(click())

            // Проверяем, что появилась ошибка ввода
            onView(withId(R.id.etDate)).check(matches(hasErrorText("Введите дату в формате ГГГГ-ММ-ДД")))
        }
    }

    private fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }
            override fun getDescription(): String {
                return "Click on child view with id $id"
            }
            override fun perform(uiController: UiController?, view: View?) {
                val childView = view?.findViewById<View>(id)
                childView?.performClick()
            }
        }
    }
}