package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LandlordProfile
import com.example.data.model.Maintenance
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPaid
import com.example.ui.viewmodel.MainViewModel
import com.example.util.DigitalReceiptGenerator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: MainViewModel,
    payments: List<Payment>,
    properties: List<Property>,
    tenants: List<Tenant>,
    maintenance: List<Maintenance>,
    profile: LandlordProfile,
    selectedDate: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val propMap = remember(properties) { properties.associateBy { it.id } }
    val tenantMap = remember(tenants) { tenants.associateBy { it.id } }

    var selectedPropertyFilter by remember { mutableStateOf<Long?>(null) }
    var calMonthOffset by remember { mutableIntStateOf(0) }

    val cal = remember(calMonthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, calMonthOffset)
        }
    }

    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) // 0-based
    val monthYearStr = remember(calMonthOffset) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("ms", "MY"))
        sdf.format(cal.time)
    }

    // Filter by unit if selected
    val filteredPayments = remember(payments, selectedPropertyFilter) {
        if (selectedPropertyFilter == null) payments
        else payments.filter { it.propertyId == selectedPropertyFilter }
    }
    val filteredMaintenance = remember(maintenance, selectedPropertyFilter) {
        if (selectedPropertyFilter == null) maintenance
        else maintenance.filter { it.propertyId == selectedPropertyFilter }
    }

    // Days in current month
    val daysInMonth = remember(calMonthOffset) {
        val c = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        c.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Day of week of 1st day (1=Sunday, 2=Monday, ..., 7=Saturday)
    val firstDayOfWeek = remember(calMonthOffset) {
        val c = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        c.get(Calendar.DAY_OF_WEEK)
    }

    val monthNumberStr = (month + 1).toString().padStart(2, '0')
    val yearMonthPrefix = "$year-$monthNumberStr"

    // Events on selected date
    val paymentsOnSelectedDate = filteredPayments.filter {
        it.paymentDate == selectedDate || it.dueDate == selectedDate
    }
    val maintenanceOnSelectedDate = filteredMaintenance.filter { it.dateReported == selectedDate }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("calendar_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Kalendar Pembayaran Bulanan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pantau tarikh kutipan, bayaran tertunggak & tarikh akhir",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Unit Filter Chips (Horizontally Scrollable LazyRow per prompt instruction)
            Text(
                text = "Tapis Mengikut Unit:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedPropertyFilter == null,
                        onClick = { selectedPropertyFilter = null },
                        label = { Text("Semua Unit (${properties.size})", fontSize = 11.sp) }
                    )
                }
                items(properties) { p ->
                    FilterChip(
                        selected = selectedPropertyFilter == p.id,
                        onClick = { selectedPropertyFilter = p.id },
                        label = { Text("Unit ${p.unitNumber}", fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Month navigation card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { calMonthOffset-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Bulan Lepas")
                        }
                        Text(
                            text = monthYearStr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { calMonthOffset++ }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Bulan Depan")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day headers (Ahad, Isn, Sel, Rab, Kha, Jum, Sab)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("Ahd", "Isn", "Sel", "Rab", "Kha", "Jum", "Sab").forEach { dayName ->
                            Text(
                                text = dayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Grid
                    val totalSlots = 42 // 6 weeks * 7 days
                    val startOffset = firstDayOfWeek - 1 // Sunday=0, Monday=1, ...

                    val weeks = totalSlots / 7
                    for (w in 0 until weeks) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (d in 0..6) {
                                val slotIndex = w * 7 + d
                                val dayNum = slotIndex - startOffset + 1

                                if (dayNum in 1..daysInMonth) {
                                    val dateStr = "$yearMonthPrefix-${dayNum.toString().padStart(2, '0')}"
                                    val isSelected = dateStr == selectedDate

                                    // Check status on this day
                                    val paidCount = filteredPayments.count { it.paymentDate == dateStr && it.status == "LUNAS" }
                                    val overdueCount = filteredPayments.count { it.dueDate == dateStr && (it.status == "TERTUNGGAK" || it.balanceDue > 0) }
                                    val partialCount = filteredPayments.count { it.paymentDate == dateStr && it.status == "SEPARA" }

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                                else Color.Transparent
                                            )
                                            .border(
                                                width = if (isSelected) 1.5.dp else 0.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { viewModel.setSelectedCalendarDate(dateStr) }
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = dayNum.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                            )

                                            // Dot indicator
                                            if (paidCount > 0 || overdueCount > 0 || partialCount > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            when {
                                                                overdueCount > 0 -> StatusOverdue
                                                                partialCount > 0 -> StatusPartial
                                                                else -> StatusPaid
                                                            }
                                                        )
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(38.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusPaid))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lunas Diterima", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(10.dp))

                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusPartial))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bayaran Separa", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(10.dp))

                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusOverdue))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tertunggak", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Date Header
            Text(
                text = "Rekod Pada: $selectedDate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (paymentsOnSelectedDate.isEmpty() && maintenanceOnSelectedDate.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tiada transaksi pembayaran atau kerosakan pada tarikh ini",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Payments on this date (Strictly "Satu Maklumat Satu Baris" with icon-only buttons)
            items(paymentsOnSelectedDate.size) { idx ->
                val p = paymentsOnSelectedDate[idx]
                val prop = propMap[p.propertyId]
                val tenant = tenantMap[p.tenantId]

                ElevatedCard(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Baris 1: Nama & Unit + Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${tenant?.name ?: "Penyewa"} (${prop?.unitNumber ?: "-"})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (p.status) {
                                    "LUNAS" -> StatusPaid.copy(alpha = 0.15f)
                                    "SEPARA" -> StatusPartial.copy(alpha = 0.15f)
                                    else -> StatusOverdue.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = p.status,
                                    color = when (p.status) {
                                        "LUNAS" -> StatusPaid
                                        "SEPARA" -> StatusPartial
                                        else -> StatusOverdue
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Baris 2: Bulan Sewa & Tarikh Akhir
                        Text(
                            text = "Sewa ${p.billingMonth} • Tarikh Akhir: ${p.dueDate}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Baris 3: Jumlah Dibayar
                        Text(
                            text = "Jumlah Dibayar: RM ${String.format("%.2f", p.amountPaid)}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (p.amountPaid > 0) EmeraldPrimaryLight else MaterialTheme.colorScheme.onSurface
                        )

                        // Baris 4: Baki Tertunggak (jika ada)
                        if (p.balanceDue > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Baki Tertunggak: RM ${String.format("%.2f", p.balanceDue)}",
                                color = StatusOverdue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Baris 5: Butang Tindakan (IKON SAHAJA - Resit & WhatsApp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Resit Icon Button
                            IconButton(
                                onClick = { viewModel.openDigitalReceipt(p) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(
                                    Icons.Default.ReceiptLong,
                                    contentDescription = "Resit",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            if (p.balanceDue > 0 && tenant != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                // 2. WhatsApp Icon Button
                                IconButton(
                                    onClick = {
                                        val reminder = DigitalReceiptGenerator.buildOverdueReminderWhatsApp(p, tenant, prop, profile)
                                        DigitalReceiptGenerator.shareViaWhatsApp(context, tenant.phone, reminder)
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366))
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = "WhatsApp",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Maintenance on this date
            items(maintenanceOnSelectedDate.size) { idx ->
                val m = maintenanceOnSelectedDate[idx]
                val prop = propMap[m.propertyId]

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("${m.title} (${prop?.unitNumber ?: "-"})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(m.category, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text("RM ${String.format("%.2f", m.cost)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
