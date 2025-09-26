package com.tuusuario.darfito

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroActivity : AppCompatActivity() {
    //    var tv1 : TextView ?= null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

//        tv1 = findViewById(R.id.tv1)
//
//        val intent = this.intent
//        if (intent == null) {
//            Log.i("TAG", "La activity no se ha llamado mediante un intent")
//        } else {
//            val nombres = intent.getStringExtra("nombres")
//            tv1?.text = nombres
//            Log.i("TAG", "" + nombres)
//        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}