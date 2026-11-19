package com.example.attendancecontrol.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Валидатор данных студента.
 * Используется перед добавлением студента в БД.
 */
object StudentValidator {

    /**
     * Проверяет корректность введённых данных студента.
     * @return строка с описанием первой найденной ошибки, или null если всё корректно
     */
    fun validate(lastName: String, firstName: String, group: String): String? {
        return when {
            lastName.trim().isEmpty()  -> "Введите фамилию"
            firstName.trim().isEmpty() -> "Введите имя"
            group.trim().isEmpty()     -> "Введите группу"
            else                       -> null
        }
    }
}

/**
 * Устанавливает верхний padding под статус-бар для указанного View.
 * Не реагирует на появление клавиатуры (IME), поэтому высота шапки остаётся стабильной.
 */
fun applyStatusBarPadding(view: View) {
    val initialPaddingTop = view.paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        v.setPadding(v.paddingLeft, initialPaddingTop + statusBarHeight, v.paddingRight, v.paddingBottom)
        insets
    }
}

/**
 * Устанавливает нижний padding под навигационную панель (кнопки или жестовая полоса).
 * Применяется к прокручиваемым контейнерам, чтобы последний элемент не перекрывался панелью.
 */
fun applyNavigationBarPadding(view: View) {
    val initialPaddingBottom = view.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, initialPaddingBottom + navBarHeight)
        insets
    }
}

/**
 * Устанавливает padding сразу под статус-бар (сверху) и навигационную панель (снизу).
 * Используется для полноэкранных корневых контейнеров (например, главный экран).
 */
fun applySystemBarsPadding(view: View) {
    val initialTop    = view.paddingTop
    val initialBottom = view.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(
            v.paddingLeft,
            initialTop    + systemBars.top,
            v.paddingRight,
            initialBottom + systemBars.bottom
        )
        insets
    }
}
