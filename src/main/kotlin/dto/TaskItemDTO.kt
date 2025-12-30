package dto

data class TaskItemDTO(
    val id: Int,
    val text: String,
    val createdAt: String,
    val isCompleted: Boolean
)