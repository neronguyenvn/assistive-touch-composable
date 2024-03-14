package com.example.composemoveablevirtualbutton.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun AssistiveMenu(
    showDialog: Boolean,
    offsetY: Float,
    expandFrom: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    dismissMenu: () -> Unit
) {
    Row(modifier.offset { IntOffset(0, offsetY.roundToInt()) }) {
        Spacer(modifier = Modifier.weight(0.15f))
        AnimatedVisibility(
            visible = showDialog,
            enter = scaleIn(
                animationSpec = tween(AssistiveMenuDefaults.ANIMATION_TIME, easing = LinearEasing),
                transformOrigin = if (expandFrom == Alignment.Start) {
                    TransformOrigin(0f, 0.5f)
                } else TransformOrigin(1f, 0.5f),
            ),
            exit = scaleOut(
                animationSpec = tween(AssistiveMenuDefaults.ANIMATION_TIME, easing = LinearEasing),
                transformOrigin = if (expandFrom == Alignment.Start) {
                    TransformOrigin(0f, 0.5f)
                } else TransformOrigin(1f, 0.5f),
            ),
            modifier = Modifier.weight(0.7f)
        ) {
            MenuContent(dismissMenu)
        }
        Spacer(modifier = Modifier.weight(0.15f))
    }
}

@Composable
private fun MenuContent(dismissMenu: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(15),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(9) {
                Surface(
                    color = Color.Red,
                    shape = CircleShape,
                    modifier = Modifier.aspectRatio(1f)
                ) {
                    IconButton(onClick = dismissMenu) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

private object AssistiveMenuDefaults {
    const val ANIMATION_TIME = 400
}