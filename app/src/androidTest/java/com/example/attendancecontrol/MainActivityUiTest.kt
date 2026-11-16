package com.example.attendancecontrol

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(StudentsActivity::class.java)

    @Test
    fun addStudent_success() {
        onView(withId(R.id.etLastName)).perform(replaceText("Петров"), closeSoftKeyboard())
        onView(withId(R.id.etFirstName)).perform(replaceText("Пётр"), closeSoftKeyboard())
        onView(withId(R.id.etGroup)).perform(replaceText("ИС-22"), closeSoftKeyboard())

        onView(withId(R.id.btnAddStudent)).perform(click())

        // Проверяем, что студент появился
        onView(withText("Петров Пётр, ИС-22")).check(matches(isDisplayed()))
    }

    @Test
    fun emptyLastName_showsError() {
        onView(withId(R.id.etFirstName)).perform(replaceText("Иван"), closeSoftKeyboard())
        onView(withId(R.id.etGroup)).perform(replaceText("ИС-21"), closeSoftKeyboard())
        onView(withId(R.id.btnAddStudent)).perform(click())

        onView(withId(R.id.etLastName)).check(matches(hasErrorText("Введите фамилию")))
    }

    @Test
    fun emptyFirstName_showsError() {
        onView(withId(R.id.etLastName)).perform(replaceText("Иванов"), closeSoftKeyboard())
        onView(withId(R.id.etGroup)).perform(replaceText("ИС-21"), closeSoftKeyboard())
        onView(withId(R.id.btnAddStudent)).perform(click())

        onView(withId(R.id.etFirstName)).check(matches(hasErrorText("Введите имя")))
    }

    @Test
    fun emptyGroup_showsError() {
        onView(withId(R.id.etLastName)).perform(replaceText("Иванов"), closeSoftKeyboard())
        onView(withId(R.id.etFirstName)).perform(replaceText("Иван"), closeSoftKeyboard())
        onView(withId(R.id.btnAddStudent)).perform(click())

        onView(withId(R.id.etGroup)).check(matches(hasErrorText("Введите группу")))
    }

    @Test
    fun onlySpaces_showsError() {
        onView(withId(R.id.etLastName)).perform(replaceText("   "), closeSoftKeyboard())
        onView(withId(R.id.etFirstName)).perform(replaceText("   "), closeSoftKeyboard())
        onView(withId(R.id.etGroup)).perform(replaceText("   "), closeSoftKeyboard())
        onView(withId(R.id.btnAddStudent)).perform(click())

        onView(withId(R.id.etLastName)).check(matches(hasErrorText("Введите фамилию")))
    }

    @Test
    fun deleteStudent_success() {
        // Добавляем студента
        onView(withId(R.id.etLastName)).perform(replaceText("Сидоров"), closeSoftKeyboard())
        onView(withId(R.id.etFirstName)).perform(replaceText("Сидор"), closeSoftKeyboard())
        onView(withId(R.id.etGroup)).perform(replaceText("ИС-23"), closeSoftKeyboard())
        onView(withId(R.id.btnAddStudent)).perform(click())

        // Проверяем, что появился
        onView(withText("Сидоров Сидор, ИС-23")).check(matches(isDisplayed()))

        // Нажимаем удалить
        onView(withId(R.id.btnDeleteStudent)).perform(click())

        // Если появился диалог подтверждения — подтверждаем
        try {
            onView(withText("Удалить")).perform(click())
        } catch (e: Exception) {
            // Диалога нет — ничего страшного
        }

        // Правильная проверка, что студент исчез
        onView(withText("Сидоров Сидор, ИС-23")).check(doesNotExist())
    }
}