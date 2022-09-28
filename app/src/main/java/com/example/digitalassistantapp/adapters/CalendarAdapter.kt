package com.example.digitalassistantapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.models.AppointmentsViewModel

class CalendarAdapter(private val mList: List<AppointmentsViewModel>, private val ctx: Context): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_appointment, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        holder.appointmentName.text = itemsViewModel.appointmentTitle
        holder.appointmentDateTime.text = itemsViewModel.appointmentDate
        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val appointmentName: TextView = itemView.findViewById(R.id.appointmentNameText)
        val appointmentDateTime: TextView = itemView.findViewById(R.id.appointmentDateTime)
    }
}