package hu.bme.aut.th642y.todolist

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.todolist.databinding.ActivitySettingsBinding
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import android.content.Intent
import android.provider.Settings



class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        // Load the current theme setting
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)
        binding.switchTheme.isChecked = isDarkMode
        applyTheme(isDarkMode)

        // Handle Theme Switch Toggle
        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            saveThemePreference(isChecked)
            applyTheme(isChecked)
        }

        val buttonToggleNotifications: Button = binding.buttonToggleNotifications
        buttonToggleNotifications.setOnClickListener {
            val areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled()
            if (areNotificationsEnabled) {
                Toast.makeText(this, "Notifications are already enabled", Toast.LENGTH_SHORT).show()
            } else {
                // Direct the user to app notification settings
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
                Toast.makeText(this, "Please enable notifications in your settings", Toast.LENGTH_LONG).show()
            }
        }

        val buttonDisableNotifications: Button = binding.buttonDisableNotifications
        buttonDisableNotifications.setOnClickListener {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Toast.makeText(this, "Notifications are already disabled", Toast.LENGTH_SHORT).show()
            } else {
                // Redirect to app notification settings for disabling notifications
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
                Toast.makeText(this, "Please disable notifications in your settings", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("DARK_MODE", isDarkMode)
        editor.apply()
    }

    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
