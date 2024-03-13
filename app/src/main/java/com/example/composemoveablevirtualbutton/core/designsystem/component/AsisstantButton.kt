package com.example.composemoveablevirtualbutton.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
fun AssistantButton(modifier: Modifier = Modifier) {

    val offsetX = remember { Animatable(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var position by remember { mutableStateOf(Offset.Zero) }
    var size = remember { IntSize.Zero }
    var boxOffsetY by remember { mutableFloatStateOf(0f) }


    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = with(LocalDensity.current) {
        configuration.screenWidthDp.dp.toPx()
    }
    val widthPadding = with(LocalDensity.current) {
        4.dp.toPx()
    }

    var showDialog by remember { mutableStateOf(false) }
    if (!showDialog) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            val newOffsetX = if (position.x + size.width / 2 < screenWidth / 2) {
                                -position.x + offsetX.value + widthPadding
                            } else screenWidth - position.x - size.width + offsetX.value - widthPadding
                            coroutineScope.launch { offsetX.animateTo(newOffsetX) }

                        }) { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                        }
                        offsetY += dragAmount.y
                        boxOffsetY += dragAmount.y
                    }
                }
                .onGloballyPositioned {
                    size = it.size
                    position = it.positionInRoot()
                }
        ) {
            IconButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        }
    }
    AnimatedVisibility(
        visible = showDialog,
        enter = expandIn(
            animationSpec = tween(1000)
        ),
        modifier = Modifier.offset { IntOffset(0, boxOffsetY.roundToInt()) }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)

        ) {
            Surface(
                shape = RoundedCornerShape(15),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.fillMaxWidth(0.5f)
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
                            IconButton(onClick = { showDialog = false }) {
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
    }
}
