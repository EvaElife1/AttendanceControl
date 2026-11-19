package com.example.attendancecontrol.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.attendancecontrol.R

/**
 * Демонстрационный экран авторизации.
 *
 * ВНИМАНИЕ: учётные данные зашиты в приложение только для демонстрации.
 * Для реального использования нужна серверная проверка и безопасное хранение паролей.
 */
class LoginActivity : AppCompatActivity() {

    private enum class Role { TEACHER, ADMIN }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val roleGroup = findViewById<RadioGroup>(R.id.roleGroup)
        val loginInput = findViewById<EditText>(R.id.loginInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val errorText = findViewById<TextView>(R.id.loginError)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val login = loginInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val role = when (roleGroup.checkedRadioButtonId) {
                R.id.roleAdmin -> Role.ADMIN
                else -> Role.TEACHER
            }

            val isValid = when (role) {
                Role.TEACHER -> login == "teacher" && password == "teacher123"
                Role.ADMIN -> login == "admin" && password == "admin123"
            }

            if (isValid) {
                errorText.text = ""
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .putExtra(EXTRA_USER_ROLE, role.name)
                )
                finish()
            } else {
                errorText.setText(R.string.login_error)
                Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_USER_ROLE = "com.example.attendancecontrol.USER_ROLE"
    }
}
