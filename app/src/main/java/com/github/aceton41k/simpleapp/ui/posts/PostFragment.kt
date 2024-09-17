package com.github.aceton41k.simpleapp.ui.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aceton41k.simpleapp.R
import com.github.aceton41k.simpleapp.adapter.PostAdapter
import com.github.aceton41k.simpleapp.api.RetrofitClient.postApiService
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    private var currentPage = 0
    private val pageSize = 10
    private var isLoading = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)

        // Инициализация адаптера и установки
        postAdapter = PostAdapter(mutableListOf())
        recyclerView.adapter = postAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Добавляем разделитель
        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider) // Используем кастомный drawable
        drawable?.let {
            dividerItemDecoration.setDrawable(it)
        }
        recyclerView.addItemDecoration(dividerItemDecoration)

        // Установка слушателя прокрутки
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    loadMorePosts()
                }
            }
        })

        // Загружаем данные
        loadMorePosts()

        return view
    }

    private fun loadMorePosts() {
        if (isLoading) return

        isLoading = true

        lifecycleScope.launch {
            try {
                val response = postApiService.getPosts(currentPage, pageSize)
                if (response.isSuccessful) {
                    response.body()?.let { postResponse ->
                        val newPosts = postResponse.content
                        if (newPosts.isNotEmpty()) {
                            postAdapter.addPosts(newPosts)
                            currentPage++
                        } else {
                            Log.d("PostFragment", "No more posts to load")
                        }
                    } ?: run {
                        Log.d("PostFragment", "Response body is null")
                    }
                } else {
                    Log.d("PostFragment", "Response error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PostFragment", "Error loading posts", e)
            } finally {
                isLoading = false
            }
        }
    }
}