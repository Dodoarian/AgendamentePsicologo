package com.example.agendamente

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agendamente.components.TopBarNavigation
import com.example.agendamente.factory.FireAuthViewModelFactory
import com.example.agendamente.models.AuthViewModel
import com.example.agendamente.models.FireAuthViewModel
import com.example.agendamente.screen.AddScheduleScreen
import com.example.agendamente.screen.ScheduleScreen
import com.example.agendamente.screen.DashboardPsychologistScreen
import com.example.agendamente.screen.ListSchedulesTodo
import com.example.agendamente.screen.LoginScreen
import com.example.agendamente.screen.PatientScheduleScreen
import com.example.agendamente.screen.SelectionScreen
import com.example.agendamente.screen.SignupScreen
import com.example.agendamente.ui.theme.AgendamenteTheme

class MainActivity : ComponentActivity() {
    private lateinit var fireViewModel: FireAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = FireAuthViewModelFactory(application)
        fireViewModel = ViewModelProvider(this, factory)[FireAuthViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel: AuthViewModel = viewModel()
            val firebase by fireViewModel.authState.collectAsState()

            LaunchedEffect(firebase) {
                if (firebase != null) {
                    navController.navigate(Routes.SELECTION_SCREEN) { }
                }
            }

            AgendamenteTheme {
                MaterialTheme(
                    colorScheme = lightColorScheme(
                        primary = Color(0xFF007f00),
                        secondary = Color(0xFF479647)
                    ),
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.SELECTION_SCREEN
                    ) {
                        composable(Routes.SELECTION_SCREEN) { SelectionScreen(navController) }
                        composable(Routes.LOGIN_PATIENT) {
                            LoginScreen(
                                navController,
                                userType = UserType.Patient,
                                viewModel = viewModel,
                                fireViewModel = fireViewModel
                            )
                        }
                        composable(Routes.LOGIN_PSYCHOLOGIST) {
                            LoginScreen(
                                navController,
                                userType = UserType.Psychologist,
                                viewModel = viewModel,
                                fireViewModel = fireViewModel
                            )
                        }
                        composable(Routes.SIGNUP_PATIENT) {
                            SignupScreen(
                                navController,
                                userType = UserType.Patient,
                                viewModel = viewModel,
                                fireViewModel = fireViewModel
                            )
                        }
                        composable(Routes.SIGNUP_PSYCHOLOGIST) {
                            SignupScreen(
                                navController,
                                userType = UserType.Psychologist,
                                viewModel = viewModel,
                                fireViewModel = fireViewModel
                            )
                        }
                        composable(Routes.DASHBOARD_PATIENT) {
                            ListSchedulesTodo(navController, fireViewModel)
                        }
                        composable(Routes.DASHBOARD_PSYCHOLOGIST) {
                            DashboardPsychologistScreen(navController, fireViewModel)
                        }
                        composable("${Routes.PATIENT_SCHEDULE}/{patientId}") { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId")
                            if (patientId != null) {
                                TopBarNavigation(
                                    {
                                        navController.popBackStack()
                                    }
                                ) {
                                    PatientScheduleScreen(
                                        patientId = patientId.replace("{", "").replace("}", ""),
                                        fireViewModel,
                                        navController
                                    )
                                }
                            } else {
                                navController.navigate(Routes.SELECTION_SCREEN)
                            }
                        }
                        composable("${Routes.PATIENT_SCHEDULE}/{patientId}/patient") { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId")
                            if (patientId != null) {
                                TopBarNavigation(
                                    {
                                        navController.popBackStack()
                                    }
                                ) {
                                    PatientScheduleScreen(
                                        patientId = patientId.replace("{", "").replace("}", ""),
                                        fireViewModel,
                                        navController,
                                        isPatient = true
                                    )
                                }
                            } else {
                                navController.navigate(Routes.SELECTION_SCREEN)
                            }
                        }
                        composable("${Routes.PATIENT_ADD_SCHEDULE}/{patientId}") { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId")
                            if (patientId != null) {
                                TopBarNavigation(
                                    {
                                        navController.popBackStack()
                                    }
                                ) {
                                    AddScheduleScreen(
                                        patientId = patientId.replace("{", "").replace("}", ""),
                                        viewModel = fireViewModel,
                                        navigateBack = {
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                        }
                        composable("${Routes.SCHEDULE_DETAIL}/{activityScheduleId}") { backStackEntry ->
                            val activityScheduleId = backStackEntry.arguments?.getString("activityScheduleId")
                            if (activityScheduleId != null) {
                                ScheduleScreen(
                                    activityScheduleId = activityScheduleId.replace("{", "").replace("}", ""),
                                    viewModel = fireViewModel,
                                    navigator = navController,
                                    userType = UserType.Psychologist

                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}


object Routes {
    const val SCHEDULE_DETAIL = "patient_detail_schedule_screen"
    const val DASHBOARD_PATIENT = "dashboard_screen_patient"
    const val DASHBOARD_PSYCHOLOGIST = "dashboard_screen_psychologist"
    const val PATIENT_ADD_SCHEDULE = "patient_add_schedule_screen"
    const val PATIENT_SCHEDULES = "patient_schedule_list_screen"
    const val PATIENT_SCHEDULE = "patient_schedule_screen"
    const val SELECTION_SCREEN = "selection_screen"
    const val LOGIN_PATIENT = "login_screen_patient"
    const val LOGIN_PSYCHOLOGIST = "login_screen_psychologist"
    const val SIGNUP_PATIENT = "signup_screen_patient"
    const val SIGNUP_PSYCHOLOGIST = "signup_screen_psychologist"
}

enum class UserType {
    Patient,
    Psychologist,
    Undefined
}
