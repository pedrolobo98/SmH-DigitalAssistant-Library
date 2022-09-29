package com.smh_digitalassistant_library.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.smh_digitalassistant_library.R
import com.smh_digitalassistant_library.adapters.CalendarAdapter
import com.smh_digitalassistant_library.models.AppointmentsViewModel
import com.smh_digitalassistant_library.utils.Constants
import com.smh_digitalassistant_library.utils.Utility
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    data class Appointments(val appointmentTitle: String, val appointmentDateTime: String)
    var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.back_button)
        val patientId = requireArguments().getString("patientID")

        val data = ArrayList<AppointmentsViewModel>()
        appointments.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.Main).launch {
            pCalendarLoading.visibility = View.VISIBLE
            val apList = async(Dispatchers.IO){fetchAppointments(patientId!!)}

            for (i in 0 until apList.await().size){
                data.add(
                    AppointmentsViewModel(
                        apList.await()[i].appointmentTitle,
                        handleDateTime(apList.await()[i].appointmentDateTime)
                    ))
                val adapter = CalendarAdapter(data, view.context)
                appointments.adapter = adapter
            }
            pCalendarLoading.visibility = View.GONE
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        appointmentDrawer.setOnClickListener {  }
        calendarWidget.setOnDateChangeListener { view, year, month, dayOfMonth ->

        }
    }

    private fun handleDateTime(appointmentDateTime: String): String {
        Log.d("DATE_TIME", appointmentDateTime)
        sdf.timeZone = TimeZone.getTimeZone("Lisbon/Portugal")
        val datetime = sdf.parse(appointmentDateTime)

        return DateFormat.getDateInstance(DateFormat.FULL).format(datetime) + " às " +
                DateFormat.getTimeInstance(DateFormat.SHORT).format(datetime).toString()
    }

    private fun fetchAppointments(id: String): ArrayList<Appointments> {
        val frame = Utility.apiget("${Constants.API_ADDRESS}/appointments/$id")
        val appointmentList = ArrayList<Appointments>()

        if (!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            appointmentList.ensureCapacity(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                appointmentList.add(Appointments(
                    jsonObject.getString("eventname"),
                    jsonObject.getString("datetime")
                )
                )
            }
        }
        return appointmentList
    }

}