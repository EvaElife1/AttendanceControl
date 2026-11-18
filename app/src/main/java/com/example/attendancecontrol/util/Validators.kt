package com.example.attendancecontrol.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

/**
 * Устанавливает верхний отступ только под статус-бар.
 * Не реагирует на появление экранной клавиатуры (IME), благодаря чему
 * высота верхней плашки остаётся фиксированной и не раздувается.
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
 * Устанавливает нижний отступ под нижнюю панель навигации устройства (navigation bars / gestures).
 * Добавляет высоту панели к исходному нижнему padding представления.
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
 * Комбинированный отступ: верхний под статус-бар и нижний под панель навигации.
 * Полезно для полноэкранных контейнеров (например, главного экрана).
 */
fun applySystemBarsPadding(view: View) {
    val initialTop = view.paddingTop
    val initialBottom = view.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(
            v.paddingLeft,
            initialTop + systemBars.top,
            v.paddingRight,
            initialBottom + systemBars.bottom
        )
        insets
    }
}
