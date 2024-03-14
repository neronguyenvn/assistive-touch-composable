package com.example.composemoveablevirtualbutton.core.designsystem.component

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
fun AssistiveTouchComposable() {

    var offsetY by remember { mutableFloatStateOf(0f) }
    var showDialog by remember { mutableStateOf(false) }
    var expandFrom by remember { mutableStateOf(Alignment.Start) }

    LaunchedEffect(offsetY) {
        Log.d("OFF", offsetY.toString())
    }

    AssistiveButton(
        offsetY = offsetY,
        onClick = { showDialog = true },
        changeOffsetY = { offsetY += it },
        changeExpandFrom = { expandFrom = it }
    )

    AssistiveMenu(
        showDialog = showDialog,
        offsetY = offsetY,
        expandFrom = expandFrom
    ) {
        showDialog = false
    }
}

@Composable
private fun AssistiveButton(
    offsetY: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    changeOffsetY: (Float) -> Unit,
    changeExpandFrom: (Alignment.Horizontal) -> Unit,
) {
    val offsetX = remember { Animatable(0f) }

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

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val newOffsetX = if (position.x + size.width / 2 < screenWidth / 2) {
                            changeExpandFrom(Alignment.Start)
                            -position.x + offsetX.value + widthPadding
                        } else {
                            changeExpandFrom(Alignment.End)
                            screenWidth - position.x - size.width + offsetX.value - widthPadding
                        }
                        coroutineScope.launch { offsetX.animateTo(newOffsetX) }

                    }) { change, dragAmount ->
                    change.consume()

                    coroutineScope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount.x)
                    }
                    changeOffsetY(dragAmount.y)
                }
            }
            .onGloballyPositioned {
                size = it.size
                position = it.positionInRoot()
            }
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
            )
        }
    }
}
