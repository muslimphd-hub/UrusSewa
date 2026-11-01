package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LandlordProfile
import com.example.data.model.Maintenance
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import com.example.ui.components.IncomeExpenseBarChart
import com.example.ui.components.MonthlyBarData
import com.example.ui.components.RentalStatusWidget
import com.example.ui.components.ScrollWheelPicker
import com.example.ui.components.StatCard
import com.example.ui.components.UrgentAlertBanner
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPaid
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import com.example.util.DigitalReceiptGenerator
import com.example.util.InAppNotification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    properties: List<Property>,
    tenants: List<Tenant>,
    payments: List<Payment>,
    maintenance: List<Maintenance>,
    profile: LandlordProfile,
    notifications: List<InAppNotification>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentMonthStr = remember { SimpleDateFormat("MMMM yyyy", Locale("ms", "MY")).format(Date()) }

    // Financial Display Filters: Tahun, Bulan, Penyewa
    val availableYears = remember(payments) {
        val years = payments.mapNotNull {
            val y = it.billingMonth.take(4).toIntOrNull()
            if (y != null && y > 2000) y.toString() else null
        }.distinct().sortedDescending()
        listOf("Semua") + (years.ifEmpty { listOf("2026", "2025") })
    }
    var finYear by remember { mutableStateOf("Semua") }

    val monthsList = listOf(
        "Semua" to "Semua",
        "01" to "Jan",
        "02" to "Feb",
        "03" to "Mac",
        "04" to "Apr",
        "05" to "Mei",
        "06" to "Jun",
        "07" to "Jul",
        "08" to "Ogo",
        "09" to "Sep",
        "10" to "Okt",
        "11" to "Nov",
        "12" to "Dis"
    )
    var finMonthCode by remember { mutableStateOf("Semua") }
    var finTenantId by remember { mutableStateOf<Long?>(null) }

    val isFinFiltered = finYear != "Semua" || finMonthCode != "Semua" || finTenantId != null

    val activePayments = remember(payments, finYear, finMonthCode, finTenantId) {
        payments.filter { p ->
            // Kutipan sewa & tunggakan dikira pada bulannya sahaja (billingMonth)
            val matchYear = if (finYear == "Semua") true else {
                p.billingMonth.startsWith(finYear)
            }
            val matchMonth = if (finMonthCode == "Semua") true else {
                p.billingMonth.endsWith("-$finMonthCode") || p.billingMonth == finMonthCode
            }
            val matchTenant = if (finTenantId == null) true else p.tenantId == finTenantId
            matchYear && matchMonth && matchTenant
        }
    }

    val activeMaintenance = remember(maintenance, finYear, finMonthCode) {
        maintenance.filter { m ->
            val matchYear = if (finYear == "Semua") true else m.dateReported.contains(finYear)
            val matchMonth = if (finMonthCode == "Semua") true else m.dateReported.contains("-$finMonthCode-")
            matchYear && matchMonth
        }
    }

    // KPI Calculations based on filtered selection
    val totalUnits = properties.size
    val occupiedUnits = properties.count { it.isOccupied }
    val occupancyRate = if (totalUnits > 0) (occupiedUnits.toDouble() / totalUnits * 100).toInt() else 0

    val totalCollected = activePayments.sumOf { it.amountPaid }
    val totalOverdue = activePayments.sumOf { it.balanceDue }
    val totalMaintenance = activeMaintenance.sumOf { it.cost }
    val bankCost = if (profile.includeBankCost && finTenantId == null) properties.sumOf { it.bankLoanPayment } else 0.0
    val managerCost = if (profile.includeManagementFee && finTenantId == null) profile.managementFeeAmount else 0.0
    val totalCosts = totalMaintenance + bankCost + managerCost
    val netIncome = totalCollected - totalCosts

    val overdueCount = activePayments.count { it.status == "TERTUNGGAK" || it.balanceDue > 0 }

    val selectedTenantObj = tenants.find { it.id == finTenantId }
    val displayTenant = selectedTenantObj?.name ?: "Semua Penyewa"
    val monthName = monthsList.find { it.first == finMonthCode }?.second ?: "Semua"
    val displayPeriod = when {
        finYear == "Semua" && finMonthCode == "Semua" -> currentMonthStr
        finYear != "Semua" && finMonthCode == "Semua" -> "Tahun $finYear"
        finYear == "Semua" && finMonthCode != "Semua" -> "Bulan $monthName"
        else -> "$monthName $finYear"
    }

    // Check expiring contracts (within 30 days)
    val now = System.currentTimeMillis()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val expiringContractsCount = tenants.count { t ->
        try {
            val d = sdf.parse(t.contractEndDate)
            if (d != null) {
                val diffDays = (d.time - now) / (1000 * 60 * 60 * 24)
                diffDays in 0..30
            } else false
        } catch (_: Exception) { false }
    }

    // Chart mock data for past months
    val chartData = listOf(
        MonthlyBarData("Jun", 4800.0, 320.0 + (if (profile.includeBankCost) 3200.0 else 0.0)),
        MonthlyBarData("Jul", 5200.0, 150.0 + (if (profile.includeBankCost) 3200.0 else 0.0)),
        MonthlyBarData("Ogo", 5000.0, 420.0 + (if (profile.includeBankCost) 3200.0 else 0.0)),
        MonthlyBarData("Sep", 5400.0, 200.0 + (if (profile.includeBankCost) 3200.0 else 0.0)),
        MonthlyBarData("Okt", totalCollected.coerceAtLeast(3600.0), totalCosts.coerceAtLeast(230.0))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen")
    ) {
        // Landlord Hero Banner
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PORTAL TUAN RUMAH",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = profile.landlordName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$totalUnits Unit Hartanah • $occupiedUnits Diduduki ($occupancyRate%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Urgent Alert Banner
        item {
            UrgentAlertBanner(
                overdueCount = overdueCount,
                overdueAmount = totalOverdue,
                expiringContractCount = expiringContractsCount,
                onViewOverdueClick = {
                    viewModel.setTab(AppTab.PAYMENTS)
                    viewModel.setPaymentFilter("TERTUNGGAK")
                }
            )
        }

        // Quick Action Grid (Imbas Resit OCR, Tambah Bayaran, Tambah Unit, Log Baiki)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tindakan Pantas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Quick Action Grid (Rekod Bayaran, Tambah Unit, Baiki Unit, Hab Penyewa)
            // 4 items on a single row -> strictly icon-only per prompt instruction!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Rekod Bayar (With OCR upload inside)
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { viewModel.openRecordPayment() }
                        .testTag("quick_action_record_payment")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimaryLight.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Rekod Bayaran",
                                tint = EmeraldPrimaryLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // 2. Tambah Unit / Penyewa Baru (Pop up pilihan)
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { viewModel.openAddOptions() }
                        .testTag("quick_action_add_options")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB).copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Unit / Penyewa",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // 3. Baiki Unit
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { viewModel.openMaintenanceSheet() }
                        .testTag("quick_action_maintenance")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA580C).copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Baiki Unit",
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // 4. Hab Penyewa
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { viewModel.openTenantHub() }
                        .testTag("quick_action_tenant_hub")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9333EA).copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Hab Penyewa",
                                tint = Color(0xFF9333EA),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Widget Status Pembayaran Bulan Semasa (Semua Unit / Penyewa & Tunggakan & Link WhatsApp)
        item {
            RentalStatusWidget(
                properties = properties,
                tenants = tenants,
                payments = payments,
                profile = profile,
                onRecordPaymentClick = { prop, tenant ->
                    viewModel.openRecordPayment()
                },
                onOpenReceiptClick = { payment ->
                    viewModel.openDigitalReceipt(payment)
                },
                onModeChange = { newMode ->
                    viewModel.saveLandlordProfile(profile.copy(widgetDisplayMode = newMode))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Financial Overview KPI Row 1 with Year, Month, Tenant filters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ringkasan Keseluruhan Kewangan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$displayPeriod • $displayTenant",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isFinFiltered) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clickable {
                                finYear = "Semua"
                                finMonthCode = "Semua"
                                finTenantId = null
                            }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "Set Semula",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pemilih Roda Skrol (Scrolled Wheel Picker) untuk Tahun & Bulan (tanpa teks 'Tahun:' & 'Bulan:')
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wheel Picker Tahun
                ScrollWheelPicker(
                    items = availableYears,
                    selectedItem = finYear,
                    onItemSelected = { finYear = it },
                    itemHeight = 30.dp,
                    visibleItemsCount = 3,
                    modifier = Modifier.weight(1f)
                )

                // Wheel Picker Bulan
                ScrollWheelPicker(
                    items = monthsList,
                    selectedItem = monthsList.find { it.first == finMonthCode } ?: monthsList[0],
                    onItemSelected = { finMonthCode = it.first },
                    itemLabel = { it.second },
                    itemHeight = 30.dp,
                    visibleItemsCount = 3,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pilihan Penyewa (Skrol mendatar tanpa label teks 'Penyewa:')
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = finTenantId == null,
                        onClick = { finTenantId = null },
                        label = { Text("Semua", fontSize = 10.sp) }
                    )
                }
                items(tenants) { t ->
                    FilterChip(
                        selected = finTenantId == t.id,
                        onClick = { finTenantId = t.id },
                        label = { Text(t.name, fontSize = 10.sp, maxLines = 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Jumlah Kutipan",
                    value = "RM ${String.format("%.2f", totalCollected)}",
                    subtitle = "${activePayments.count { it.status == "LUNAS" }} unit lunas",
                    icon = Icons.Default.CurrencyExchange,
                    iconTint = EmeraldPrimaryLight,
                    iconBgColor = EmeraldPrimaryLight.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Sewa Tertunggak",
                    value = "RM ${String.format("%.2f", totalOverdue)}",
                    subtitle = "$overdueCount penyewa ada baki",
                    icon = Icons.Default.Warning,
                    iconTint = StatusOverdue,
                    iconBgColor = StatusOverdue.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Kos Operasi",
                    value = "RM ${String.format("%.2f", totalCosts)}",
                    subtitle = buildString {
                        append("Baiki RM ${totalMaintenance.toInt()}")
                        if (profile.includeBankCost && bankCost > 0) append(" • Bank RM ${bankCost.toInt()}")
                        if (profile.includeManagementFee && managerCost > 0) append(" • Pengurus RM ${managerCost.toInt()}")
                    },
                    icon = Icons.Default.ReceiptLong,
                    iconTint = Color(0xFFEA580C),
                    iconBgColor = Color(0xFFEA580C).copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Pendapatan Bersih",
                    value = "RM ${String.format("%.2f", netIncome)}",
                    subtitle = "Kutipan tolak semua kos",
                    icon = Icons.Default.TrendingUp,
                    iconTint = Color(0xFF0F766E),
                    iconBgColor = Color(0xFF0F766E).copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Monthly Income & Expenses Bar Chart
        item {
            IncomeExpenseBarChart(dataList = chartData)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Export Statement Quick Action (Icon only button without wrapping text)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openExportDialog() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Muat Turun Penyata Kewangan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Format PDF atau Excel (CSV)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Icon-only download action button (no squashed/vertical text!)
                    IconButton(
                        onClick = { viewModel.openExportDialog() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Eksport",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Recent Transactions Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi Bayaran Terkini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lihat Semua",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { viewModel.setTab(AppTab.PAYMENTS) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (activePayments.isEmpty()) {
            item {
                Text(
                    text = "Tiada rekod bayaran bagi kriteria tapisan ini.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            // Strictly "Satu Maklumat Satu Baris" per user instruction!
            items(activePayments.take(4)) { p ->
                val prop = properties.find { it.id == p.propertyId }
                val t = tenants.find { it.id == p.tenantId }
                val isPaid = p.status == "LUNAS" && p.balanceDue <= 0
                val isPartial = p.status == "SEPARA" || (p.amountPaid > 0 && p.balanceDue > 0)
                val isOverdue = p.status == "TERTUNGGAK" || (p.amountPaid == 0.0 && p.balanceDue > 0)

                ElevatedCard(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .testTag("recent_payment_${p.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Baris 1: Ikon Status + Nama Penyewa & No Unit + Badge Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isPaid -> StatusPaid.copy(alpha = 0.15f)
                                                isPartial -> StatusPartial.copy(alpha = 0.15f)
                                                else -> StatusOverdue.copy(alpha = 0.15f)
                                            }
                                        )
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isPaid -> Icons.Default.CheckCircle
                                            else -> Icons.Default.Warning
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            isPaid -> StatusPaid
                                            isPartial -> StatusPartial
                                            else -> StatusOverdue
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "${t?.name ?: "Penyewa"} (${prop?.unitNumber ?: "-"})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    isPaid -> StatusPaid.copy(alpha = 0.15f)
                                    isPartial -> StatusPartial.copy(alpha = 0.15f)
                                    else -> StatusOverdue.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = when {
                                        isPaid -> "LUNAS"
                                        isPartial -> "SEPARA"
                                        else -> "TERTUNGGAK"
                                    },
                                    color = when {
                                        isPaid -> StatusPaid
                                        isPartial -> StatusPartial
                                        else -> StatusOverdue
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Baris 2: Bulan Sewa & Tarikh Transaksi
                        Text(
                            text = "Bulan: ${p.billingMonth} • Tarikh: ${p.paymentDate ?: p.dueDate}",
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
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = StatusOverdue
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Baris 5: Butang Tindakan (IKON SAHAJA - Resit, WhatsApp, Emel)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Resit Rasmi Icon
                            IconButton(
                                onClick = { viewModel.openDigitalReceipt(p) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(
                                    Icons.Default.ReceiptLong,
                                    contentDescription = "Resit Rasmi",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            if (p.balanceDue > 0 && t != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                // 2. WhatsApp Peringatan Icon
                                IconButton(
                                    onClick = {
                                        val reminder = DigitalReceiptGenerator.buildOverdueReminderWhatsApp(p, t, prop, profile)
                                        DigitalReceiptGenerator.shareViaWhatsApp(context, t.phone, reminder)
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

                                Spacer(modifier = Modifier.width(8.dp))
                                // 3. Emel Peringatan Icon
                                IconButton(
                                    onClick = {
                                        val reminder = DigitalReceiptGenerator.buildOverdueReminderWhatsApp(p, t, prop, profile)
                                        DigitalReceiptGenerator.sendEmail(
                                            context,
                                            t.email,
                                            "Peringatan Sewa - ${p.billingMonth}",
                                            reminder
                                        )
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Emel",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
