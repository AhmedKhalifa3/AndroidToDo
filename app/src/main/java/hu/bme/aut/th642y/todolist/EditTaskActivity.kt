package hu.bme.aut.th642y.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityEditTaskBinding

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the task details passed through the Intent
        val taskId = intent.getStringExtra("TASK_ID") ?: ""
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: ""
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION") ?: ""

        // Populate the fields with the current task data
        binding.editTextTaskTitle.setText(taskTitle)
        binding.editTextTaskDescription.setText(taskDescription)

        // Update Task Button Click Listener
        binding.buttonUpdateTask.setOnClickListener {
            val updatedTitle = binding.editTextTaskTitle.text.toString()
            val updatedDescription = binding.editTextTaskDescription.text.toString()

            if (updatedTitle.isBlank()) {
                Toast.makeText(this, "Task title cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create an Intent to send the updated task data back to MainActivity
            val resultIntent = Intent().apply {
                putExtra("TASK_ID", taskId)
                putExtra("TASK_TITLE", updatedTitle)
                putExtra("TASK_DESCRIPTION", updatedDescription)
                putExtra("RESULT_CODE", RESULT_OK)
            }
            setResult(RESULT_OK, resultIntent)
            finish() // Close the activity
        }

        // Delete Task Button Click Listener
        // Delete Task Button Click Listener in EditTaskActivity
        binding.buttonDeleteTask.setOnClickListener {
            // Send the task ID back to MainActivity for deletion
            val resultIntent = Intent().apply {
                putExtra("TASK_ID", taskId)
                putExtra("RESULT_CODE", RESULT_FIRST_USER)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.buttonMarkAsCompleted.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("TASK_ID", taskId)
                putExtra("RESULT_CODE", RESULT_CANCELED) // Unique result code for marking as completed
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }


    }
}
