package hu.bme.aut.th642y.todolist

import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

object FileHelper {
    private const val TASKS_FILE_NAME = "tasks.json"
    private const val COMPLETED_TASKS_FILE_NAME = "completed_tasks.json"

    fun saveTasksToFile(context: Context, taskList: List<Task>, completedTaskList: List<Task>) {
        val gson = Gson()

        // Save active tasks
        val activeTasksJson = gson.toJson(taskList)
        val activeTasksFile = File(context.filesDir, TASKS_FILE_NAME)
        try {
            FileWriter(activeTasksFile).use { writer ->
                writer.write(activeTasksJson)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Save completed tasks
        val completedTasksJson = gson.toJson(completedTaskList)
        val completedTasksFile = File(context.filesDir, COMPLETED_TASKS_FILE_NAME)
        try {
            FileWriter(completedTasksFile).use { writer ->
                writer.write(completedTasksJson)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadTasksFromFile(context: Context): Pair<MutableList<Task>, MutableList<Task>> {
        val gson = Gson()
        val activeTasksFile = File(context.filesDir, TASKS_FILE_NAME)
        val completedTasksFile = File(context.filesDir, COMPLETED_TASKS_FILE_NAME)

        val activeTasks = mutableListOf<Task>()
        val completedTasks = mutableListOf<Task>()

        if (activeTasksFile.exists()) {
            try {
                FileReader(activeTasksFile).use { reader ->
                    val tasksArray = gson.fromJson(reader, Array<Task>::class.java)
                    activeTasks.addAll(tasksArray)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (completedTasksFile.exists()) {
            try {
                FileReader(completedTasksFile).use { reader ->
                    val completedTasksArray = gson.fromJson(reader, Array<Task>::class.java)
                    completedTasks.addAll(completedTasksArray)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return Pair(activeTasks, completedTasks)
    }
}
