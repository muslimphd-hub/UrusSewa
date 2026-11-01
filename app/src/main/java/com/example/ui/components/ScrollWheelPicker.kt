package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * Pemilih Roda Skrol (Scrolled Wheel Picker)
 * Gaya moden/iOS wheel drum yang membolehkan pengguna meleret dan memilih
 * item dalam format drum berputar dengan penanda tengah.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ScrollWheelPicker(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 32.dp,
    visibleItemsCount: Int = 3,
    itemLabel: (T) -> String = { it.toString() }
) {
    if (items.isEmpty()) return

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()

    val totalHeight = itemHeight * visibleItemsCount
    val verticalPadding = itemHeight * ((visibleItemsCount - 1) / 2)

    // Scroll to initial selected item
    LaunchedEffect(selectedItem, items) {
        val targetIndex = items.indexOf(selectedItem).coerceAtLeast(0)
        if (targetIndex >= 0 && !listState.isScrollInProgress) {
            listState.scrollToItem(targetIndex)
        }
    }

    // Observe center item and notify on snap
    LaunchedEffect(listState, items) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) null
            else {
                val centerOffset = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
                visibleItems.minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2
                    kotlin.math.abs(itemCenter - centerOffset)
                }?.index
            }
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { centerIndex ->
                if (centerIndex in items.indices) {
                    val candidate = items[centerIndex]
                    if (candidate != selectedItem) {
                        onItemSelected(candidate)
                    }
                }
            }
    }

    Box(
        modifier = modifier
            .height(totalHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        // Central selection bar indicator
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .padding(horizontal = 4.dp)
        ) {}

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = verticalPadding),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = item == selectedItem
                val targetAlpha = if (isSelected) 1.0f else 0.45f
                val targetFontSize = if (isSelected) 13.sp else 11.sp
                val targetFontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    label = "wheel_text_color"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                            onItemSelected(item)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemLabel(item),
                        fontSize = targetFontSize,
                        fontWeight = targetFontWeight,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(targetAlpha)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Top & bottom subtle fade gradients for authentic wheel appearance
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * 0.8f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * 0.8f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        )
                    )
                )
        )
    }
}
