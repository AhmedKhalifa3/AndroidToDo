package hu.bme.aut.th642y.todolist
import android.content.Intent
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityAddTaskBinding

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Save Task Button Click Listener
        binding.buttonSaveTask.setOnClickListener {
            val taskTitle = binding.editTextTaskTitle.text.toString()
            val taskDescription = binding.editTextTaskDescription.text.toString()

            if (taskTitle.isBlank()) {
                Toast.makeText(this, "Task title cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Return the result to MainActivity
            val resultIntent = Intent().apply {
                putExtra("TASK_TITLE", taskTitle)
                putExtra("TASK_DESCRIPTION", taskDescription)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Close the Add Task Activity
        }
    }
}
