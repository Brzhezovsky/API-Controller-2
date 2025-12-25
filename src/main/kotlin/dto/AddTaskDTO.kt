package dto

import jakarta.validation.constraints.Size

data class AddTaskDTO(
    @Size(min=2, max=30)
    val text: String
)
