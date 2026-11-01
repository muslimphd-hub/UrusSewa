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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LandlordProfile
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import com.example.ui.components.ScrollWheelPicker
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPaid
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PaymentInitialData
import com.example.util.DigitalReceiptGenerator

@Composable
fun PaymentsScreen(
    viewModel: MainViewModel,
    payments: List<Payment>,
    properties: List<Property>,
    tenants: List<Tenant>,
    profile: LandlordProfile,
    filter: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val propMap = remember(properties) { properties.associateBy { it.id } }
    val tenantMap = remember(tenants) { tenants.associateBy { it.id } }

    val statusOptions = listOf("Semua", "Tertunggak", "Lunas", "Separa")
    var selectedStatus by remember(filter) {
        mutableStateOf(
            when (filter) {
                "TERTUNGGAK" -> "Tertunggak"
                "LUNAS" -> "Lunas"
                "SEPARA" -> "Separa"
                else -> "Semua"
            }
        )
    }

    val availableYears = remember(payments) {
        val years = payments.mapNotNull {
            val y = it.billingMonth.take(4).toIntOrNull()
            if (y != null && y > 2000) y.toString() else null
        }.distinct().sortedDescending()
        listOf("Semua") + (years.ifEmpty { listOf("2026", "2025") })
    }
    var filterYear by remember { mutableStateOf("Semua") }

    val monthOptions = listOf("Semua", "Jan", "Feb", "Mac", "Apr", "Mei", "Jun", "Jul", "Ogo", "Sep", "Okt", "Nov", "Dis")
    var filterMonth by remember { mutableStateOf("Semua") }

    var filterTenantId by remember { mutableStateOf<Long?>(null) }
    var showTenantDialog by remember { mutableStateOf(false) }

    // Kutipan sewa diletakkan di bulannya sahaja (billingMonth)
    val filteredPayments = payments.filter { p ->
        val matchesFilter = when (selectedStatus) {
            "Tertunggak" -> p.status == "TERTUNGGAK" || p.balanceDue > 0
            "Lunas" -> p.status == "LUNAS" && p.balanceDue <= 0
            "Separa" -> p.status == "SEPARA" || (p.amountPaid > 0 && p.balanceDue > 0)
            else -> true
        }

        val matchesYear = if (filterYear == "Semua") true else {
            p.billingMonth.startsWith(filterYear)
        }

        val matchesMonth = if (filterMonth == "Semua") true else {
            val monthIndex = monthOptions.indexOf(filterMonth) // 1 for Jan, 12 for Dis
            val monthDigits = monthIndex.toString().padStart(2, '0')
            p.billingMonth.endsWith("-$monthDigits") || p.billingMonth == monthDigits
        }

        val matchesTenant = if (filterTenantId == null) true else p.tenantId == filterTenantId

        val prop = propMap[p.propertyId]
        val tenant = tenantMap[p.tenantId]
        val matchesSearch = p.billingMonth.contains(searchQuery, ignoreCase = true) ||
                (prop?.unitNumber?.contains(searchQuery, ignoreCase = true) == true) ||
                (prop?.name?.contains(searchQuery, ignoreCase = true) == true) ||
                (tenant?.name?.contains(searchQuery, ignoreCase = true) == true) ||
                p.receiptNumber.contains(searchQuery, ignoreCase = true)

        matchesFilter && matchesYear && matchesMonth && matchesTenant && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openRecordPayment() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_record_payment")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Rekod Bayaran")
            }
        },
        modifier = modifier.fillMaxSize().testTag("payments_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Top header: Removed duplicate record payment buttons as instructed
                Column {
                    Text(
                        text = "Kutipan Sewa & Resit",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Jejak bayaran, tunggakan & imbasan OCR",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari unit, nama, atau no resit...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("search_payment_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Scrolled Wheel Picker Row for Status, Tahun, and Bulan
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Status Scrolled Wheel Picker (Semua, Tertunggak, Lunas, Separa)
                        Box(modifier = Modifier.weight(1f)) {
                            ScrollWheelPicker(
                                items = statusOptions,
                                selectedItem = selectedStatus,
                                onItemSelected = {
                                    selectedStatus = it
                                    val fKey = when (it) {
                                        "Tertunggak" -> "TERTUNGGAK"
                                        "Lunas" -> "LUNAS"
                                        "Separa" -> "SEPARA"
                                        else -> "SEMUA"
                                    }
                                    viewModel.setPaymentFilter(fKey)
                                },
                                visibleItemsCount = 3,
                                itemHeight = 36.dp
                            )
                        }

                        // 2. Tahun Scrolled Wheel Picker (Semua, 2026, ...)
                        Box(modifier = Modifier.weight(1f)) {
                            ScrollWheelPicker(
                                items = availableYears,
                                selectedItem = filterYear,
                                onItemSelected = { filterYear = it },
                                visibleItemsCount = 3,
                                itemHeight = 36.dp
                            )
                        }

                        // 3. Bulan Scrolled Wheel Picker (Semua, Jan - Dis)
                        Box(modifier = Modifier.weight(1f)) {
                            ScrollWheelPicker(
                                items = monthOptions,
                                selectedItem = filterMonth,
                                onItemSelected = { filterMonth = it },
                                visibleItemsCount = 3,
                                itemHeight = 36.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pilihan Penyewa: Skrol kiri-kanan jika <= 10 penyewa, atau pop up list jika > 10 penyewa
                if (tenants.size <= 10) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = filterTenantId == null,
                                onClick = { filterTenantId = null },
                                label = { Text("Semua", fontSize = 11.sp) }
                            )
                        }
                        items(tenants) { t ->
                            FilterChip(
                                selected = filterTenantId == t.id,
                                onClick = { filterTenantId = t.id },
                                label = { Text(t.name, fontSize = 11.sp, maxLines = 1) }
                            )
                        }
                    }
                } else {
                    val currentTenantName = tenants.find { it.id == filterTenantId }?.name ?: "Semua Penyewa"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = filterTenantId != null,
                            onClick = { showTenantDialog = true },
                            label = { Text(currentTenantName, fontSize = 11.sp) }
                        )
                        if (filterTenantId != null) {
                            OutlinedButton(
                                onClick = { filterTenantId = null },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Set Semula", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (filteredPayments.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyExchange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tiada rekod bayaran bagi kategori ini",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Gunakan butang 'Imbas OCR' untuk membaca resit bank atau '+' untuk merekod manual.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredPayments) { payment ->
                    val prop = propMap[payment.propertyId]
                    val tenant = tenantMap[payment.tenantId]

                    val isFull = payment.status == "LUNAS" && payment.balanceDue <= 0
                    val isPartial = payment.status == "SEPARA" || (payment.amountPaid > 0 && payment.balanceDue > 0)
                    val isOverdue = payment.status == "TERTUNGGAK" || (payment.amountPaid == 0.0 && payment.balanceDue > 0)

                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("payment_card_${payment.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row: Unit, Month, Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isFull -> StatusPaid.copy(alpha = 0.15f)
                                                    isPartial -> StatusPartial.copy(alpha = 0.15f)
                                                    else -> StatusOverdue.copy(alpha = 0.15f)
                                                }
                                            )
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isFull -> Icons.Default.CheckCircle
                                                else -> Icons.Default.Warning
                                            },
                                            contentDescription = null,
                                            tint = when {
                                                isFull -> StatusPaid
                                                isPartial -> StatusPartial
                                                else -> StatusOverdue
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${tenant?.name ?: "Penyewa"} (${prop?.unitNumber ?: "-"})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${prop?.name ?: ""} • Bulan: ${payment.billingMonth}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when {
                                        isFull -> StatusPaid.copy(alpha = 0.15f)
                                        isPartial -> StatusPartial.copy(alpha = 0.15f)
                                        else -> StatusOverdue.copy(alpha = 0.15f)
                                    }
                                ) {
                                    Text(
                                        text = when {
                                            isFull -> "LUNAS"
                                            isPartial -> "SEPARA"
                                            else -> "TERTUNGGAK"
                                        },
                                        color = when {
                                            isFull -> StatusPaid
                                            isPartial -> StatusPartial
                                            else -> StatusOverdue
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Spacer(modifier = Modifier.height(8.dp))

                            // Satu Maklumat Satu Baris (Standardized for equal card height)
                            Text(
                                text = "Sewa Bulanan: RM ${String.format("%.2f", payment.amountDue)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Jumlah Dibayar: RM ${String.format("%.2f", payment.amountPaid)}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (payment.amountPaid > 0) EmeraldPrimaryLight else MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (payment.balanceDue > 0) "Baki Tertunggak: RM ${String.format("%.2f", payment.balanceDue)}" else "Baki Tertunggak: RM 0.00 (Lunas)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (payment.balanceDue > 0) StatusOverdue else StatusPaid
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Reference & Cloud Encrypted Token
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimaryLight, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = payment.cloudStorageEncryptedRef ?: "Disimpan Dalam Enkripsi Storan Awan",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Actions Row: All 4 buttons with uniform 40dp circular size -> 100% equal card structure!
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Resit Icon
                                IconButton(
                                    onClick = { viewModel.openDigitalReceipt(payment) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .testTag("btn_receipt_${payment.id}")
                                ) {
                                    Icon(
                                        Icons.Default.ReceiptLong,
                                        contentDescription = "Resit",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // 2. WhatsApp Icon
                                IconButton(
                                    onClick = {
                                        val phone = tenant?.phone ?: ""
                                        val reminder = if (payment.balanceDue > 0) {
                                            DigitalReceiptGenerator.buildOverdueReminderWhatsApp(payment, tenant, prop, profile)
                                        } else {
                                            "Salam Sejahtera ${tenant?.name ?: ""}, bayaran sewa bagi ${prop?.unitNumber} untuk bulan ${payment.billingMonth} telah disahkan lunas. Terima kasih!"
                                        }
                                        DigitalReceiptGenerator.shareViaWhatsApp(context, phone, reminder)
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366))
                                        .testTag("btn_wa_reminder_${payment.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = "WhatsApp",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // 3. Emel Icon
                                IconButton(
                                    onClick = {
                                        val email = tenant?.email ?: ""
                                        val reminder = if (payment.balanceDue > 0) {
                                            DigitalReceiptGenerator.buildOverdueReminderWhatsApp(payment, tenant, prop, profile)
                                        } else {
                                            "Salam Sejahtera ${tenant?.name ?: ""}, pengesahan bayaran sewa unit ${prop?.unitNumber} (${payment.billingMonth})."
                                        }
                                        DigitalReceiptGenerator.sendEmail(
                                            context,
                                            email,
                                            "Penyata Sewa - ${payment.billingMonth} (${prop?.unitNumber})",
                                            reminder
                                        )
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = "Emel",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // 4. Kemaskini Icon
                                IconButton(
                                    onClick = {
                                        viewModel.openRecordPayment(
                                            PaymentInitialData(
                                                propertyId = payment.propertyId,
                                                tenantId = payment.tenantId,
                                                billingMonth = payment.billingMonth,
                                                dueDate = payment.dueDate,
                                                amountDue = payment.amountDue,
                                                amountPaid = payment.amountPaid,
                                                paymentDate = payment.paymentDate ?: payment.dueDate,
                                                referenceNo = payment.ocrReferenceNo ?: "",
                                                bankName = payment.ocrBankName ?: "Maybank2u",
                                                notes = payment.notes
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Kemaskini",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showTenantDialog) {
            Dialog(
                onDismissRequest = { showTenantDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pilih Penyewa",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { showTenantDialog = false },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Tutup")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 380.dp)
                        ) {
                            item {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            "Semua Penyewa",
                                            fontWeight = if (filterTenantId == null) FontWeight.Bold else FontWeight.Normal,
                                            color = if (filterTenantId == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    trailingContent = {
                                        if (filterTenantId == null) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        filterTenantId = null
                                        showTenantDialog = false
                                    }
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }

                            items(tenants) { t ->
                                val isSelected = filterTenantId == t.id
                                val propName = propMap[t.propertyId]?.let { "${it.unitNumber} (${it.name})" } ?: "Tiada unit"
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            t.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    supportingContent = {
                                        Text(
                                            propName,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    trailingContent = {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        filterTenantId = t.id
                                        showTenantDialog = false
                                    }
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}
