package com.github.aceton41k.simpleapp.ui.posts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aceton41k.simpleapp.api.PostApiService
import com.github.aceton41k.simpleapp.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class PostViewModel(private val postApiService: PostApiService) : ViewModel() {

    // StateFlow для управления состоянием постов
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    // StateFlow для управления состоянием загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPosts(page: Int, size: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = postApiService.getPosts(page, size)
                if (response.isSuccessful) {
                    _posts.value = response.body()?.content ?: emptyList()
                }
            } catch (e: Exception) {
                // Обработка ошибок
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _postResponse = MutableLiveData<Response<Post>>()
    val postResponse: LiveData<Response<Post>> get() = _postResponse

    fun createPost(post: Post, onPostCreated: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            // Предполагается, что вы вызываете API для создания поста
            try {
                val response = postApiService.createPost(post)
                if (response.isSuccessful) {
                    // Проверяем, что response.body() не null
                    response.body()?.let { createdPost ->
                        _posts.value = _posts.value + createdPost // Обновляем список постов
                        onPostCreated("Post added!") // Передаем сообщение о создании поста
                    } ?: run {
                        // Обработка случая, когда тело ответа null
                        Log.e("PostViewModel", "Post creation returned null response")
                    }
                } else {
                    // Обработка ошибок, например, вывод сообщения об ошибке
                    Log.e("PostViewModel", "Error creating post: ${response.message()}")
                }
            } catch (e: Exception) {
                // Обработка исключений
            } finally {
                _isLoading.value = false
            }
        }
    }
}