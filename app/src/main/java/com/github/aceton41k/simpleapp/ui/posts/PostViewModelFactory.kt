package com.github.aceton41k.simpleapp.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.aceton41k.simpleapp.api.PostApiService

class PostViewModelFactory(
    private val postApiService: PostApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(postApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}