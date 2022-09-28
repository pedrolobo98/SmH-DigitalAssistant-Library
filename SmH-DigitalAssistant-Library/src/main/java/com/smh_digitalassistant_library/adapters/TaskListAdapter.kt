package com.smh_digitalassistant_library.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smh_digitalassistant_library.R
import com.smh_digitalassistant_library.models.AllTasksViewModel
import com.smh_digitalassistant_library.utils.Utility

class TaskListAdapter(private val mList: List<AllTasksViewModel>, private val listener: (String, String, String) -> Unit): RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_task, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        holder.taskName.text = itemsViewModel.taskName
        holder.itemView.setOnClickListener { view ->
            Utility.preventOverclick(view)
            listener(itemsViewModel.taskId, itemsViewModel.taskName, itemsViewModel.taskDescription)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskNameText)
    }
}