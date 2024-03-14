package com.example.composemoveablevirtualbutton.core.designsystem.component

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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

    val offsetY by remember { mutableStateOf(Animatable(0f)) }
    var showDialog by remember { mutableStateOf(false) }
    var expandFrom by remember { mutableStateOf(Alignment.Start) }

    LaunchedEffect(offsetY) {
        Log.d("OFF", offsetY.toString())
    }

    AssistiveButton(
        offsetY = offsetY,
        onClick = { showDialog = true },
        changeExpandFrom = { expandFrom = it }
    )

    AssistiveMenu(
        showDialog = showDialog,
        buttonOffsetY = offsetY.value,
        expandFrom = expandFrom
    ) {
        showDialog = false
    }
}

@Composable
private fun AssistiveButton(
    offsetY: Animatable<Float, AnimationVector1D>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    changeExpandFrom: (Alignment.Horizontal) -> Unit,
) {
    val offsetX by remember { mutableStateOf(Animatable(0f)) }
    var position by remember { mutableStateOf(Offset.Zero) }

    var size = remember { IntSize.Zero }
    val screenSize = with(LocalDensity.current) {
        IntSize(
            width = LocalConfiguration.current.screenWidthDp.dp.toPx().roundToInt(),
            height = LocalConfiguration.current.screenHeightDp.dp.toPx().roundToInt()
        )
    }
    val coroutineScope = rememberCoroutineScope()


    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val (dx, dy) = calculateDxDy(
                            position = position,
                            componentSize = size,
                            screenSize = screenSize,
                        ) {
                            changeExpandFrom(it)
                        }
                        coroutineScope.launch {
                            offsetX.stop()
                            offsetX.animateTo(offsetX.value + dx)
                        }
                        coroutineScope.launch {
                            offsetY.stop()
                            offsetY.animateTo(offsetY.value + dy)
                        }

                    }) { change, dragAmount ->
                    change.consume()

                    coroutineScope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount.x)
                    }
                    coroutineScope.launch {
                        offsetY.snapTo(offsetY.value + dragAmount.y)
                    }
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
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

private fun calculateDxDy(
    position: Offset,
    componentSize: IntSize,
    screenSize: IntSize,
    changeExpandFrom: (Alignment.Horizontal) -> Unit
): Pair<Float, Float> {

    val dx = if (position.x + componentSize.width / 2 < screenSize.width / 2) {
        changeExpandFrom(Alignment.Start)
        -position.x
    } else {
        changeExpandFrom(Alignment.End)
        screenSize.width - position.x - componentSize.width
    }

    val dy = when {
        position.y < 0 -> -position.y
        position.y > screenSize.height - componentSize.height ->
            screenSize.height - position.y - componentSize.height

        else -> 0f
    }

    return Pair(dx, dy)
}
