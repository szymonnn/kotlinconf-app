package org.jetbrains.kotlinconf.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

internal inline fun <reified T : ViewModel> Fragment.createViewModel(): T =
    ViewModelProviders.of(this)[T::class.java]
