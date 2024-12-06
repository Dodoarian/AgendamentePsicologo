package com.example.agendamente.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.agendamente.Routes
import com.example.agendamente.models.ActivitySchedule
import com.example.agendamente.models.FireAuthViewModel
import com.example.agendamente.models.Patient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardPsychologistScreen(
    navController: NavHostController,
    fireViewModel: FireAuthViewModel
) {
    val context = LocalContext.current
    val patientId = remember { mutableStateOf("") }
    val patients by fireViewModel.patients.collectAsState()
    val schedules by fireViewModel.activitySchedules.collectAsState()

    LaunchedEffect(patientId) {
        if (patientId.value.isNotEmpty()) {
            fireViewModel.loadActivitySchedules(patientId.value)
        }
    }

    LaunchedEffect(Unit) {
        fireViewModel.loadPatients()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Lista de Pacientes",
                style = typography.titleLarge,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            //TODO: LazyColumn para exibir a lista de pacientes
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(patients?.size ?: 0) { index ->
                    PatientCard(
                        patient = patients!!.get(index),
                        onClick = { selectedPatientId ->
                            patientId.value = selectedPatientId
                            navController.navigate("${Routes.PATIENT_SCHEDULE}/{$selectedPatientId}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PatientCard(patient: Patient, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(patient.id) },
//        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = patient.name,
                style = typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View Details")
        }
    }
}

@Composable
fun PatientScheduleScreen(
    patientId: String,
    fireViewModel: FireAuthViewModel,
    navController: NavHostController,
    isPatient: Boolean = false
) {
    val context = LocalContext.current
    val activitySchedules by fireViewModel.activitySchedules.collectAsState()
    var patientName by remember { mutableStateOf("") }
    Log.d("PatientScheduleScreen", "User Logged ID: $patientId")

    LaunchedEffect(Unit) {
        fireViewModel.loadActivitySchedules(
            patientId = patientId,
        )
        fireViewModel.getPacientName(patientId) {
            patientName = it
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!isPatient) {
                IconButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF2F6932), Color(0xFF4CAF50)),
                                start = Offset(0f, 0f),
                                end = Offset(
                                    Float.POSITIVE_INFINITY,
                                    Float.POSITIVE_INFINITY
                                )
                            ), CircleShape
                        ),
                    onClick = {
                        navController.navigate("${Routes.PATIENT_ADD_SCHEDULE}/$patientId")
                    },
                    content = {
                        Icon(
                            Icons.Filled.Add, contentDescription = "Add Schedule",
                            modifier = Modifier.graphicsLayer(alpha = 0.99f),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                if (isPatient) {
                    Text(
                        text = "Olá $patientName, bem-vindo de volta!",
                        style = typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = "Agendamentos do paciente: $patientName",
                        style = typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                }


                LazyColumn {
                    activitySchedules?.let { schedule ->
                        items(schedule.size) { index ->
                            ScheduleCard(activitySchedules!![index], onClick = {navController.navigate("${Routes.SCHEDULE_DETAIL}/{${activitySchedules!![index].id}}")})
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ScheduleCard(schedule: ActivitySchedule, onClick: () -> Unit = {}) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val startDateFormatted = schedule.startDate.let { dateFormat.format(Date(it)) }
    val endDateFormatted = schedule.endDate.let { dateFormat.format(Date(it)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = schedule.title, style = typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = schedule.description, style = typography.bodyMedium)
            HorizontalDivider()
            Text(text = "Frequência: ${schedule.frequency}", style = typography.bodySmall)
            Text(text = "Início: $startDateFormatted", style = typography.bodySmall)
            Text(text = "Fim: $endDateFormatted", style = typography.bodySmall)
        }
    }
}

