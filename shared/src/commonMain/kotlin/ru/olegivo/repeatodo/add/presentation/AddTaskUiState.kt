package ru.olegivo.repeatodo.add.presentation

data class AddTaskUiState(
    val title: String = "",
    val isLoading: Boolean = false,
    val isAdded: Boolean = false,
) {
    val canAdd = title.isNotBlank() && !isLoading && !isAdded
}
