package com.smh_digitalassistant_library.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smh_digitalassistant_library.R
import com.smh_digitalassistant_library.models.PatientListViewModel
import com.smh_digitalassistant_library.utils.Utility

class PatientListAdapter(private val mList: List<PatientListViewModel>, private val ctx: Context, private val listener: (String, String, Int) -> Unit): RecyclerView.Adapter<PatientListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_patient, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        holder.imageView.setImageResource(itemsViewModel.image)
        holder.textViewName.text = itemsViewModel.textName
        holder.textViewID.text = itemsViewModel.textID
        holder.textViewPathology.text = itemsViewModel.textPathology
        holder.textViewState.text = itemsViewModel.textState
        holder.itemView.setOnClickListener { view ->
            Utility.preventOverclick(view)
            listener(itemsViewModel.textID, itemsViewModel.textName, itemsViewModel.image)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.cardPatientPicture)
        val textViewName: TextView = itemView.findViewById(R.id.cardPatientName)
        val textViewID: TextView = itemView.findViewById(R.id.cardPatientID)
        val textViewPathology: TextView = itemView.findViewById(R.id.cardPatientPathology)
        val textViewState: TextView = itemView.findViewById(R.id.cardPatientState)
    }

}