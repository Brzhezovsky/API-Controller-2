package dto

data class TaskListResponseDTO(
    val success: Boolean,
    val count: Int,
    val tasks: List<TaskItemDTO>
)