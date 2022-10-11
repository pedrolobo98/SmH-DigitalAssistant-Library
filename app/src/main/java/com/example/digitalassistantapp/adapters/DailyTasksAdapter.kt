package com.example.digitalassistantapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.models.DailyTasksViewModel
import com.example.digitalassistantapp.utils.Utility

class DailyTasksAdapter(private val mList: List<DailyTasksViewModel>, private val ctx: Context, private val senderId: String, private val listener: (Int, String, Boolean) -> Unit) : RecyclerView.Adapter<DailyTasksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_dailytasks, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        holder.taskNumber.text = itemsViewModel.taskNum
        holder.taskDescription.text = itemsViewModel.taskDesc
        holder.taskConfirmation.isChecked = itemsViewModel.taskConfirm
        holder.itemView.setOnClickListener { view ->
            Utility.preventOverclick(view)
            if (itemsViewModel.taskQuestionnaire) {
                listener(position, itemsViewModel.taskDesc, itemsViewModel.taskConfirm)
            } else {
                Toast.makeText(ctx, "Exercício ${position + 1} clicado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNumber: TextView = itemView.findViewById(R.id.taskNumberText)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescriptionText)
        val taskConfirmation: CheckBox = itemView.findViewById(R.id.taskConfirmation)
    }
}