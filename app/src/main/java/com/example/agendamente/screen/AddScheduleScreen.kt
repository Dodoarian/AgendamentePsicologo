package com.example.agendamente.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agendamente.components.DateRangePickerModal
import com.example.agendamente.components.FrequencyDropdown
import com.example.agendamente.components.GradientButton
import com.example.agendamente.components.MyTextFieldComponent
import com.example.agendamente.components.NormalTextComponent
import com.example.agendamente.components.TimeDropdown
import com.example.agendamente.models.ActivitySchedule
import com.example.agendamente.models.FireAuthViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun AddScheduleScreen(
    patientId: String,
    viewModel: FireAuthViewModel,
    navigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf("diário") }
    var selectedTime by remember { mutableStateOf("7:00") }
    var psychologistId by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Validation state
    var titleError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }

    Surface(
        color = Color.White, modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column {
                NormalTextComponent(value = "Adicionar novo agendamento")
            }
            Spacer(modifier = Modifier.height(25.dp))
            Column {
                MyTextFieldComponent(
                    labelValue = "Título",
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = title.isBlank()
                    },
                )
                if (titleError) {
                    Text(
                        text = "O título é obrigatório.", color = Color.Red, fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                MyTextFieldComponent(
                    labelValue = "Descrição",
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = description.isBlank()
                    },
                )
                if (descriptionError) {
                    Text(
                        text = "A descrição é obrigatória.", color = Color.Red, fontSize = 12.sp
                    )
                }
                if (showDatePicker) {
                    DateRangePickerModal({
                        startDate = it.first
                        endDate = it.second
                        dateError = startDate == null || endDate == null
                    }, {
                        showDatePicker = false
                    })
                }
                Spacer(modifier = Modifier.height(10.dp))
                FrequencyDropdown(onFrequencyChange = { selectedFrequency = it })
                Spacer(modifier = Modifier.height(10.dp))
                TimeDropdown(onTimeChange = { selectedTime = it })
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = startDate?.let {
                    SimpleDateFormat(
                        "dd/MM/yyyy", Locale.getDefault()
                    ).format(it)
                } ?: "",
                    onValueChange = {},
                    shape = MaterialTheme.shapes.medium,
                    label = { Text("Data Inicial") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                showDatePicker = !showDatePicker
                            })
                    },
                    isError = dateError
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = endDate?.let {
                    SimpleDateFormat(
                        "dd/MM/yyyy", Locale.getDefault()
                    ).format(it)
                } ?: "",
                    onValueChange = {},
                    shape = MaterialTheme.shapes.medium,
                    label = { Text("Data Final") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                showDatePicker = !showDatePicker
                            })
                    })
                if (dateError) {
                    Text(
                        text = "As datas são obrigatórias.", color = Color.Red, fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(25.dp))

            Column {
                GradientButton(
                    onClick = {
                        titleError = title.isBlank()
                        descriptionError = description.isBlank()
                        dateError = startDate == null || endDate == null

                        if (!titleError && !descriptionError && !dateError) {
                            viewModel.createActivitySchedule(activitySchedule = ActivitySchedule(
                                title = title,
                                description = description,
                                frequency = selectedFrequency,
                                hour = selectedTime,
                                psychologistId = psychologistId,
                                startDate = startDate!!,
                                endDate = endDate!!,
                                patientId = patientId,
                            ), onResult = {
                                if (it) {
                                    Toast.makeText(
                                        context,
                                        "Agendamento criado com sucesso.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navigateBack()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Falha ao criar o agendamento.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        }
                    },
                    enabled = title.isNotBlank() && description.isNotBlank() && startDate != null && endDate != null
                ) {
                    Text(text = "Adicionar", color = Color.White, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(25.dp))
            }
        }
    }
}
