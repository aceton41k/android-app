package com.github.aceton41k.simpleapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aceton41k.simpleapp.R
import com.github.aceton41k.simpleapp.model.Post
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class PostAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

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

    fun addPosts(newPosts: List<Post>) {
        if (newPosts.isNotEmpty()) {
            val startPosition = posts.size
            posts.addAll(newPosts)
            notifyItemRangeInserted(startPosition, newPosts.size)
            Log.d("PostAdapter", "Added ${newPosts.size} posts, new size: ${posts.size}")
        }
    }

    private fun formatDateTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) {
            return "No date available"
        }

        return try {
            // Определите формат даты и времени, используемый в строке
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val dateTime = LocalDateTime.parse(dateTimeString, formatter)

            // Форматирование в строку с учетом локализации устройства
            val localizedFormatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.getDefault())
            dateTime.format(localizedFormatter)
        } catch (e: DateTimeParseException) {
            // Обработка ошибок парсинга
            "Invalid date"
        }
    }
}
