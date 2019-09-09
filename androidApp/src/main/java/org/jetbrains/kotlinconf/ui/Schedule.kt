package org.jetbrains.kotlinconf.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.kotlinconf.R

class ScheduleViewModel : ViewModel()

class ScheduleController : Fragment() {
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = createViewModel()

        return inflater.inflate(R.layout.fragment_schedule, container, false).apply {
            setupSchedule()
        }
    }

    private fun View.setupSchedule() {
        val schedule: RecyclerView = findViewById(R.id.schedule_list)
    }
}