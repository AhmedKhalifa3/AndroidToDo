package hu.bme.aut.th642y.todolist
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import java.util.Calendar
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        db = FirebaseFirestore.getInstance()

        checkAndRequestNotificationPermission()
        setDailyReminder(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val (loadedTasks, loadedCompletedTasks) = FileHelper.loadTasksFromFile(this)
        taskList.addAll(loadedTasks)
        SharedData.completedTaskList.addAll(loadedCompletedTasks) // Use the singleton

        // Setup RecyclerView for active tasks
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(this, taskList)
        binding.recyclerViewTasks.adapter = taskAdapter

        // Setup RecyclerView
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(this, taskList)
        binding.recyclerViewTasks.adapter = taskAdapter

        // Set up item click listener for selecting a task
        taskAdapter.onItemClick = { selectedTask ->
            val intent = Intent(this, EditTaskActivity::class.java).apply {
                putExtra("TASK_ID", selectedTask.id)
                putExtra("TASK_TITLE", selectedTask.title)
                putExtra("TASK_DESCRIPTION", selectedTask.description)
            }
            startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)
        }

        // Navigate to Add Task Screen
        binding.buttonAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        binding.buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonCompletedTasks.setOnClickListener {
            val intent = Intent(this, CompletedTasksActivity::class.java).apply {
            }
            startActivity(intent)
        }

        binding.buttonUploadToFirestore.setOnClickListener {
            try {
                uploadTasksToFirestore(db)
            } catch (e: Exception) {
                Log.e("FirestoreUploadError", "Error uploading tasks: ${e.message}")
                Toast.makeText(this, "Error uploading tasks", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun uploadTasksToFirestore(db: FirebaseFirestore) {
        // Upload active tasks
        for (task in taskList) {
            val taskMap = hashMapOf(
                "title" to task.title,
                "description" to task.description,
                "isCompleted" to task.isCompleted
            )

            db.collection("tasks")
                .add(taskMap)
                .addOnSuccessListener { documentReference ->
                    Log.d("FirestoreUpload", "Active task uploaded with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreUploadError", "Error uploading active task: $e")
                }
        }

        // Upload completed tasks
        for (task in SharedData.completedTaskList) {
            val taskMap = hashMapOf(
                "title" to task.title,
                "description" to task.description,
                "isCompleted" to task.isCompleted
            )

            db.collection("completed_tasks")
                .add(taskMap)
                .addOnSuccessListener { documentReference ->
                    Log.d("FirestoreUpload", "Completed task uploaded with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreUploadError", "Error uploading completed task: $e")
                }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ADD_TASK_REQUEST_CODE -> {
                    val taskTitle = data?.getStringExtra("TASK_TITLE") ?: ""
                    val taskDescription = data?.getStringExtra("TASK_DESCRIPTION") ?: ""

                    if (taskTitle.isNotBlank()) {
                        val newTask = Task(id = System.currentTimeMillis().toString(), title = taskTitle, description = taskDescription)
                        taskList.add(newTask)
                        taskAdapter.notifyItemInserted(taskList.size - 1)
                        Toast.makeText(this, "Task added: $taskTitle", Toast.LENGTH_SHORT).show()
                    }
                }
                EDIT_TASK_REQUEST_CODE -> {
                    val taskId = data?.getStringExtra("TASK_ID") ?: return
                    val taskTitle = data.getStringExtra("TASK_TITLE") ?: ""
                    val taskDescription = data.getStringExtra("TASK_DESCRIPTION") ?: ""

                    when (data.getIntExtra("RESULT_CODE", -1)) {
                        RESULT_OK -> {
                            // Update task logic
                            val task = taskList.find { it.id == taskId }
                            task?.apply {
                                title = taskTitle
                                description = taskDescription
                            }
                            taskAdapter.notifyDataSetChanged()
                            Toast.makeText(this, "Task updated: $taskTitle", Toast.LENGTH_SHORT).show()
                        }
                        RESULT_FIRST_USER -> {
                            // Delete task logic
                            val taskIndex = taskList.indexOfFirst { it.id == taskId }
                            if (taskIndex != -1) {
                                taskList.removeAt(taskIndex)
                                taskAdapter.notifyItemRemoved(taskIndex)
                                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                        RESULT_CANCELED -> {
                            // Mark task as completed logic
                            val taskIndex = taskList.indexOfFirst { it.id == taskId }
                            if (taskIndex != -1) {
                                val completedTask = taskList.removeAt(taskIndex)
                                completedTask.isCompleted = true
                                SharedData.completedTaskList.add(completedTask) // Update shared data
                                taskAdapter.notifyItemRemoved(taskIndex)
                                Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val ADD_TASK_REQUEST_CODE = 1
        const val EDIT_TASK_REQUEST_CODE = 2
    }
    override fun onStop() {
        super.onStop()
        // Save tasks to file when the activity is stopped
        FileHelper.saveTasksToFile(this, taskList, SharedData.completedTaskList) // Use shared data
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun setDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(
                context,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Set the alarm time to 9:00 AM every day
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            // Adjust time if it's already past today
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // Set a repeating alarm
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }


    fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }
}
