package dto

data class ResponseTaskDTO (
    val success: Boolean,
    val message: String,
    val task: String
)