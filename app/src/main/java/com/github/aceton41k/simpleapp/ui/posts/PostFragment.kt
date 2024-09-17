package com.github.aceton41k.simpleapp.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aceton41k.simpleapp.R
import com.github.aceton41k.simpleapp.adapter.PostAdapter
import com.github.aceton41k.simpleapp.api.RetrofitClient
import com.github.aceton41k.simpleapp.model.Post
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Загружаем посты
        fetchPosts()

        return view
    }

    private fun fetchPosts() {
        lifecycleScope.launch {
            try {
                val posts = RetrofitClient.api.getPosts()
                postAdapter = PostAdapter(posts)
                recyclerView.adapter = postAdapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error fetching posts", Toast.LENGTH_SHORT).show()
            }
        }
    }
}