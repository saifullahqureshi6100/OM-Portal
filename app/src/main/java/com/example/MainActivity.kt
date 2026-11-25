package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.firebase.configureAppCheck
import com.example.ui.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch Intent before any UI or ViewModels.
        configureAppCheck(this, intent)

        // STEP 2: Edge-to-edge display and Auth-gated Compose root.
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}
