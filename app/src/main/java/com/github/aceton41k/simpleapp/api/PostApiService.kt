package com.github.aceton41k.simpleapp.api

import com.github.aceton41k.simpleapp.model.GetAllPostResponse
import com.github.aceton41k.simpleapp.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PostApiService {
    @GET("/api/posts")
    suspend fun getPosts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetAllPostResponse>

    @POST("/api/posts")
    suspend fun createPost(
        @Body body: Post
    ): Response<Post>
}