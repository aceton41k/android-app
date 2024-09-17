package com.github.aceton41k.simpleapp.api

import com.github.aceton41k.simpleapp.model.AuthRequest
import com.github.aceton41k.simpleapp.model.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/auth/login")
    fun login(@Body request: AuthRequest): Call<AuthResponse>
}