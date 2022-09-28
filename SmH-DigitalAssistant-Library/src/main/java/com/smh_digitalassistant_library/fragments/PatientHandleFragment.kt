package com.smh_digitalassistant_library.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smh_digitalassistant_library.R
import com.smh_digitalassistant_library.adapters.CalendarAdapter
import com.smh_digitalassistant_library.adapters.DailyTasksAdapter
import com.smh_digitalassistant_library.adapters.TaskListAdapter
import com.smh_digitalassistant_library.adapters.TasksPlanAdapter
import com.smh_digitalassistant_library.models.AllTasksViewModel
import com.smh_digitalassistant_library.models.AppointmentsViewModel
import com.smh_digitalassistant_library.models.DailyTasksViewModel
import com.smh_digitalassistant_library.utils.Constants
import com.smh_digitalassistant_library.utils.Utility.apiget
import com.smh_digitalassistant_library.utils.Utility.preventOverclick
import im.dacer.androidcharts.LineView
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_patient_handle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import lecho.lib.hellocharts.model.PointValue
import org.json.JSONArray
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PatientHandleFragment : Fragment() {
    data class PatientsInfo(val condition: String, val state: String)
    data class PatientTaskInfo(val taskId: Int, val taskName: String, val score: String, val numQuestions: Int, val lastScore: String, val isCompleted: Boolean)
    data class PatientAppointments(val appointmentTitle: String, val appointmentDateTime: String)
    data class AvailableTasksPH(val taskId: String, val taskname: String, val taskDesc: String)
    var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val size: ArrayList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_patient_handle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val patientId = requireArguments().getString("patientID")
        val patientName = requireArguments().getString("patientName")
        val userId = requireArguments().getString("userId")
        val patientPic = requireArguments().getInt("patientPic")

        var registerPage = false
        var planPage = false
        mainBody.visibility = View.VISIBLE
        registerBody.visibility = View.GONE
        editTreatmentBody.visibility = View.GONE

        val dataCondition = ArrayList<String>()
        val dataStatus = ArrayList<String>()
        val dataScore: ArrayList<String> = ArrayList()
        val dataLastScore: ArrayList<String> = ArrayList()
        val dataName: ArrayList<String> = ArrayList()
        val data = ArrayList<AppointmentsViewModel>()
        val data2 = ArrayList<DailyTasksViewModel>()
        val allTasks = ArrayList<AllTasksViewModel>()
        val items: MutableList<String> = ArrayList()

        phAppointments.layoutManager = LinearLayoutManager(context)
        phTreatment.layoutManager = LinearLayoutManager(context)
        editTreatmentItems.layoutManager = LinearLayoutManager(context)

        //Coroutine to load general status
        CoroutineScope(Dispatchers.Main).launch {
            mHandlePatientLoading.visibility = View.VISIBLE
            val ptInfo1 = async(Dispatchers.IO) {fetchPatientInfo(patientId!!.split(" ")[1].toInt())}
            for (i in 0 until ptInfo1.await().size) {
                dataCondition.add("Condição: " + ptInfo1.await()[i].condition)
                dataStatus.add("Estado: " + ptInfo1.await()[i].state)

            }
            textViewPatientCondition.text = dataCondition[0]
            textViewPatientStatus.text = dataStatus[0]

            mHandlePatientLoading.visibility = View.INVISIBLE
        }

        textViewPatientAge.text = patientId
        textViewPatientName.text = patientName

        val values: MutableList<PointValue> = ArrayList()
        values.add(PointValue(0f, 2f))
        values.add(PointValue(1f, 4f))
        values.add(PointValue(2f, 3f))
        values.add(PointValue(3f, 4f))

        if (patientPic != null) {
            cardPatientPicture.setImageResource(patientPic)
        } else {
            cardPatientPicture.setImageResource(R.drawable.ic_baseline_person_24)
        }

        back_button_ps.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }

        //Coroutine to load appointments
        CoroutineScope(Dispatchers.Main).launch {
            mHandlePatientLoading.visibility = View.VISIBLE
            val apInfo = async(Dispatchers.IO) {fetchAppointments(patientId!!.split(" ")[1].toInt())}
            for (i in 0 until apInfo.await().size){
                data.add(
                    AppointmentsViewModel(
                        apInfo.await()[i].appointmentTitle,
                        handleDateTime(apInfo.await()[i].appointmentDateTime)
                    )
                )
                val adapter = CalendarAdapter(data, view.context)
                phAppointments.adapter = adapter
            }
            mHandlePatientLoading.visibility = View.INVISIBLE
        }

        cardview_patient_handle.setOnClickListener {

        }

        //Edição do plano de tratamento
        buttonTreatmentPlan.setOnClickListener {
            if(!planPage) {
                if (registerPage) {
                    items.clear()
                    registerBody.visibility = View.GONE
                    registerPage = false
                    buttonTreatmentReg.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_teal))
                }
                val adapter = TasksPlanAdapter(data2){ position, task ->
                    //Toast.makeText(requireContext(), "Eliminar tarefa ${position + 1}", Toast.LENGTH_SHORT).show()
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Deseja eliminar a tarefa?")
                        .setNegativeButton("Não"){_, _ ->
                        }
                        .setPositiveButton("Sim"){_, _ ->
                            data2.removeAt(position)
                            editTreatmentItems.adapter?.notifyItemRemoved(position)
                        }
                        .show()
                }
                editTreatmentItems.adapter = adapter
                mainBody.visibility = View.GONE
                editTreatmentBody.visibility = View.VISIBLE
                planPage = true
                buttonTreatmentPlan.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.medical_green))
            } else {
                editTreatmentBody.visibility = View.GONE
                mainBody.visibility = View.VISIBLE
                planPage = false
                buttonTreatmentPlan.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_teal))
            }

            //Abre container de seleção de tarefas na edição do tratamento
            editTreatmentBody.setOnClickListener {
                allTasks.clear()
                preventOverclick(view)
                val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

                val bottomSheetView = LayoutInflater.from(requireContext()).inflate(
                    R.layout.bottom_sheet,
                    view.findViewById<View>(R.id.bottom_sheet) as LinearLayout?
                )

                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetView.bottomSheetTaskContainer.layoutManager = LinearLayoutManager(context)
                //Coroutine to load all tasks
                CoroutineScope(Dispatchers.Main).launch {
                    val atList = async(Dispatchers.IO) {fetchAllTasks()}

                    for (i in 0 until atList.await().size) {
                        allTasks.add(
                            AllTasksViewModel(
                                atList.await()[i].taskId,
                                atList.await()[i].taskname,
                                atList.await()[i].taskDesc
                            ))
                        val adapter = TaskListAdapter(allTasks){taskId, taskName, _ ->
                            Toast.makeText(requireContext(), "Adicionada Tarefa $taskId", Toast.LENGTH_SHORT).show()
                            data2.add(DailyTasksViewModel(
                                "Tarefa:",
                                "Questionário $taskName",
                                taskConfirm = false,
                                taskQuestionnaire = true
                            ))
                            editTreatmentItems.adapter?.notifyDataSetChanged() //Alterar futuramente
                        }
                        bottomSheetView.bottomSheetTaskContainer.adapter = adapter
                    }
                }
                bottomSheetDialog.show()
            }
        }

        //Coroutine to load task scores and task information
        CoroutineScope(Dispatchers.Main).launch {
            val tInfo = async(Dispatchers.IO) {fetchPatientTaskInfo(patientId!!.split(" ")[1].toInt())}
            for (i in 0 until tInfo.await().size) {
                dataScore.add(tInfo.await()[i].score)
                dataName.add(tInfo.await()[i].taskName)
                size.add(tInfo.await()[i].numQuestions)
                dataLastScore.add(tInfo.await()[i].lastScore)
                data2.add(
                    DailyTasksViewModel(
                        "Tarefa:",
                        tInfo.await()[i].taskName,
                        tInfo.await()[i].isCompleted,
                        true
                    )
                )
                val adapter = DailyTasksAdapter(data2, requireContext(), patientId!!.split(" ")[1]){_, _, _ ->

                }
                phTreatment.adapter = adapter
            }
        }

        menu_questionnaires.editText?.doOnTextChanged { text, _, _, _ ->
            val selected = dataName.indexOf(text.toString())
            val score = dataScore[selected].split(",")
            initLineView(line_view, size[selected])
            setUpdateChart(line_view, score)
            text_last_score_mpr.text = dataLastScore[selected]
        }

        //Registo do tratamento
        buttonTreatmentReg.setOnClickListener {
            if (!registerPage) {
                if (planPage){
                    editTreatmentBody.visibility = View.GONE
                    planPage = false
                    buttonTreatmentPlan.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_teal))
                }
                for (elem in dataName) {
                    items.add(elem)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                (menu_questionnaires.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                initLineView(line_view, size[0])
                mainBody.visibility = View.GONE
                registerBody.visibility = View.VISIBLE
                registerPage = true
                buttonTreatmentReg.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.medical_green))
            } else {
                items.clear()
                registerBody.visibility = View.GONE
                mainBody.visibility = View.VISIBLE
                registerPage = false
                buttonTreatmentReg.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_teal))
            }
        }
    }

    private fun fetchPatientInfo(patientId: Int): ArrayList<PatientsInfo> {
        val frame = apiget("${Constants.API_ADDRESS}/patientpid/$patientId")
        val patientInfo = ArrayList<PatientsInfo>()

        if (!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            patientInfo.ensureCapacity(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                patientInfo.add(
                    PatientsInfo(
                        jsonObject.getString("condition").toString(),
                        jsonObject.getString("status")
                    )
                )
            }
        }
        return patientInfo
    }

    private fun fetchPatientTaskInfo(patientId: Int): ArrayList<PatientTaskInfo> {
        val frame = apiget("${Constants.API_ADDRESS}/tasks/$patientId")
        val taskInfo = ArrayList<PatientTaskInfo>()

        if (!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            taskInfo.ensureCapacity(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                taskInfo.add(
                    PatientTaskInfo(
                        jsonObject.getInt("taskId"),
                        jsonObject.getString("taskName"),
                        jsonObject.getString("score"),
                        jsonObject.getInt("numStg"),
                        jsonObject.getString("scoreSingle"),
                        jsonObject.getInt("isCompleted") != 0
                    )
                )
            }
        }
        return taskInfo
    }

    private fun fetchAppointments(patientId: Int): ArrayList<PatientAppointments> {
        val frame = apiget("${Constants.API_ADDRESS}/appointments/$patientId")
        val appointmentList = ArrayList<PatientAppointments>()

        if (!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            appointmentList.ensureCapacity(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                appointmentList.add(
                    PatientAppointments(
                        jsonObject.getString("eventname"),
                        jsonObject.getString("datetime")
                    )
                )
            }
        }
        return appointmentList
    }

    private fun fetchAllTasks(): ArrayList<AvailableTasksPH> {
        val frame = apiget("${Constants.API_ADDRESS}/task_list")
        val taskList = ArrayList<AvailableTasksPH>()

        if (!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            taskList.ensureCapacity(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                taskList.add(
                    AvailableTasksPH(
                        jsonObject.getString("taskId").toString(),
                        jsonObject.getString("taskName").toString(),
                        jsonObject.getString("taskDescription").toString()
                    )
                )
            }
        }

        return taskList
    }

    private fun initLineView(lineView: LineView, size: Int) {
        val test = ArrayList<String>()
        for (i in 0 until size) {
            test.add((i + 1).toString())
        }
        lineView.setBottomTextList(test)
        lineView.setColorArray(
            intArrayOf(
                Color.parseColor("#2196F3"), Color.parseColor("#9C27B0"),
                Color.parseColor("#F44336"), Color.parseColor("#009688")
            )
        )
        lineView.setDrawDotLine(true)
        lineView.setShowPopup(LineView.SHOW_POPUPS_NONE)
    }

    private fun setUpdateChart(lineView: LineView, score: List<String>) {
        val dataList: ArrayList<Int> = ArrayList()
        for (element in score) {
            dataList.add(element.toInt())
        }

        val dataLists: ArrayList<ArrayList<Int>> = ArrayList()
        dataLists.add(dataList)
        lineView.setDataList(dataLists)
    }

    private fun handleDateTime(appointmentDateTime: String): String {
        sdf.timeZone = TimeZone.getTimeZone("Lisbon/Portugal")
        val datetime = sdf.parse(appointmentDateTime)

        return DateFormat.getDateInstance(DateFormat.FULL).format(datetime) + " às " +
                DateFormat.getTimeInstance(DateFormat.SHORT).format(datetime).toString()
    }

}