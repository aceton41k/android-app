package com.github.aceton41k.simpleapp.ui.posts

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.aceton41k.simpleapp.R
import com.github.aceton41k.simpleapp.model.Post
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFeed(viewModel: PostViewModel) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showModalBottomSheet = remember { mutableStateOf(false) }
    val context = LocalContext.current // Получаем контекст

    // Функция для обработки создания поста
    fun onPostCreated(title: String, message: String) {
        val newPost = Post(
            id = 0,
            title = title,
            message = message,
            createdAt = null,
            createdBy = null,
            updatedAt = null,
            modifiedBy = null
        )
        viewModel.createPost(newPost) { toastMessage ->
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show() // Показываем тост
        }
    }

    // Основной контейнер
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else {
            if (posts.isNotEmpty()) {
                LazyColumn {
                    items(posts) { post ->
                        PostItem(post)
                    }
                }
            } else {
                Text(text = "No posts available")
            }
        }

        // Плавающая кнопка для создания нового поста
        FloatingActionButton(
            onClick = {
                Log.d("PostFeed", "FloatingActionButton clicked, showing modal bottom sheet.")
                showModalBottomSheet.value = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(70.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Comment,
                contentDescription = stringResource(R.string.new_post),
                modifier = Modifier
                    .fillMaxSize() // Устанавливаем размер иконки
                    .padding(15.dp) //
            )
        }

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

//         Показ модального окна при изменении состояния showModalBottomSheet
        if (showModalBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showModalBottomSheet.value = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // Высота будет зависеть от контента
            ) {
                // Содержимое модального окна
                NewPostDialog(
                    onDismiss = { showModalBottomSheet.value = false }, // Закрываем окно при отмене
                    onPostCreated = { title, message  ->
                        onPostCreated(title, message) // Вызов функции для создания поста
                        showModalBottomSheet.value =
                            false // Закрыть модальное окно после создания поста
                    }
                )
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {

    val commonTextStyle = TextStyle(
        fontSize = 15.sp,
        fontStyle = FontStyle.Italic
    )

    Column(
        modifier = Modifier.padding(5.dp) // Общие отступы для всего контейнера
    ) {
        // Здесь можете описать, как будет выглядеть каждый элемент списка
        Text(
            text = post.title,
            style = MaterialTheme.typography.h5, // Установите стиль текста
            color = Color.Black // Установите цвет текста

        ) // Добавьте отступы) // Пример отображения заголовка поста

        Text(
            text = post.message,
            style = MaterialTheme.typography.subtitle1
        )

        post.createdAt?.let { date ->
            // Используем стандартный формат ISO_OFFSET_DATE_TIME для корректной обработки даты с Z
            val dateTime = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            // Формат для вывода
            val outputFormatter =
                DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.getDefault())
            val formattedDate = dateTime.format(outputFormatter)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Posted on: $formattedDate,",
                    style = commonTextStyle,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "by ${post.createdBy}",
                    style = commonTextStyle
                )
            }
        }
        Divider(
            thickness = 1.dp,
            color = Color.Black,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
fun NewPostDialog(onDismiss: () -> Unit, onPostCreated: (String, String) -> Unit) {
    val titleState = remember { mutableStateOf("") }
    val messageState = remember { mutableStateOf("") }

    fun buttonStyle(): Modifier {
        return Modifier
            .size(150.dp, 70.dp) // Размер кнопки
            .padding(8.dp) // Внешний отступ (опционально)
    }

    val textStyle = TextStyle(
        fontSize = 20.sp,  // Размер шрифта
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(text = "Create New Post", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = titleState.value,
            onValueChange = { titleState.value = it },
            label = {
                Text(
                    "Title",
                    style = textStyle
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = messageState.value,
            onValueChange = { messageState.value = it },
            label = {
                Text(
                    "Message",
                    style = textStyle
                )
            },
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(100.dp),
                modifier = buttonStyle().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Dismiss",
                    style = textStyle
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (titleState.value.isNotBlank() && messageState.value.isNotBlank()) {
                        onPostCreated(titleState.value, messageState.value)
                    }
                },
                shape = RoundedCornerShape(100.dp),
                modifier = buttonStyle().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Post!",
                    style = textStyle
                )
            }
        }
    }
}

@Preview(apiLevel = 34)
@Composable
fun PostItemPreview() {
    // Создайте тестовый экземпляр Post
    val testPost = Post(
        id = 1,
        title = "Test post title extended",
        message = "This is the message of the test post with many letters more and more letters.",
        createdAt = "2024-09-27T15:46:24.429959Z",
        updatedAt = "2024-09-27T15:46:24.429959Z",
        modifiedBy = 666,
        createdBy = 666
    )
    Column {
        PostItem(post = testPost) // Вызовите функцию PostItem с тестовыми данными
    }
}

@Preview(showBackground = true)
@Composable
fun NewPostDialogPreview() {
    NewPostDialog(
        onDismiss = { /* Пустая логика для превью */ },
        onPostCreated = { title, message ->
            // В превью можно оставить пустой обработчик
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FloatingActionButtonPreview() {
    // Используйте MaterialTheme для корректного отображения
    MaterialTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* действие при нажатии */ },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(70.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = stringResource(R.string.new_post),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp)
                    )
                }
            }
        ) {// Здесь начинается содержимое Scaffold
            // Вы можете добавить любой контент, например:
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center // Центрируем содержимое
            ) {
                Text("Содержимое приложения!", style = MaterialTheme.typography.h6)
            }
        }
    }
}