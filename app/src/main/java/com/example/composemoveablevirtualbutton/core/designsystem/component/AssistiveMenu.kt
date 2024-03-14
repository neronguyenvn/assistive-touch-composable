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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun AssistiveMenu(
    showDialog: Boolean,
    buttonOffsetY: Float,
    expandFrom: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    dismissMenu: () -> Unit
) {
    var y by remember { mutableFloatStateOf(0f) }
    var height by remember { mutableIntStateOf(0) }
    val screenHeight = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx().roundToInt()
    }

    var offsetY by remember {
        mutableFloatStateOf(calculateOffsetY(y, height, screenHeight, buttonOffsetY))
    }

    LaunchedEffect(buttonOffsetY, height) {
        offsetY = calculateOffsetY(y, height, screenHeight, buttonOffsetY)
    }

    Row(
        modifier = modifier
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .onGloballyPositioned {
                y = it.positionInRoot().y
                height = it.size.height
            }
            .statusBarsPadding()
    ) {
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

private fun calculateOffsetY(
    y: Float,
    componentHeight: Int,
    screenHeight: Int,
    buttonOffsetY: Float
): Float = when {
    y < 0 -> -y + buttonOffsetY
    y > screenHeight - componentHeight -> screenHeight - y - componentHeight + buttonOffsetY
    else -> buttonOffsetY
}


private object AssistiveMenuDefaults {
    const val ANIMATION_TIME = 400
}