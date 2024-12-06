package com.example.agendamente.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.agendamente.models.FireAuthViewModel

@Composable
fun ListSchedulesTodo(
    navController: NavHostController,
    fireViewModel: FireAuthViewModel
) {
    val context = LocalContext.current
    val schedules by fireViewModel.schedules.collectAsState()

    LaunchedEffect(Unit) {
//        fireViewModel.loadSchedules()
    }

}