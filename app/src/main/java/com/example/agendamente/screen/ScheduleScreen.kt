package com.example.agendamente.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agendamente.UserType
import com.example.agendamente.components.TopBarNavigation
import com.example.agendamente.models.FireAuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScheduleScreen(
    viewModel: FireAuthViewModel,
    activityScheduleId: String,
    navigator: NavHostController,
    userType: UserType
) {
    val schedules by viewModel.schedules.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSchedules(activityScheduleId)
    }
    TopBarNavigation({
        navigator.popBackStack()
    }) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(schedules) { schedule ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = schedule.done == 1,
                                onCheckedChange = if (userType == UserType.Patient) { isChecked ->
                                    viewModel.updateScheduleDone(schedule.id, isChecked) { success ->
                                        val message = if (success) {
                                            "Atividade atualizada com sucesso."
                                        } else {
                                            "Erro ao atualizar atividade."
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                } else null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = schedule.note,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium
                                )

                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val formattedDate = dateFormat.format(Date(schedule.scheduled))

                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }

            }
        }
    }
}
