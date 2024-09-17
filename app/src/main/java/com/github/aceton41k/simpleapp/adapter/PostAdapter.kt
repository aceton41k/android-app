package com.github.aceton41k.simpleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aceton41k.simpleapp.R
import com.github.aceton41k.simpleapp.model.Post
import com.github.aceton41k.simpleapp.model.formatDateTime

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.postTitle)
        val message: TextView = itemView.findViewById(R.id.postMessage)
        val createdAt: TextView = itemView.findViewById(R.id.postCreatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.title.text = post.title
        holder.message.text = post.message
        holder.createdAt.text = formatDateTime(post.createdAt)
    }

    override fun getItemCount() = posts.size
}