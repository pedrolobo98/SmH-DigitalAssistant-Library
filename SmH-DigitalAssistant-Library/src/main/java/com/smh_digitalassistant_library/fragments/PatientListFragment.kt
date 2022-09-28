package com.smh_digitalassistant_library.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smh_digitalassistant_library.R
import com.smh_digitalassistant_library.activities.MedicActivity
import com.smh_digitalassistant_library.adapters.PatientListAdapter
import com.smh_digitalassistant_library.models.PatientListViewModel
import com.smh_digitalassistant_library.utils.Constants
import com.smh_digitalassistant_library.utils.Utility.apiget
import kotlinx.android.synthetic.main.fragment_patient_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.ArrayList

class PatientListFragment : Fragment() {
    data class PatientsList(val patientId: Int, val patientName: String, val condition: String, val state: String )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_patient_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.back_button_ps)
        val patientListView = view.findViewById<RecyclerView>(R.id.patient_list_view)
        val data = ArrayList<PatientListViewModel>()
        //val adapter = PatientListAdapter(data, requireContext())
        val medicId = arguments?.getString("MedicId")

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }

        patientListView.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.Main).launch {
            mPatientLoading.visibility = View.VISIBLE
            val ptList = async(Dispatchers.IO) {fetchPatients(medicId!!.toInt())}

            for (i in 0 until ptList.await().size) {
                data.add(
                    PatientListViewModel(
                        R.drawable.ic_baseline_person_24,
                        "Nome: ${ptList.await()[i].patientName}",
                        "ID: ${ptList.await()[i].patientId}",
                        "Patologia: ${ptList.await()[i].condition}",
                        "Estado: ${ptList.await()[i].state}"
                    ))

                val adapter = PatientListAdapter(data, view.context) {pId, pName, pImage ->
                    val act = activity as MedicActivity
                    act.handlePatient(pId, pName, pImage)
                }

                patientListView.adapter = adapter
            }
            mPatientLoading.visibility = View.INVISIBLE
        }
    }

    private fun fetchPatients(medicId: Int): ArrayList<PatientsList> {
        val frame = apiget("${Constants.API_ADDRESS}/patients/$medicId")
        val patientList = ArrayList<PatientsList>()

        if (!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            patientList.ensureCapacity(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                patientList.add(PatientsList(
                    jsonObject.getInt("idpatient"),
                    (jsonObject.getString("firstName").toString() + " " + jsonObject.getString("lastName").toString()),
                    jsonObject.getString("condition").toString(),
                    jsonObject.getString("status")))
            }
        }
        return patientList
    }
}