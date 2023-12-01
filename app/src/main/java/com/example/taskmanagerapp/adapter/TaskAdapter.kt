package com.example.taskmanagerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.databinding.TaskItemBinding
import com.example.taskmanagerapp.model.TaskList

class TaskAdapter(private val context: Context, private val listener: ITaskRvAdapter) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val allTasks = ArrayList<TaskList>()

    inner class TaskViewHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateTaskList(newNodeList: List<TaskList>) {
        allTasks.clear()
        allTasks.addAll(newNodeList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount() = allTasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = allTasks[position]

        holder.binding.tvTaskTitle.text = currentTask.titleText
        val dateComponents = currentTask.dateText.split("-")
        val dayOfWeek = dateComponents[0]
        val dayOfMonth = dateComponents[1]
        val month = dateComponents[2]

        holder.binding.tvWeek.text = dayOfWeek
        holder.binding.tvMonth.text = month
        holder.binding.tvDate.text = dayOfMonth
        holder.binding.tvTime.text = currentTask.timeText
        holder.binding.tvMoreTask.setOnClickListener {
            popUpMenu(it, currentTask)
        }

        if (currentTask.statusText) holder.binding.tvStatus.text = "UPCOMING"
        else holder.binding.tvStatus.text = "COMPLETED"
    }

    private fun popUpMenu(view: View, currentTask: TaskList) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.show_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.tasksEdit -> {
                    listener.updateTaskClicked(currentTask)
                    true
                }

                R.id.taskDelete -> {
                    listener.deleteTaskClicked(currentTask)
                    true
                }

                R.id.taskComplete -> {
                    listener.updateTaskCompleted(currentTask)
                    true
                }

                else -> true
            }
        }

        popupMenu.show()
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenu)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(menu, true)
    }
}

interface ITaskRvAdapter {
    fun deleteTaskClicked(task: TaskList)
    fun updateTaskClicked(task: TaskList)
    fun updateTaskCompleted(task: TaskList)
}