package com.example.agendamente.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
fun SignupScreen(navController: NavHostController, userType: UserType, viewModel: AuthViewModel, fireViewModel: FireAuthViewModel) {
    viewModel.userType.value = userType
    val (greetingText, actionText, _) = when (userType) {
        UserType.Patient -> Triple("Olá, Paciente", "Crie uma Conta", "Entrar como Paciente")
        UserType.Psychologist -> Triple("Olá, Psicólogo", "Crie uma Conta", "Entrar como Psicólogo")
        UserType.Undefined -> Triple("", "", "")
    }

    val context = LocalContext.current

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp)
        ) {
            NormalTextComponent(value = greetingText)
            HeadingTextComponent(value = actionText)
            Spacer(modifier = Modifier.height(25.dp))

            Column {
                MyTextFieldComponent(
                    labelValue = "Nome",
                    value = viewModel.name.value,
                    onValueChange = { viewModel.name.value = it }
                )
//                Spacer(modifier = Modifier.height(10.dp))
//                MyTextFieldComponent(
//                    labelValue = "Sobrenome",
//                    icon = Icons.Outlined.Person,
//                    value = viewModel.surname.value,
//                    onValueChange = { viewModel.surname.value = it }
//                )
                Spacer(modifier = Modifier.height(10.dp))
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
                Spacer(modifier = Modifier.height(10.dp))

                GradientButton(onClick = {
                    if (userType == UserType.Patient) {
                        fireViewModel.signupPatient(
                            email = viewModel.email.value,
                            password = viewModel.password.value,
                            name = viewModel.name.value,
                            onSuccess = {
                                navController.navigate(Routes.LOGIN_PATIENT)
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        fireViewModel.signupPsychologist(
                            email = viewModel.email.value,
                            password = viewModel.password.value,
                            name = viewModel.name.value,
                            onSuccess = {
                                navController.navigate(Routes.LOGIN_PSYCHOLOGIST)
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }) {
                    Text(text = "Registrar", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}