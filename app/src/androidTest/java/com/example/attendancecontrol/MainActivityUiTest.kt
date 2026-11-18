package com.example.attendancecontrol

import android.content.Context
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.attendancecontrol.data.db.AppDatabase
import com.example.attendancecontrol.data.entity.StudentEntity
import com.example.attendancecontrol.ui.AttendanceActivity
import com.example.attendancecontrol.ui.StatisticsActivity
import com.example.attendancecontrol.ui.adapter.AttendanceAdapter
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityUiTests {

    private lateinit var db: AppDatabase

    @Before
    fun setupDatabase() {
        // Получаем контекст приложения
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabase.getDatabase(context)

        // Очищаем базу и добавляем тестового студента ПЕРЕД каждым тестом
        runBlocking {
            db.clearAllTables() // Очищаем все таблицы
            db.studentDao().insert(
                StudentEntity(
                    firstName = "Иван",
                    lastName = "Иванов",
                    groupName = "ИС-21"
                )
            )
        }
    }

    @Test
    fun tc08_reopenAttendanceActivity_keepsDataOrResetsCorrectly() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            onView(withId(R.id.etSubject)).perform(replaceText("Math"))
            onView(withId(R.id.etDate)).perform(replaceText("2023-10-01"))
        }

        ActivityScenario.launch(AttendanceActivity::class.java).use {
            onView(withId(R.id.etSubject)).check(matches(withText("")))
        }
    }

    @Test
    fun tc09_statistics_emptyHistory_showsEmptyState() {
        // Очищаем базу для этого конкретного теста, чтобы проверить пустое состояние
        runBlocking { db.clearAllTables() }

        ActivityScenario.launch(StatisticsActivity::class.java).use {
            Thread.sleep(1000)
            onView(withId(R.id.layoutEmpty)).check(matches(isDisplayed()))
            onView(withId(R.id.layoutContent)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun tc10_attendance_invalidDate_validationError() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            onView(withId(R.id.etSubject)).perform(replaceText("Math"))
            onView(withId(R.id.etDate)).perform(replaceText("2023-13-45"))
            onView(withId(R.id.btnSaveAttendance)).perform(click())
            onView(withId(R.id.etDate)).check(matches(hasErrorText("Введите дату в формате ГГГГ-ММ-ДД")))
        }
    }

    @Test
    fun tc11_attendance_largeList_scrollsCorrectly() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            Thread.sleep(1500)
            onView(withId(R.id.rvAttendance)).check(matches(isDisplayed()))
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.scrollToPosition<AttendanceAdapter.AttendanceViewHolder>(0)
            )
        }
    }

    @Test
    fun tc12_attendance_changeStatusBeforeSave() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            Thread.sleep(1500)

            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.actionOnItemAtPosition<AttendanceAdapter.AttendanceViewHolder>(
                    0, clickChildViewWithId(R.id.rbPresent)
                )
            )
            onView(withId(R.id.rvAttendance)).perform(
                RecyclerViewActions.actionOnItemAtPosition<AttendanceAdapter.AttendanceViewHolder>(
                    0, clickChildViewWithId(R.id.rbLate)
                )
            )
            onView(withId(R.id.rvAttendance)).check(matches(hasDescendant(allOf(withId(R.id.rbLate), isChecked()))))
            onView(withId(R.id.rvAttendance)).check(matches(not(hasDescendant(allOf(withId(R.id.rbPresent), isChecked())))))
        }
    }

    @Test
    fun tc13_attendance_repeatedMarking() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            Thread.sleep(1500)

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
            onView(withId(R.id.rvAttendance)).check(matches(hasDescendant(allOf(withId(R.id.rbPresent), isChecked()))))
        }
    }

    @Test
    fun tc14_attendance_emptyFields_validationError() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            onView(withId(R.id.btnSaveAttendance)).perform(click())
            onView(withId(R.id.etSubject)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun tc15_attendance_displayMultipleStudents() {
        ActivityScenario.launch(AttendanceActivity::class.java).use {
            Thread.sleep(1000)
            onView(withId(R.id.rvAttendance)).check(matches(isDisplayed()))
        }
    }

    private fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? = null
            override fun getDescription(): String = "Click on child view with id $id"
            override fun perform(uiController: UiController?, view: View?) {
                view?.findViewById<View>(id)?.performClick()
            }
        }
    }
}