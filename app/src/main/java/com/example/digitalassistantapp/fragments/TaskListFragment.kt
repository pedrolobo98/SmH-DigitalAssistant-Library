package com.example.digitalassistantapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.adapters.TaskListAdapter
import com.example.digitalassistantapp.models.AllTasksViewModel
import com.example.digitalassistantapp.utils.Constants
import com.example.digitalassistantapp.utils.Utility.apiget
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.coroutines.*
import org.json.JSONArray

class TaskListFragment : Fragment() {
    data class AvailableTasks(val taskId: String, val taskname: String, val taskDesc: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }

        val data = ArrayList<AllTasksViewModel>()
        alltasksView.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.Main).launch {
            mTaskLoading.visibility = View.VISIBLE
            val atList = async(Dispatchers.IO) {fetchAllTasks()}

            for (i in 0 until atList.await().size) {
                data.add(
                    AllTasksViewModel(
                        atList.await()[i].taskId,
                        atList.await()[i].taskname,
                        atList.await()[i].taskDesc
                    ))
                val adapter = TaskListAdapter(data){_, taskName, taskDesc ->
                    expandTask(taskName, taskDesc)
                }
                alltasksView.adapter = adapter
            }
            mTaskLoading.visibility = View.INVISIBLE
        }
    }

    private fun fetchAllTasks(): ArrayList<AvailableTasks> {
        val frame = apiget("${Constants.API_ADDRESS}/task_list")
        val taskList = ArrayList<AvailableTasks>()

        if (!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            taskList.ensureCapacity(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                taskList.add(AvailableTasks(
                    jsonObject.getString("taskId").toString(),
                    jsonObject.getString("taskName").toString(),
                    jsonObject.getString("taskDescription").toString()))
            }
        }

        return taskList
    }

    private fun expandTask(taskName: String, description: String) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(taskName)
        builder.setMessage(description)

        builder.setPositiveButton("Editar") {_, _ ->}
        builder.setNegativeButton("Fechar") {_, _ ->}

        builder.create().show()
    }
}