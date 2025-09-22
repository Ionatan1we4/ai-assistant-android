package com.app.assistant.util

sealed class LockState {
    object None : LockState() // Default state when no task is locked

    data class LockAlarm(
        var day: String? = null, // Already processed day (e.g., "tomorrow")
        var time: String? = null // Time to be provided
    ) : LockState()

    data class LockReminder(
        var day: String? = null, // Already processed day (e.g., "tomorrow")
        var time: String? = null, // Time to be provided
        var context: String? = null // Message for reminder
    ) : LockState()

    object LockNavigation : LockState() // No additional data needed

    // Add more categories here as needed
}
