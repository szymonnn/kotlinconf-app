package org.jetbrains.kotlinconf.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import org.jetbrains.kotlinconf.R

class HomeViewModel : ViewModel() {
}

class HomeController : Fragment() {
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = createViewModel()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}
