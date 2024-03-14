package com.example.composemoveablevirtualbutton.core.designsystem.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun AssistiveTouchComposable(modifier: Modifier = Modifier) {

    val offsetX = remember { Animatable(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var position by remember { mutableStateOf(Offset.Zero) }
    var size = remember { IntSize.Zero }

    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = with(LocalDensity.current) {
        configuration.screenWidthDp.dp.toPx()
    }
    val widthPadding = with(LocalDensity.current) {
        4.dp.toPx()
    }

    var showDialog by remember { mutableStateOf(false) }
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val newOffsetX =
                            if (position.x + size.width / 2 < screenWidth / 2) {
                                -position.x + offsetX.value + widthPadding
                            } else screenWidth - position.x - size.width + offsetX.value - widthPadding
                        coroutineScope.launch { offsetX.animateTo(newOffsetX) }

                    }) { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount.x)
                    }
                    offsetY += dragAmount.y
                }
            }
            .onGloballyPositioned {
                size = it.size
                position = it.positionInRoot()
            }
    ) {
        IconButton(
            onClick = { showDialog = true },
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
            )
        }
    }
    AssistiveMenu(
        showDialog = showDialog,
        offsetY = offsetY.roundToInt(),
        expandFrom = if (position.x + size.width / 2 < screenWidth / 2) {
            Alignment.Start
        } else Alignment.End
    ) {
        showDialog = false
    }
}

@Composable
private fun AssistiveMenu(
    showDialog: Boolean,
    offsetY: Int,
    expandFrom: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    dismissMenu: () -> Unit
) {

    LaunchedEffect(showDialog) {
        if (showDialog) {
            Log.d("FROM", if (expandFrom == Alignment.Start) "START" else "END")
        }
    }


    Row(modifier.offset { IntOffset(0, offsetY) }) {
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
                } else TransformOrigin(1f, 0.5f),            ),
            modifier = Modifier.weight(0.7f)
        ) {
            Surface(
                shape = RoundedCornerShape(15),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.weight(0.7f)
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
                            shape = CircleShape
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
        Spacer(modifier = Modifier.weight(0.15f))
    }
}

object AssistiveMenuDefaults {
    const val ANIMATION_TIME = 400
}