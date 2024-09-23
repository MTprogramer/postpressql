package DataModel

data class TodoDataModel(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)