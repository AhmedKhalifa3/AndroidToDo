package hu.bme.aut.th642y.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityCompletedTasksBinding

class CompletedTasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompletedTasksBinding
    private lateinit var completedTaskAdapter: TaskAdapter
    private var selectedTask: Task? = null // Variable to store the selected task for deletion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load completed tasks from storage
        val completedTaskList = SharedData.completedTaskList

        binding.recyclerViewCompletedTasks.layoutManager = LinearLayoutManager(this)
        completedTaskAdapter = TaskAdapter(this, completedTaskList)
        binding.recyclerViewCompletedTasks.adapter = completedTaskAdapter

        // Set up item click listener for selecting a task
        completedTaskAdapter.onItemClick = { task ->
            selectedTask = task // Store the clicked task as the selected task
            Toast.makeText(this, "Task selected: ${task.title}", Toast.LENGTH_SHORT).show()
        }

        // Delete Permanently Button Click Listener
        binding.buttonDeletePermanently.setOnClickListener {
            selectedTask?.let { task ->
                showDeleteConfirmationDialog(task, completedTaskList)
            } ?: run {
                Toast.makeText(this, "No task selected for deletion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(task: Task, completedTaskList: MutableList<Task>) {
        // Confirm deletion before removing a task
        val taskIndex = completedTaskList.indexOf(task)
        if (taskIndex != -1) {
            completedTaskList.removeAt(taskIndex)
            completedTaskAdapter.notifyItemRemoved(taskIndex)
            Toast.makeText(this, "Task deleted permanently", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun saveCompletedTasks() {
//        // Implement code to save the completed task list to a file or SharedPreferences
//        FileHelper.saveTasksToFile(this, taskList, completedTaskList)
//    }
}
