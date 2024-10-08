package com.github.aceton41k.simpleapp.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.aceton41k.simpleapp.api.RetrofitClient.postApiService

class PostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels {
        // Создаем фабрику для ViewModel, передавая PostApiService
        PostViewModelFactory(postApiService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PostFeed(viewModel = viewModel) // Не передаем onNewPostClick здесь
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Загружаем посты при создании экрана
        viewModel.loadPosts(page = 0, size = 10)
    }
}