package com.github.aceton41k.simpleapp.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

data class Post(
    val id: Int,
    val title: String,
    val message: String,
    val createdAt: String?
)

fun formatDateTime(dateTimeString: String?): String {
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