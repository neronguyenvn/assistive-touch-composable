package com.example.composemoveablevirtualbutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.composemoveablevirtualbutton.core.designsystem.component.AssistiveTouchComposable
import com.example.composemoveablevirtualbutton.ui.theme.ComposeMoveableVirtualButtonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMoveableVirtualButtonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Yellow
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.CenterStart) {
                        AssistiveTouchComposable()
                    }
                }
            }
        }
    }
}
