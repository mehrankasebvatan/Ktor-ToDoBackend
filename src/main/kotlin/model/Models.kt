package com.example

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val username: String,
    val password: String
)


@Serializable
data class UserId(
    val userId: Int
)

@Serializable
data class IdModel(
    val id: Int
)

@Serializable
data class TaskDTO(
    val userId: Int,
    val id: Int,
    val title: String,
    val content: String,
    val lastModifiedDate: String,
)

@Serializable
data class BaseResponse(
    val code: Int? = -1,
    val message: String? = "",
    val data: List<TaskDTO>? = emptyList()
)