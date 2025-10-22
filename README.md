# Android To-Do List App

This is an **Android To-Do List Manager** designed to help users organize, track, and manage their tasks efficiently. It provides a feature-rich interface with local and cloud storage, notifications, and task management capabilities.

## Features

### Task Management
- Add, edit, and delete tasks
- Mark tasks as completed
- View active and completed tasks separately

### Persistence
- Saves tasks locally on the device
- Uploads tasks (active and completed) to **Firebase Firestore** for cloud backup

### Notifications & Reminders
- Sets a **daily reminder** alarm at 9:00 AM
- Sends notifications for reminders

### User Interface
- Uses **RecyclerView** for displaying tasks
- Buttons for navigation: Add Task, Settings, Completed Tasks, Upload to Cloud

### Synchronization
- Cloud integration with Firebase ensures tasks are backed up and accessible across devices

## Usage
1. Open the app and grant notification permissions.
2. Add tasks via the "Add Task" button.
3. Edit or delete tasks by tapping on an existing task.
4. Completed tasks are moved to the Completed Tasks section.
5. Upload tasks to Firebase Firestore for backup using the "Upload to Firestore" button.

## Tech Stack
- **Language:** Kotlin
- **Android SDK**
- **Firebase Firestore** for cloud storage
- **RecyclerView** for task display
- **AlarmManager** for daily reminders

## Notes
- Tasks are saved locally when the app is stopped.
- Completed tasks are stored separately from active tasks.
- Notifications require user permission.

## License
This project is licensed under the MIT License.
