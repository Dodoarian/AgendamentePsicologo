package com.example.agendamente.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class FireAuthViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = FireAuthViewModel::class.simpleName
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _authState = MutableStateFlow(auth.currentUser)
    val authState: StateFlow<FirebaseUser?> = _authState

    private val _patients = MutableStateFlow<List<Patient>?>(value = null)
    val patients: StateFlow<List<Patient>?> = _patients

    private val _activitySchedules = MutableStateFlow<List<ActivitySchedule>?>(value = null)
    val activitySchedules: StateFlow<List<ActivitySchedule>?> = _activitySchedules

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules


    init {
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = firebaseAuth.currentUser
        }
    }

    fun loginPatient(
        email: String, password: String, onSuccess: (Patient) -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user
                if (user != null) {
                    val snapshot = database.child("patients").child(user.uid).get().await()
                    val data = snapshot.value as? HashMap<*, *>
                    val patient = data?.let {
                        Patient(
                            id = it["id"] as? String ?: "",
                            name = it["name"] as? String ?: "",
                            email = it["email"] as? String ?: "",
                            firebaseToken = it["firebaseToken"] as? String ?: ""
                        )
                    }
                    if (patient != null) {
                        onSuccess(patient)
                    } else {
                        onError("Dados do paciente não encontrados.")
                    }
                } else {
                    onError("Usuário não autenticado.")
                }
            } catch (e: Exception) {
                Log.e(tag, "LoginPatient error: ${e.message}", e)
                onError(e.message ?: "Ocorreu um erro desconhecido.")
            }
        }
    }

    fun signupPatient(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                val token = user?.getIdToken(false)?.await()?.token
                if (user != null && token != null) {
                    val patient = Patient(
                        id = user.uid, name = name, email = email, firebaseToken = token
                    )
                    database.child("patients").child(user.uid).setValue(patient).await()
                    onSuccess()
                } else {
                    onError("Falha ao registrar o paciente.")
                }
            } catch (e: Exception) {
                Log.e(tag, "SignupPatient error: ${e.message}", e)
                onError(e.message ?: "Ocorreu um erro desconhecido.")
            }
        }
    }

    fun getPacientName(patientId: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val name = database.child("patients").child(patientId).child("name").get()
                .await().value as String
            onSuccess(name)
        }
    }

    fun loginPsychologist(
        email: String,
        password: String,
        onSuccess: (Psychologist) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user
                if (user != null) {
                    val snapshot = database.child("psychologists").child(user.uid).get().await()
                    val data = snapshot.value as? HashMap<*, *>
                    val psychologist = data?.let {
                        Psychologist(
                            id = it["id"] as? String ?: "",
                            name = it["name"] as? String ?: "",
                            email = it["email"] as? String ?: "",
                            firebaseToken = it["firebaseToken"] as? String ?: ""
                        )
                    }
                    if (psychologist != null) {
                        onSuccess(psychologist)
                    } else {
                        onError("Dados do psicólogo não encontrados.")
                    }
                } else {
                    onError("Usuário não autenticado.")
                }
            } catch (e: Exception) {
                Log.e(tag, "LoginPatient error: ${e.message}", e)
                onError(e.message ?: "Ocorreu um erro desconhecido.")
            }
        }
    }

    fun signupPsychologist(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                val token = user?.getIdToken(false)?.await()?.token
                if (user != null && token != null) {
                    val psychologist = Psychologist(
                        id = user.uid, name = name, email = email, firebaseToken = token
                    )
                    database.child("psychologists").child(user.uid).setValue(psychologist).await()
                    onSuccess()
                } else {
                    onError("Falha ao registrar o psicólogo.")
                }
            } catch (e: Exception) {
                Log.e(tag, "SignupPsychologist error: ${e.message}", e)
                onError(e.message ?: "Ocorreu um erro desconhecido.")
            }
        }
    }


    fun logout() {
        auth.signOut()
    }

    fun writeData(path: String, data: Any) {
        database.child(path).setValue(data).addOnSuccessListener {
            Log.d(tag, "Dados gravados com sucesso")
        }.addOnFailureListener { exception ->
            Log.e(tag, "Erro ao gravar dados", exception)
        }
    }

    fun loadPatients() {
        database.child("patients").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val patientsList = snapshot.children.mapNotNull { child ->
                    val data = child.value as? HashMap<*, *>
                    data?.let {
                        Patient(
                            id = it["id"] as? String ?: "",
                            name = it["name"] as? String ?: "",
                            email = it["email"] as? String ?: "",
                            firebaseToken = it["firebaseToken"] as? String ?: ""
                        )
                    }
                }
                _patients.value = patientsList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(tag, "Erro ao carregar pacientes: ${error.message}", error.toException())
            }
        })
    }

    fun loadSchedules(activityScheduleId: String) {
        database.child("schedules").orderByChild("activityScheduleId").equalTo(activityScheduleId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val loadedSchedules =
                        snapshot.children.mapNotNull { it.getValue(Schedule::class.java) }
                    _schedules.value = loadedSchedules
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("loadSchedules", "Error: ${error.message}")
                }
            })
    }

    fun loadActivitySchedules(patientId: String) {
        database.child("activitySchedules").orderByChild("patientId").equalTo(patientId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val activityScheduleList = snapshot.children.mapNotNull { child ->
                        val data = child.value as? HashMap<*, *>
                        data?.let {
                            ActivitySchedule(
                                id = it["id"] as? String ?: "",
                                patientId = it["patientId"] as? String ?: "",
                                title = it["title"] as? String ?: "",
                                description = it["description"] as? String ?: "",
                                frequency = it["frequency"] as? String ?: "",
                                endDate = it["endDate"] as Long,
                                startDate = it["startDate"] as Long,
                                psychologistId = it["psychologistId"] as? String ?: "",
                            )
                        }
                    }
                    Log.w("teste", "dados $activityScheduleList")
                    _activitySchedules.value = activityScheduleList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "Erro ao carregar pacientes: ${error.message}", error.toException())
                }
            })
    }

    fun createActivitySchedule(
        activitySchedule: ActivitySchedule, onResult: (Boolean) -> Unit
    ) {
        val key = database.child("activitySchedules").push().key
        if (key == null) {
            onResult(false)
            return
        }

        val activityScheduleValues = activitySchedule.apply {
            id = key
        }.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/activitySchedules/$key" to activityScheduleValues
        )

        val startHour = activitySchedule.hour.split(":")[0].toInt()
        val startMinute = activitySchedule.hour.split(":")[1].toInt()
        val timeInMillis = (startHour * 3600 + startMinute * 60) * 1000L

        when (activitySchedule.frequency) {
            "diário" -> {
                val daysToAdd = 30 // Exemplo: gerar 30 dias
                for (i in 0 until daysToAdd) {
                    val scheduleDate = activitySchedule.startDate + (i * 86400000L) + timeInMillis

                    val scheduleKey = database.child("schedules").push().key
                    if (scheduleKey != null) {
                        val schedule = Schedule(
                            id = scheduleKey,
                            activityScheduleId = key,
                            note = "DIÁRIO: ${activitySchedule.hour} - ${activitySchedule.description}",
                            scheduled = scheduleDate,
                            done = 0,
                            createdAt = System.currentTimeMillis(),
                        )

                        childUpdates["/schedules/$scheduleKey"] = schedule.toMap()
                    }
                }
            }

            "semanal" -> {
                var currentStartDate = activitySchedule.startDate

                while (currentStartDate <= activitySchedule.endDate) {
                    val scheduleDate = currentStartDate + timeInMillis

                    val scheduleKey = database.child("schedules").push().key
                    if (scheduleKey != null) {
                        val schedule = Schedule(
                            id = scheduleKey,
                            activityScheduleId = key,
                            note = "SEMANAL: ${activitySchedule.hour} - ${activitySchedule.description}",
                            scheduled = scheduleDate,
                            done = 0,
                            createdAt = System.currentTimeMillis(),
                        )

                        childUpdates["/schedules/$scheduleKey"] = schedule.toMap()
                    }

                    currentStartDate += 604800000L
                }
            }

            else -> {}
        }

        viewModelScope.launch {
            database.updateChildren(childUpdates).addOnSuccessListener {
                Log.d(tag, "Dados gravados com sucesso")
                onResult(true)
            }.addOnFailureListener { exception ->
                Log.e(tag, "Erro ao gravar dados", exception)
                onResult(false)
            }
        }
    }


    fun updateScheduleDone(scheduleId: String, done: Boolean, onResult: (Boolean) -> Unit) {
        database.child("schedules").child(scheduleId).child("done").setValue(if (done) 1 else 0)
            .addOnSuccessListener {
                _schedules.value = _schedules.value.map { schedule ->
                    if (schedule.id == scheduleId) {
                        schedule.copy(done = if (done) 1 else 0)
                    } else {
                        schedule
                    }
                }
                onResult(true)
            }.addOnFailureListener { exception ->
                Log.e(tag, "Erro ao atualizar agendamento $scheduleId.", exception)
                onResult(false)
            }
    }
}


