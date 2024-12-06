package com.example.agendamente.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.agendamente.UserType

class AuthViewModel : ViewModel() {
    var userType = mutableStateOf(UserType.Undefined)
    var name = mutableStateOf("")
    var surname = mutableStateOf("")
//    var email = mutableStateOf("dodoarian@gmail.com")
    var email = mutableStateOf("")
//    var password = mutableStateOf("123456")
    var password = mutableStateOf("")
}
