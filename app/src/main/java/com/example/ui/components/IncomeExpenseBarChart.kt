package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.StatusOverdue
import java.util.Locale
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

data class MonthlyBarData(
    val monthLabel: String,
    val income: Double,
    val expense: Double
)

enum class ChartType(val label: String) {
    BAR("Bar"),
    LINE("Garisan"),
    PIE("Pai")
}

@Composable
fun IncomeExpenseBarChart(
    dataList: List<MonthlyBarData>,
    modifier: Modifier = Modifier
) {
    var selectedChartType by remember { mutableStateOf(ChartType.BAR) }
    var selectedItemIndex by remember { mutableStateOf<Int?>(dataList.lastIndex.takeIf { it >= 0 }) }
    var touchedPieSection by remember { mutableStateOf<String?>(null) }

    val activeItem = selectedItemIndex?.let { dataList.getOrNull(it) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Tajuk 'Prestasi Kewangan' + Togol Pilihan Carta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Prestasi Kewangan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Togol Gaya Segmented: Bar, Garisan, Pai
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(2.dp)
                ) {
                    ChartType.values().forEach { cType ->
                        val isSelected = selectedChartType == cType
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { selectedChartType = cType }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cType.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pop up angka interaktif bila disentuh
            AnimatedVisibility(visible = activeItem != null || touchedPieSection != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedChartType == ChartType.PIE && touchedPieSection != null) {
                            val totalInc = dataList.sumOf { it.income }
                            val totalExp = dataList.sumOf { it.expense }
                            val net = totalInc - totalExp
                            val displayText = when (touchedPieSection) {
                                "KUTIPAN" -> "Kutipan Keseluruhan: RM ${String.format(Locale.US, "%.0f", totalInc)} (${if (totalInc + totalExp > 0) (totalInc / (totalInc + totalExp) * 100).toInt() else 0}%)"
                                "KOS" -> "Kos & Penyelenggaraan: RM ${String.format(Locale.US, "%.0f", totalExp)} (${if (totalInc + totalExp > 0) (totalExp / (totalInc + totalExp) * 100).toInt() else 0}%)"
                                else -> "Untung Bersih: RM ${String.format(Locale.US, "%.0f", net)}"
                            }
                            Text(displayText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        } else if (activeItem != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Bulan ${activeItem.monthLabel}:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Kutipan: RM ${String.format(Locale.US, "%.0f", activeItem.income)}  |  Kos: RM ${String.format(Locale.US, "%.0f", activeItem.expense)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldPrimaryLight))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kutipan", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusOverdue))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kos", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (selectedChartType == ChartType.PIE) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF3B82F6)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bersih", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body Chart
            when (selectedChartType) {
                ChartType.BAR -> {
                    // Carta Bar Interaktif
                    val maxVal = max(dataList.maxOfOrNull { max(it.income, it.expense) } ?: 5000.0, 3000.0)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .pointerInput(dataList) {
                                detectTapGestures { offset ->
                                    val groupWidth = size.width / dataList.size
                                    val idx = (offset.x / groupWidth).toInt().coerceIn(0, dataList.lastIndex)
                                    selectedItemIndex = idx
                                    touchedPieSection = null
                                }
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height - 20.dp.toPx()
                        val groupWidth = canvasWidth / dataList.size
                        val barWidth = 14.dp.toPx()
                        val spacing = 4.dp.toPx()

                        // Baseline
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.35f),
                            start = Offset(0f, canvasHeight),
                            end = Offset(canvasWidth, canvasHeight),
                            strokeWidth = 2f
                        )

                        dataList.forEachIndexed { index, item ->
                            val centerX = groupWidth * index + groupWidth / 2
                            val isFocused = selectedItemIndex == index

                            // Highlight selected column background
                            if (isFocused) {
                                drawRoundRect(
                                    color = EmeraldPrimaryLight.copy(alpha = 0.08f),
                                    topLeft = Offset(centerX - groupWidth / 2 + 2.dp.toPx(), 0f),
                                    size = Size(groupWidth - 4.dp.toPx(), canvasHeight),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                            }

                            // Income bar (left)
                            val incomeHeight = ((item.income / maxVal) * (canvasHeight - 20)).toFloat().coerceAtLeast(6f)
                            val incomeTop = canvasHeight - incomeHeight
                            drawRoundRect(
                                color = if (isFocused) EmeraldPrimaryLight else EmeraldPrimaryLight.copy(alpha = 0.85f),
                                topLeft = Offset(centerX - barWidth - spacing / 2, incomeTop),
                                size = Size(barWidth, incomeHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )

                            // Expense bar (right)
                            val expenseHeight = ((item.expense / maxVal) * (canvasHeight - 20)).toFloat().coerceAtLeast(4f)
                            val expenseTop = canvasHeight - expenseHeight
                            drawRoundRect(
                                color = if (isFocused) StatusOverdue else StatusOverdue.copy(alpha = 0.75f),
                                topLeft = Offset(centerX + spacing / 2, expenseTop),
                                size = Size(barWidth, expenseHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }

                    // Month Labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        dataList.forEachIndexed { idx, item ->
                            val isSelected = selectedItemIndex == idx
                            Text(
                                text = item.monthLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { selectedItemIndex = idx }
                                    .padding(vertical = 2.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }

                ChartType.LINE -> {
                    // Carta Garisan (Line Chart)
                    val maxVal = max(dataList.maxOfOrNull { max(it.income, it.expense) } ?: 5000.0, 3000.0)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .pointerInput(dataList) {
                                detectTapGestures { offset ->
                                    val groupWidth = size.width / (dataList.size - 1).coerceAtLeast(1)
                                    val idx = ((offset.x + groupWidth / 2) / groupWidth).toInt().coerceIn(0, dataList.lastIndex)
                                    selectedItemIndex = idx
                                    touchedPieSection = null
                                }
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height - 20.dp.toPx()
                        val stepX = canvasWidth / (dataList.size - 1).coerceAtLeast(1)

                        // Baseline
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.35f),
                            start = Offset(0f, canvasHeight),
                            end = Offset(canvasWidth, canvasHeight),
                            strokeWidth = 2f
                        )

                        val incomePath = Path()
                        val expensePath = Path()

                        dataList.forEachIndexed { i, d ->
                            val x = i * stepX
                            val incY = canvasHeight - ((d.income / maxVal) * (canvasHeight - 20)).toFloat()
                            val expY = canvasHeight - ((d.expense / maxVal) * (canvasHeight - 20)).toFloat()

                            if (i == 0) {
                                incomePath.moveTo(x, incY)
                                expensePath.moveTo(x, expY)
                            } else {
                                incomePath.lineTo(x, incY)
                                expensePath.lineTo(x, expY)
                            }
                        }

                        // Draw Lines
                        drawPath(incomePath, color = EmeraldPrimaryLight, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                        drawPath(expensePath, color = StatusOverdue, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

                        // Draw Point dots
                        dataList.forEachIndexed { i, d ->
                            val x = i * stepX
                            val incY = canvasHeight - ((d.income / maxVal) * (canvasHeight - 20)).toFloat()
                            val expY = canvasHeight - ((d.expense / maxVal) * (canvasHeight - 20)).toFloat()
                            val isFocused = selectedItemIndex == i

                            drawCircle(
                                color = if (isFocused) Color.White else EmeraldPrimaryLight,
                                radius = if (isFocused) 5.dp.toPx() else 3.5f.dp.toPx(),
                                center = Offset(x, incY)
                            )
                            if (isFocused) {
                                drawCircle(color = EmeraldPrimaryLight, radius = 5.dp.toPx(), center = Offset(x, incY), style = Stroke(2.dp.toPx()))
                            }

                            drawCircle(
                                color = if (isFocused) Color.White else StatusOverdue,
                                radius = if (isFocused) 5.dp.toPx() else 3.5f.dp.toPx(),
                                center = Offset(x, expY)
                            )
                            if (isFocused) {
                                drawCircle(color = StatusOverdue, radius = 5.dp.toPx(), center = Offset(x, expY), style = Stroke(2.dp.toPx()))
                            }
                        }
                    }

                    // Month Labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dataList.forEachIndexed { idx, item ->
                            val isSelected = selectedItemIndex == idx
                            Text(
                                text = item.monthLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { selectedItemIndex = idx }
                                    .padding(vertical = 2.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }

                ChartType.PIE -> {
                    // Carta Pai (Pie Chart)
                    val totalIncome = dataList.sumOf { it.income }
                    val totalExpense = dataList.sumOf { it.expense }
                    val netProfit = (totalIncome - totalExpense).coerceAtLeast(0.0)
                    val grandSum = (totalIncome + totalExpense).coerceAtLeast(1.0)

                    val incomeAngle = (totalIncome / grandSum * 360f).toFloat()
                    val expenseAngle = (totalExpense / grandSum * 360f).toFloat()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(120.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        val center = Offset(size.width / 2f, size.height / 2f)
                                        val dx = offset.x - center.x
                                        val dy = offset.y - center.y
                                        var angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                        if (angle < 0) angle += 360f

                                        touchedPieSection = if (angle < incomeAngle) "KUTIPAN" else "KOS"
                                    }
                                }
                        ) {
                            val strokeWidth = 26.dp.toPx()
                            val radius = (size.minDimension - strokeWidth) / 2
                            val center = Offset(size.width / 2, size.height / 2)

                            // Income Arc
                            drawArc(
                                color = EmeraldPrimaryLight,
                                startAngle = -90f,
                                sweepAngle = incomeAngle,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth)
                            )

                            // Expense Arc
                            drawArc(
                                color = StatusOverdue,
                                startAngle = -90f + incomeAngle,
                                sweepAngle = expenseAngle,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth)
                            )
                        }

                        // Pie statistics breakdown
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimaryLight.copy(alpha = 0.12f),
                                modifier = Modifier.clickable { touchedPieSection = "KUTIPAN" }
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("Kutipan: RM ${String.format(Locale.US, "%.0f", totalIncome)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimaryLight)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusOverdue.copy(alpha = 0.12f),
                                modifier = Modifier.clickable { touchedPieSection = "KOS" }
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("Kos: RM ${String.format(Locale.US, "%.0f", totalExpense)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusOverdue)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                                modifier = Modifier.clickable { touchedPieSection = "BERSIH" }
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("Untung: RM ${String.format(Locale.US, "%.0f", netProfit)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
