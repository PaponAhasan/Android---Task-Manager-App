package com.example.taskmanagerapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.alarm.AlarmItem
import com.example.taskmanagerapp.alarm.AlarmScheduler
import com.example.taskmanagerapp.alarm.AndroidAlarmScheduler
import com.example.taskmanagerapp.database.TaskRoomDatabase
import com.example.taskmanagerapp.databinding.FragmentAddTaskBinding
import com.example.taskmanagerapp.model.TaskList
import com.example.taskmanagerapp.repository.TaskRepository
import com.example.taskmanagerapp.utils.DialogListener
import com.example.taskmanagerapp.utils.ViewUtils
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.example.taskmanagerapp.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

class AddTaskFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val binding by lazy {
        FragmentAddTaskBinding.inflate(layoutInflater)
    }

    private lateinit var taskViewModel: TaskViewModel

    private var timePickerDialog: TimePickerDialog? = null

    private lateinit var alarmScheduler: AndroidAlarmScheduler
    private var alarmItem: AlarmItem? = null

    private var taskId = ""

    private lateinit var todayDate: String
    private lateinit var todayTime: String

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

        //Time & date set
        onDateTimePicker()

        val receivedBundle = arguments
        if (receivedBundle != null) {
            val task = receivedBundle.getSerializable("task", TaskList::class.java)
            binding.etTaskTitle.setText(task?.titleText)
            binding.etTaskDescription.setText(task?.bodyText)
            binding.etTaskEvent.setText(task?.eventText)
            binding.etTaskDate.setText(task?.dateText)
            binding.etTaskTime.setText(task?.timeText)
            taskId = task?.id.toString()
        }

        binding.btnSave.setOnClickListener {
            val taskTitle = binding.etTaskTitle.text.toString()
            val taskDes = binding.etTaskDescription.text.toString()
            val taskEvent = binding.etTaskEvent.text.toString()
            val taskDate = binding.etTaskDate.text.toString()
            val taskTime = binding.etTaskTime.text.toString()

            val insertTask = TaskList(
                taskTitle,
                taskDes,
                taskEvent,
                taskDate,
                taskTime,
                true
            )

            if (taskTitle.isEmpty() || taskDes.isEmpty() || taskDate.isEmpty()
                || taskTime.isEmpty() || taskEvent.isEmpty()
            ) {
                openSeekBar("Text required..")
            } else {
//                val bundle = Bundle()
//                bundle.putSerializable("tasks", insertTask)
//                findNavController().navigate(R.id.action_addTaskFragment_to_tasksFragment, bundle)

                //current Time
                val currentDateTimeString = "$todayDate $todayTime"
                val currentDateFormatter =
                    SimpleDateFormat("EEE-dd-MMM HH:mm a", Locale.getDefault())
                val currentDate = currentDateFormatter.parse(currentDateTimeString)
                val currentSeconds = currentDate?.time?.div(1000)
                //selected Time
                val selectedDateTimeString = "$taskDate $taskTime"
                val selectedDateFormatter =
                    SimpleDateFormat("EEE-dd-MMM HH:mm a", Locale.getDefault())
                val selectedDate = selectedDateFormatter.parse(selectedDateTimeString)
                val selectedSeconds = selectedDate?.time?.div(1000)

                try {
                    val taskTime = selectedSeconds?.minus(currentSeconds!!)
                    Log.d("MainActivity", "Seconds since epoch: $taskTime")
                    //alarm
                    alarmItem = AlarmItem(
                        LocalDateTime.now().plusSeconds(taskTime!!),
                        taskTitle
                    )
                    // Schedule the alarm
                    alarmScheduler = AndroidAlarmScheduler(requireContext())
                    alarmScheduler.schedule(alarmItem!!)
                    // To cancel the alarm
                    // alarmScheduler.cancel(alarmItem)
                } catch (e: ParseException) {
                    Log.e("Alarm", "Error parsing date and time", e)
                }

                val taskDao = TaskRoomDatabase.getDatabase(requireContext()).getTaskDao()
                val repository = TaskRepository(taskDao)
                val taskViewModelFactory = ViewModelFactory(repository)
                taskViewModel = ViewModelProvider(
                    this,
                    taskViewModelFactory
                )[TaskViewModel::class.java]

                if (receivedBundle != null) {
                    insertTask.id = taskId.toInt()
                    taskViewModel.update(insertTask)
                    openSeekBar("Task update..")
                } else {
                    taskViewModel.insert(insertTask)
                    openSeekBar("Task insert..")
                }
                findNavController().navigate(R.id.action_addTaskFragment_to_tasksFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val image = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete)
            val message = "Are your sure? Quit without saving?"
            ViewUtils.viewDialogResponse(
                requireContext(),
                image!!,
                message,
                object :
                    DialogListener {
                    override fun onConfirmed() {
                        findNavController().navigate(R.id.action_addTaskFragment_to_tasksFragment)
                    }

                    override fun onCanceled() {

                    }
                })
        }
    }

    private fun openSeekBar(msg: String) {
        val mySeekBar = Snackbar.make(
            requireActivity().findViewById(R.id.mainLayout),
            msg, Snackbar.LENGTH_LONG
        )
        mySeekBar.show()
    }

    private fun getCurrentDateTime() {

    }

    private fun onDateTimePicker() {
        val today = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE-dd-MMM", Locale.getDefault())

        val time = today.time
        val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())

        todayDate = dateFormat.format(today)
        todayTime = timeFormat.format(time)

        binding.etTaskDate.setText(todayDate)
        binding.etTaskTime.setText(todayTime)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
        datePickerDialog.datePicker.minDate =
            System.currentTimeMillis() - 1000 // Disable past dates

        timePickerDialog = TimePickerDialog(context, this, hour, minute, false)
        timePickerDialog?.updateTime(hour, minute)

        binding.etTaskDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                datePickerDialog.show()
            }
        }

        binding.etTaskTime.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                timePickerDialog?.show()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val myMonth = month + 1

        val selected = Calendar.getInstance()
        selected.set(Calendar.YEAR, year)
        selected.set(Calendar.MONTH, myMonth - 1)
        selected.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val dateFormat = SimpleDateFormat("EEE-dd-MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(selected.time)
        binding.etTaskDate.setText(formattedDate)
        timePickerDialog?.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val selectedTime = timeFormat.format(calendar.time)
        binding.etTaskTime.setText(selectedTime)
    }
}