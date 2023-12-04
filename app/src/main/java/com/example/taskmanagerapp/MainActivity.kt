package com.example.taskmanagerapp

import android.app.NotificationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taskmanagerapp.alarm.notification.InAppNotification
import com.example.taskmanagerapp.alarm.sound.MediaPlayerService
import com.example.taskmanagerapp.fragments.AddTaskFragment
import com.example.taskmanagerapp.model.TaskList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //clear notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(InAppNotification.NOTIFICATION_ID)
        //stop a sound
        val serviceIntent = Intent(this, MediaPlayerService::class.java)
        stopService(serviceIntent)

        // Check if the intent has the extra indicating the fragment to open
        val fragmentToOpen = intent.getStringExtra("FRAGMENT_KEY")
        if (fragmentToOpen != null) {
            // Open the corresponding fragment
            when (fragmentToOpen) {
                "EditFragment" -> {
                    val task = intent.getSerializableExtra("DATA_KEY") as TaskList

                    val bundle = Bundle()
                    bundle.putSerializable("task", task)

                    // Replace the container with AddTaskFragment
                    val addTaskFragment = AddTaskFragment()
                    addTaskFragment.arguments = bundle

                    /*supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, addTaskFragment)
                        .commit()*/
                }
            }
        }
    }
}