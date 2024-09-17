package com.github.aceton41k.simpleapp.api

import com.github.aceton41k.simpleapp.model.PostResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApiService {
    @GET("/api/posts")
    suspend fun getPosts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PostResponse>
}