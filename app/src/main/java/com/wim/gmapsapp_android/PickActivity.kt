package com.wim.gmapsapp_android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import com.wim.gmapsapp_android.model.Address
import com.wim.gmapsapp_android.ui.screen.MapScreen
import com.wim.gmapsapp_android.ui.theme.GMapsAppAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("address", Address::class.java)
        } else {
            intent.getParcelableExtra("address")
        }

        setContent {
            GMapsAppAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (address != null) {
                        MapScreen(address = address)
                    }
                }
            }
        }
    }
}