package com.example.agendamente.services

import android.app.Application
import android.util.Log

class BackgroundApp : Application() {
    private val tag = BackgroundApp::class.simpleName

    override fun onCreate() {
        super.onCreate()
        Log.v(tag, "Iniciando $tag")
    }
}
