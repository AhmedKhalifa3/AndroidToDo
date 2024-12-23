package hu.bme.aut.th642y.todolist

import java.io.Serializable

data class Task(
    val id: String,
    var title: String,
    var description: String,
    var isCompleted: Boolean = false,
) : Serializable
