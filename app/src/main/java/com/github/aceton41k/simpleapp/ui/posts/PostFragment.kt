package com.github.aceton41k.simpleapp.ui.posts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aceton41k.simpleapp.R
import com.github.aceton41k.simpleapp.activity.LoginActivity
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
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        val drawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.divider
        ) // Используем кастомный drawable
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
                        }
                    }
                } else if (response.code() == 403) {
                    handleTokenExpired()
                }
            } catch (e: Exception) {
                // Обработка ошибок сети или других исключений
            } finally {
                isLoading = false
            }
        }

    }

    private fun handleTokenExpired() {
        // Очистите токен
        val sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("jwt_token")
            apply()
        }

        // Покажите тостовое уведомление
        Toast.makeText(requireContext(), "Session has expired. Please log in again.", Toast.LENGTH_SHORT).show()


        // Перейдите на экран логина
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Закройте текущую активность, если нужно
    }
}