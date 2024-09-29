package com.github.aceton41k.simpleapp.model

data class Post(
    val id: Long,
    val title: String,
    val message: String,
    val createdAt: String?,
    val createdBy: Long?,
    val updatedAt: String?,
    val modifiedBy: Long?
)