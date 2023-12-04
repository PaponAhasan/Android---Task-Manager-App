package com.example.taskmanagerapp.fragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.adapter.ITaskRvAdapter
import com.example.taskmanagerapp.adapter.TaskAdapter
import com.example.taskmanagerapp.alarm.notification.ActionStopReceiver
import com.example.taskmanagerapp.alarm.notification.InAppNotification
import com.example.taskmanagerapp.database.TaskRoomDatabase
import com.example.taskmanagerapp.databinding.FragmentTasksBinding
import com.example.taskmanagerapp.model.TaskList
import com.example.taskmanagerapp.repository.TaskRepository
import com.example.taskmanagerapp.utils.DialogListener
import com.example.taskmanagerapp.utils.ViewUtils
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.example.taskmanagerapp.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class TasksFragment : Fragment(), DatePickerDialog.OnDateSetListener, ITaskRvAdapter {

    private val binding by lazy {
        FragmentTasksBinding.inflate(layoutInflater)
    }
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var inCompleteTasks: ArrayList<TaskList>
    private var isCompleted = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()

        //open calendar
        binding.ivCalander.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
            datePickerDialog.show()
        }

        //go for the add task
        binding.fabAdTask.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment_to_addTaskFragment)
        }
        //open popup menu for edit, delete
        binding.ivMenu.setOnClickListener {
            popUpMenu(it)
        }

        //get data from database- room
        val taskDao = TaskRoomDatabase.getDatabase(requireContext()).getTaskDao()
        val repository = TaskRepository(taskDao)
        val taskViewModelFactory = ViewModelFactory(repository)
        taskViewModel = ViewModelProvider(
            this,
            taskViewModelFactory
        )[TaskViewModel::class.java]

        //setup recycler view
        adapter = TaskAdapter(requireContext(), this)
        binding.tasksRecyclerView.adapter = adapter
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)

        taskViewModel.getAllTasks().observe(viewLifecycleOwner) { tasks ->
            inCompleteTasks = arrayListOf()
            tasks?.let {
                //get InComplete Tasks List
                getInCompleteTasks(it)
                //for recycler view show InComplete tasks
                adapter.updateTaskList(inCompleteTasks)
            }
            //if tasks list all data empty show animation image
            onHandleLottieAnimationView(tasks)
        }
        //for back stack
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

    private fun getInCompleteTasks(tasks: List<TaskList>) {
        tasks.forEach {
            // if statusText is true, task is InCompleted
            if (it.statusText) {
                inCompleteTasks.add(it)
            } else isCompleted++
        }
    }

    private fun onHandleLottieAnimationView(tasks: List<TaskList>) {
        // if isCompleted is equal the tasksList size then
        // all tasks list are Completed task
        if (isCompleted == tasks.size) {
            binding.lottieAnimationView.visibility = View.VISIBLE
        } else binding.lottieAnimationView.visibility = View.GONE
    }

    override fun deleteTaskClicked(task: TaskList) {
        //show dialog before deleting the task
        val image = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete)
        val message = "Are your sure deleted this task?"
        ViewUtils.viewDialogResponse(
            requireContext(),
            image!!,
            message,
            object :
                DialogListener {
                override fun onConfirmed() {
                    //delete task
                    taskViewModel.delete(task)
                    //stop alarm
                    val actionIntent = Intent(context, ActionStopReceiver::class.java).apply {
                        action = InAppNotification.ACTION_STOP
                        putExtra("alarmItem", task)
                    }
                    context?.sendBroadcast(actionIntent)
                    //sms show
                    openSeekBar("Task Deleted...")
                }
                override fun onCanceled() {}
            })
    }

    override fun updateTaskClicked(task: TaskList) {
        val bundle = Bundle()
        bundle.putSerializable("task", task)
        findNavController().navigate(R.id.action_tasksFragment_to_addTaskFragment, bundle)
    }

    override fun updateTaskCompleted(task: TaskList) {
        //stop alarm
        val actionIntent = Intent(context, ActionStopReceiver::class.java).apply {
            action = InAppNotification.ACTION_STOP
            putExtra("alarmItem", task)
        }
        context?.sendBroadcast(actionIntent)

        val insertTask = TaskList(
            task.titleText,
            task.bodyText,
            task.eventText,
            task.dateText,
            task.timeText,
            false
        )
        //show dialog sms
        val image = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_success)
        val message = "Are your sure you want to mark this task as completed"
        ViewUtils.viewDialogResponse(
            requireContext(),
            image!!,
            message,
            object :
                DialogListener {
                override fun onConfirmed() {
                    insertTask.id = task.id
                    taskViewModel.update(insertTask)
                    openSeekBar("Successfully task completed...")
                }
                override fun onCanceled() {}
            })
    }

    private fun popUpMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.top_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.completedFragment -> {
                    findNavController().navigate(R.id.action_tasksFragment_to_completedFragment)
                    return@setOnMenuItemClickListener true
                }
                R.id.actonDelete -> {
                    val image =
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete)
                    val message = "Are your sure clear all task!!"
                    ViewUtils.viewDialogResponse(
                        requireContext(),
                        image!!,
                        message,
                        object :
                            DialogListener {
                            override fun onConfirmed() {
                                taskViewModel.deleteAllTasks()
                                binding.lottieAnimationView.visibility = View.VISIBLE
                                openSeekBar("Task Clear...")
                            }

                            override fun onCanceled() {

                            }
                        })
                    return@setOnMenuItemClickListener true
                }

                else -> true
            }
        }
        popupMenu.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        openSeekBar("$dayOfMonth-$month-$year")
    }
    private fun openSeekBar(msg: String) {
        val mySeekBar = Snackbar.make(
            requireActivity().findViewById(R.id.mainLayout),
            msg, Snackbar.LENGTH_LONG
        )
        mySeekBar.show()
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                101
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //..
            } else {
                checkPermission()
            }
        }
    }
    companion object {
        const val TAG = "TasksFragment"
    }
}