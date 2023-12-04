package com.example.taskmanagerapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.adapter.ITaskRvAdapter
import com.example.taskmanagerapp.adapter.TaskAdapter
import com.example.taskmanagerapp.database.TaskRoomDatabase
import com.example.taskmanagerapp.databinding.FragmentCompletedBinding
import com.example.taskmanagerapp.model.TaskList
import com.example.taskmanagerapp.repository.TaskRepository
import com.example.taskmanagerapp.utils.DialogListener
import com.example.taskmanagerapp.utils.ViewUtils
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.example.taskmanagerapp.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class CompletedFragment : Fragment(), DatePickerDialog.OnDateSetListener, ITaskRvAdapter {

    private val binding by lazy {
        FragmentCompletedBinding.inflate(layoutInflater)
    }
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var completeTasks: ArrayList<TaskList>
    private var isCompleted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivCalander.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
            datePickerDialog.show()
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_completedFragment_to_tasksFragment)
        }

        val taskDao = TaskRoomDatabase.getDatabase(requireContext()).getTaskDao()
        val repository = TaskRepository(taskDao)
        val taskViewModelFactory = ViewModelFactory(repository)
        taskViewModel = ViewModelProvider(
            this,
            taskViewModelFactory
        )[TaskViewModel::class.java]

        adapter = TaskAdapter(requireContext(), this)
        binding.tasksRecyclerView.adapter = adapter

        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)

        taskViewModel.getAllTasks().observe(viewLifecycleOwner) { tasks ->
            completeTasks = arrayListOf()
            tasks?.let {
                //get Complete Tasks List
                getInCompleteTasks(it)
                //for recycler view show Complete tasks
                adapter.updateTaskList(completeTasks)
            }
            //if tasks list all data empty show animation image
            onHandleLottieAnimationView()
        }
    }

    private fun getInCompleteTasks(tasks: List<TaskList>) {
        tasks.forEach {
            // if statusText is false, task is Completed
            if (!it.statusText) {
                completeTasks.add(it)
                //if isCompleted is true then tasksList have at
                // least one Completed tasks
                isCompleted = true
            }
        }
    }

    private fun onHandleLottieAnimationView() {
        //if isCompleted is true then tasksList have at
        // least one Completed tasks
        if (isCompleted) {
            binding.lottieAnimationView.visibility = View.GONE
        } else binding.lottieAnimationView.visibility = View.VISIBLE
    }

    override fun deleteTaskClicked(task: TaskList) {
        val image = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete)
        val message = "Are your sure deleted this task?"
        ViewUtils.viewDialogResponse(
            requireContext(),
            image!!,
            message,
            object :
                DialogListener {
                override fun onConfirmed() {
                    taskViewModel.delete(task)
                    openSeekBar("Task Deleted...")
                }

                override fun onCanceled() {

                }
            })
    }

    override fun updateTaskClicked(task: TaskList) {
        openSeekBar("You already this task completed")
    }

    override fun updateTaskCompleted(task: TaskList) {
        openSeekBar("You already this task completed")
    }

    private fun openSeekBar(msg: String) {
        val mySeekBar = Snackbar.make(
            requireActivity().findViewById(R.id.mainLayout),
            msg, Snackbar.LENGTH_LONG
        )
        mySeekBar.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        openSeekBar("$dayOfMonth-$month-$year")
    }
}