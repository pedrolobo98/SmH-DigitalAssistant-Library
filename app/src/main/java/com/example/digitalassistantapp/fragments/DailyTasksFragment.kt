package com.example.digitalassistantapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.activities.PatientMode
import com.example.digitalassistantapp.adapters.DailyTasksAdapter
import com.example.digitalassistantapp.models.DailyTasksViewModel
import com.example.digitalassistantapp.utils.Utility.apiget
import com.example.digitalassistantapp.utils.Constants.API_ADDRESS
import kotlinx.android.synthetic.main.fragment_daily_tasks.*
import kotlinx.coroutines.*
import org.json.JSONArray

class DailyTasksFragment : Fragment() {
    data class DailyTask(val taskId: String, val taskname: String, val isConfirmed: Boolean, val isQuestionnaire: Boolean)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_daily_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.back_button)
        val dailyTaskView = view.findViewById<RecyclerView>(R.id.taskListView)
        val data = ArrayList<DailyTasksViewModel>()

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }

        dailyTaskView.layoutManager = LinearLayoutManager(context)

        val patientId = arguments?.getString("patientID")
        if (!patientId.isNullOrEmpty()){
            //Log.d("ARGUMENT_PATIENT_ID", uid)
            CoroutineScope(Dispatchers.Main).launch {
                pTaskLoading.visibility = View.VISIBLE
                val tList = async(Dispatchers.IO) { fetchUserTasks(patientId.toInt()) }
                for (i in 0 until tList.await().size) {
                    data.add(
                        DailyTasksViewModel(
                            "Tarefa " + (i + 1),
                            tList.await()[i].taskname,
                            tList.await()[i].isConfirmed,
                            tList.await()[i].isQuestionnaire
                        ))
                    val adapter = DailyTasksAdapter(data, view.context, patientId) { position, taskName, done ->
                        if (!done){
                            val act = activity as PatientMode
                            act.openChatOn(position, taskName)
                        } else {
                            Toast.makeText(context, "Tarefa já realizada", Toast.LENGTH_LONG).show()
                        }
                    }
                    dailyTaskView.adapter = adapter
                }
                pTaskLoading.visibility = View.INVISIBLE
            }
        }
        else {
            Log.d("ERROR", "UID is empty")
        }
    }

    private fun fetchUserTasks(id: Int): ArrayList<DailyTask> {
        val frame = apiget("$API_ADDRESS/tasks/$id")
        val taskList = ArrayList<DailyTask>()
        var isCompleted: Boolean
        var isQuestionnaire: Boolean

        if(!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            taskList.ensureCapacity(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                //Log.d("RESPONSE_TASKS", jsonobject.toString())
                isCompleted = jsonObject.getInt("isCompleted") != 0
                isQuestionnaire = jsonObject.getInt("isQuestionnaire") != 0
                taskList.add(DailyTask(jsonObject.getString("taskId").toString(), jsonObject.getString("taskName").toString(), isCompleted, isQuestionnaire))
            }
        }
        return taskList
    }
}