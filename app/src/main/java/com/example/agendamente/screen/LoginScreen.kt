package com.example.agendamente.screen

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.agendamente.Routes
import com.example.agendamente.UserType
import com.example.agendamente.components.GradientButton
import com.example.agendamente.components.HeadingTextComponent
import com.example.agendamente.components.MyTextFieldComponent
import com.example.agendamente.components.NormalTextComponent
import com.example.agendamente.components.PasswordTextFieldComponent
import com.example.agendamente.models.AuthViewModel
import com.example.agendamente.models.FireAuthViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    userType: UserType,
    viewModel: AuthViewModel,
    fireViewModel: FireAuthViewModel
) {
    viewModel.userType.value = userType
    val (greetingText, actionText, _) = when (userType) {
        UserType.Patient -> Triple("Olá, Paciente", "Bem-vindo de volta", "Registrar como Paciente")
        UserType.Psychologist -> Triple(
            "Olá, Psicólogo",
            "Bem-vindo de volta",
            "Registrar como Psicólogo"
        )

        UserType.Undefined -> Triple("", "", "")
    }

    val context = LocalContext.current

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column {
                NormalTextComponent(value = greetingText)
                HeadingTextComponent(value = actionText)
            }
            Spacer(modifier = Modifier.height(25.dp))
            Column {
                MyTextFieldComponent(
                    labelValue = "Email",
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.value = it }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PasswordTextFieldComponent(
                    labelValue = "Senha",
                    password = viewModel.password.value,
                    onPasswordChange = { viewModel.password.value = it }
                )
            }
            Spacer(modifier = Modifier.height(25.dp))

            Column {
                GradientButton(onClick = {
                    if (userType == UserType.Patient) {
                        fireViewModel.loginPatient(
                            email = viewModel.email.value,
                            password = viewModel.password.value,
                            onSuccess = {
                                navController.navigate("${Routes.PATIENT_SCHEDULE}/{${it.id}}/patient") { }
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        fireViewModel.loginPsychologist(
                            email = viewModel.email.value,
                            password = viewModel.password.value,
                            onSuccess = {
                                navController.navigate(Routes.DASHBOARD_PSYCHOLOGIST) {}
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }) {
                    Text(text = "Entrar", color = Color.White, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(25.dp))
                GradientButton(onClick = {
                    if (userType == UserType.Patient) {
                        navController.navigate(Routes.SIGNUP_PATIENT)
                    } else {
                        navController.navigate(Routes.SIGNUP_PSYCHOLOGIST)
                    }
                }) {
                    Text(text = "Registrar", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}

