package com.github.aceton41k.simpleapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.aceton41k.simpleapp.R
import com.github.aceton41k.simpleapp.api.AuthApiService
import com.github.aceton41k.simpleapp.api.RetrofitClient
import com.github.aceton41k.simpleapp.api.SharedPreferencesTokenProvider
import com.github.aceton41k.simpleapp.model.AuthRequest
import com.github.aceton41k.simpleapp.model.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var authApiService: AuthApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val tokenProvider = SharedPreferencesTokenProvider(this)
        RetrofitClient.setTokenProvider(tokenProvider)

        // Проверка наличия токена и перенаправление при необходимости
        val token = sharedPreferences.getString("jwt_token", null)
        if (token != null) {
            // Если токен существует, сразу переходим на MainActivity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Инициализация AuthApiService
        authApiService = RetrofitClient.authApiService

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val authRequest = AuthRequest(email, password)

        authApiService.login(authRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                    val token = response.body()?.token
                    token?.let {
                        // Сохраните токен в SharedPreferences
                        with(sharedPreferences.edit()) {
                            putString("jwt_token", it)
                            apply()
                        }
                    }

                    // Переход на главный экран приложения
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

