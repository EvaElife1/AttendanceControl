package com.example.attendancecontrol.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.attendancecontrol.R

/**
 * Демонстрационный экран авторизации.
 *
 * Учётные данные находятся в клиентском приложении только для тестирования.
 * Для реального использования нужна серверная аутентификация.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginInput = findViewById<EditText>(R.id.loginInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val errorText = findViewById<TextView>(R.id.loginError)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val login = loginInput.text.toString().trim()
            val password = passwordInput.text.toString()

            // Роль определяется по тестовой учётной записи — отдельный выбор роли не нужен.
            val role = when {
                login == "teacher" && password == "teacher123" -> "TEACHER"
                login == "admin" && password == "admin123" -> "ADMIN"
                else -> null
            }

            if (role != null) {
                errorText.text = ""
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .putExtra(EXTRA_USER_ROLE, role)
                )
                finish()
            } else {
                errorText.setText(R.string.login_error)
            }
        }
    }

    companion object {
        const val EXTRA_USER_ROLE = "com.example.attendancecontrol.USER_ROLE"
    }
}
