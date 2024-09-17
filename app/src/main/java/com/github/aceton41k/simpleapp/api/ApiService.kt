package com.github.aceton41k.simpleapp.api

import com.github.aceton41k.simpleapp.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET("/api/posts")
    suspend fun getPosts(): List<Post>
}