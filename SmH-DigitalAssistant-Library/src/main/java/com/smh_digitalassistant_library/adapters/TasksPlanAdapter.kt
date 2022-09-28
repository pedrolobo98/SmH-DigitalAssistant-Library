package com.smh_digitalassistant_library.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smh_digitalassistant_library.R
import com.smh_digitalassistant_library.models.DailyTasksViewModel
import com.smh_digitalassistant_library.utils.Utility

class TasksPlanAdapter(private val mList: List<DailyTasksViewModel>, private val listener: (Int, String) -> Unit): RecyclerView.Adapter<TasksPlanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_tasksplan, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        holder.taskNumberText.text = itemsViewModel.taskNum
        holder.taskDescriptionText.text = itemsViewModel.taskDesc
        holder.deleteTask.setOnClickListener { view ->
            Utility.preventOverclick(view)
            listener(position, itemsViewModel.taskDesc)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNumberText: TextView = itemView.findViewById(R.id.taskNumberTextE)
        val taskDescriptionText: TextView = itemView.findViewById(R.id.taskDescriptionTextE)
        val deleteTask: Button = itemView.findViewById(R.id.deleteTask)
    }
}