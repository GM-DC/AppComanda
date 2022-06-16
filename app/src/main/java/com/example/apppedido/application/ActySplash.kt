package com.example.apppedido.application.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ActySplash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, ActyUsuario::class.java))
        finish()
    }
}