package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.shadow
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
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPaid
import com.example.util.DigitalReceiptGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data holder for Unit Current Month Payment Status
 */
data class UnitStatusItem(
    val property: Property,
    val tenant: Tenant?,
    val payment: Payment?,
    val monthlyRent: Double,
    val amountPaid: Double,
    val balanceDue: Double,
    val status: String, // "LUNAS", "SEPARA", "TERTUNGGAK", "KOSONG"
    val dueDate: String,
    val paymentDate: String?
)

/**
 * Suis Togol Gaya iOS (iOS-style Toggle Switch)
 * Ringkas, responsif, dan tidak memakan ruang pada kad.
 */
@Composable
fun IosToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 18.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ios_toggle_offset"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF34C759) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        label = "ios_toggle_color"
    )

    Box(
        modifier = modifier
            .width(42.dp)
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

/**
 * Widget yang memaparkan status pembayaran bulan semasa untuk semua unit sewa/penyewa
 * dan tunggakan, serta link pantas WhatsApp untuk setiap penyewa.
 * Menyokong Mod Komprehensif dan Mod Ringkas.
 */
@Composable
fun RentalStatusWidget(
    properties: List<Property>,
    tenants: List<Tenant>,
    payments: List<Payment>,
    profile: LandlordProfile,
    modifier: Modifier = Modifier,
    onRecordPaymentClick: (Property, Tenant?) -> Unit = { _, _ -> },
    onOpenReceiptClick: (Payment) -> Unit = {},
    onModeChange: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val currentMonthStr = remember { SimpleDateFormat("MMMM yyyy", Locale("ms", "MY")).format(Date()) }
    val currentMonthNumeric = remember { SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) }
    val currentMonthFormatted = remember { SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date()) }

    // Toggle mode: sync with profile.widgetDisplayMode or local toggle
    var isComprehensive by remember(profile.widgetDisplayMode) {
        mutableStateOf(profile.widgetDisplayMode == "KOMPREHENSIF")
    }

    // Build status list for all properties
    val unitStatusList = remember(properties, tenants, payments) {
        properties.map { prop ->
            val tenant = tenants.find { it.propertyId == prop.id }
            val propPayments = payments.filter { it.propertyId == prop.id }

            // Find payment for current month, or the latest payment
            val curPayment = propPayments.find { p ->
                p.billingMonth.contains(currentMonthNumeric) ||
                        p.billingMonth.contains(SimpleDateFormat("MM", Locale.getDefault()).format(Date())) ||
                        p.billingMonth.contains(currentMonthStr, ignoreCase = true)
            } ?: propPayments.maxByOrNull { it.id }

            if (tenant == null) {
                UnitStatusItem(
                    property = prop,
                    tenant = null,
                    payment = null,
                    monthlyRent = prop.monthlyRent,
                    amountPaid = 0.0,
                    balanceDue = 0.0,
                    status = "KOSONG",
                    dueDate = "-",
                    paymentDate = null
                )
            } else if (curPayment != null) {
                UnitStatusItem(
                    property = prop,
                    tenant = tenant,
                    payment = curPayment,
                    monthlyRent = curPayment.amountDue.coerceAtLeast(prop.monthlyRent),
                    amountPaid = curPayment.amountPaid,
                    balanceDue = curPayment.balanceDue,
                    status = curPayment.status,
                    dueDate = curPayment.dueDate,
                    paymentDate = curPayment.paymentDate
                )
            } else {
                // Tenant active but no payment record yet for current month
                UnitStatusItem(
                    property = prop,
                    tenant = tenant,
                    payment = null,
                    monthlyRent = prop.monthlyRent,
                    amountPaid = 0.0,
                    balanceDue = prop.monthlyRent,
                    status = "TERTUNGGAK",
                    dueDate = "7hb ${currentMonthStr.take(3)}",
                    paymentDate = null
                )
            }
        }
    }

    // High level summary metrics
    val totalUnits = unitStatusList.size
    val lunasCount = unitStatusList.count { it.status == "LUNAS" }
    val separaCount = unitStatusList.count { it.status == "SEPARA" }
    val tunggakanCount = unitStatusList.count { it.status == "TERTUNGGAK" }
    val kosongCount = unitStatusList.count { it.status == "KOSONG" }
    val totalKutipan = unitStatusList.sumOf { it.amountPaid }
    val totalTunggakan = unitStatusList.sumOf { it.balanceDue }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("rental_status_widget")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Widget Header Bar (Compact & Space-saving)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E40AF).copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = "Status Sewa",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• $currentMonthFormatted",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Togol Gaya iOS (iOS-style Toggle Switch)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = if (isComprehensive) "Lengkap" else "Ringkas",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isComprehensive) Color(0xFF34C759) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IosToggleSwitch(
                        checked = isComprehensive,
                        onCheckedChange = { nextMode ->
                            isComprehensive = nextMode
                            onModeChange?.invoke(if (nextMode) "KOMPREHENSIF" else "RINGKAS")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ringkasan metrik sebaris padat gaya iOS (Single-line Capsule Metrics Bar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lunas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(StatusPaid)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$lunasCount Lunas",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusPaid
                    )
                }

                Text("•", color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), fontSize = 10.sp)

                // Tertunggak
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (tunggakanCount > 0) StatusOverdue else MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$tunggakanCount Tunggak",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tunggakanCount > 0) StatusOverdue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text("•", color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), fontSize = 10.sp)

                // Baki Tunggakan
                Text(
                    text = "Baki: RM ${String.format(Locale.US, "%.0f", totalTunggakan)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalTunggakan > 0) Color(0xFFEA580C) else StatusPaid
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Units List
            if (unitStatusList.isEmpty()) {
                Text(
                    text = "Tiada unit hartanah berdaftar lagi.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                unitStatusList.forEachIndexed { index, item ->
                    if (isComprehensive) {
                        // PAPARAN KOMPREHENSIF
                        ComprehensiveUnitCard(
                            item = item,
                            currentMonth = currentMonthStr,
                            profile = profile,
                            context = context,
                            onRecordPayment = { onRecordPaymentClick(item.property, item.tenant) },
                            onOpenReceipt = { item.payment?.let { onOpenReceiptClick(it) } }
                        )
                    } else {
                        // PAPARAN RINGKAS
                        CompactUnitCard(
                            item = item,
                            currentMonth = currentMonthStr,
                            profile = profile,
                            context = context
                        )
                    }

                    if (index < unitStatusList.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * Kad Komprehensif: Menunjukkan status lengkap, progress bar pembayaran sewa,
 * perincian kewangan, tarikh sewaan dan butang pantas WhatsApp & Panggilan.
 */
@Composable
private fun ComprehensiveUnitCard(
    item: UnitStatusItem,
    currentMonth: String,
    profile: LandlordProfile,
    context: Context,
    onRecordPayment: () -> Unit,
    onOpenReceipt: () -> Unit
) {
    val prop = item.property
    val tenant = item.tenant
    val isPaid = item.status == "LUNAS" && item.balanceDue <= 0
    val isPartial = item.status == "SEPARA" || (item.amountPaid > 0 && item.balanceDue > 0)
    val isVacant = item.status == "KOSONG" || tenant == null

    val statusColor = when {
        isVacant -> Color(0xFF64748B)
        isPaid -> StatusPaid
        isPartial -> StatusPartial
        else -> StatusOverdue
    }

    val progressRatio = if (item.monthlyRent > 0) {
        (item.amountPaid / item.monthlyRent).toFloat().coerceIn(0f, 1f)
    } else 1f

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Baris 1: No Unit & Nama Hartanah + Status Badge
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
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = when {
                                isVacant -> Icons.Default.Home
                                isPaid -> Icons.Default.CheckCircle
                                isPartial -> Icons.Default.HourglassBottom
                                else -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${prop.unitNumber} • ${prop.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (tenant != null) tenant.name else "Unit Kosong (Sedia Disewa)",
                            fontSize = 11.sp,
                            color = if (tenant != null) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF64748B)
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = when {
                            isVacant -> "KOSONG"
                            isPaid -> "LUNAS"
                            isPartial -> "SEPARA"
                            else -> "TERTUNGGAK"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar Bayaran (Jika ada penyewa)
            if (tenant != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kemajuan Bayaran",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progressRatio * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { progressRatio },
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Baris Maklumat Kewangan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sewa Bulan", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("RM ${String.format("%.2f", item.monthlyRent)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Jumlah Dibayar", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("RM ${String.format("%.2f", item.amountPaid)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (item.amountPaid > 0) EmeraldPrimaryLight else MaterialTheme.colorScheme.onSurface)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tunggakan", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (item.balanceDue > 0) "RM ${String.format("%.2f", item.balanceDue)}" else "RM 0.00",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.balanceDue > 0) StatusOverdue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Butang Tindakan (WhatsApp, Telefon, Resit/Rekod)
            if (tenant != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Link WhatsApp untuk penyewa (Peringatan / Penghargaan)
                    IconButton(
                        onClick = {
                            val msg = buildWhatsAppMessage(
                                tenant = tenant,
                                property = prop,
                                item = item,
                                currentMonth = currentMonth,
                                profile = profile
                            )
                            DigitalReceiptGenerator.shareViaWhatsApp(context, tenant.phone, msg)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366))
                            .testTag("whatsapp_unit_${prop.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "WhatsApp Penyewa",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 2. Butang Panggilan Telefon
                    IconButton(
                        onClick = {
                            try {
                                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tenant.phone}"))
                                context.startActivity(callIntent)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Hubungi Penyewa",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 3. Rekod Bayaran / Resit
                    IconButton(
                        onClick = {
                            if (item.payment != null) {
                                onOpenReceipt()
                            } else {
                                onRecordPayment()
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = if (item.payment != null) Icons.Default.ReceiptLong else Icons.Default.CreditCard,
                            contentDescription = if (item.payment != null) "Lihat Resit" else "Rekod Bayaran",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Kad Ringkas: Paparan padat untuk setiap unit sewa, status tunggakan,
 * dan butang terus WhatsApp.
 */
@Composable
private fun CompactUnitCard(
    item: UnitStatusItem,
    currentMonth: String,
    profile: LandlordProfile,
    context: Context
) {
    val prop = item.property
    val tenant = item.tenant
    val isPaid = item.status == "LUNAS" && item.balanceDue <= 0
    val isPartial = item.status == "SEPARA" || (item.amountPaid > 0 && item.balanceDue > 0)
    val isVacant = item.status == "KOSONG" || tenant == null

    val statusColor = when {
        isVacant -> Color(0xFF64748B)
        isPaid -> StatusPaid
        isPartial -> StatusPartial
        else -> StatusOverdue
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unit & Tenant
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${prop.unitNumber} (${tenant?.name ?: "Kosong"})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = if (item.balanceDue > 0) "Tunggakan: RM ${String.format(Locale.US, "%.0f", item.balanceDue)}" else "Lunas (RM ${String.format(Locale.US, "%.0f", item.amountPaid)})",
                        fontSize = 10.sp,
                        color = if (item.balanceDue > 0) StatusOverdue else EmeraldPrimaryLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // WhatsApp Button if tenant exists
            if (tenant != null) {
                IconButton(
                    onClick = {
                        val msg = buildWhatsAppMessage(
                            tenant = tenant,
                            property = prop,
                            item = item,
                            currentMonth = currentMonth,
                            profile = profile
                        )
                        DigitalReceiptGenerator.shareViaWhatsApp(context, tenant.phone, msg)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366))
                        .testTag("compact_whatsapp_unit_${prop.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "WhatsApp",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

/**
 * Menghasilkan teks pesanan WhatsApp rasmi yang mesra & jelas bagi setiap penyewa.
 */
private fun buildWhatsAppMessage(
    tenant: Tenant,
    property: Property,
    item: UnitStatusItem,
    currentMonth: String,
    profile: LandlordProfile
): String {
    return if (item.balanceDue > 0) {
        """
        Salam Sejahtera ${tenant.name},
        
        Peringatan mesra berkenaan bayaran sewa rumah bulan $currentMonth bagi unit ${property.unitNumber} (${property.name}).
        
        • Sewa Bulanan : RM ${String.format("%.2f", item.monthlyRent)}
        • Jumlah Dibayar: RM ${String.format("%.2f", item.amountPaid)}
        • Baki Tertunggak : RM ${String.format("%.2f", item.balanceDue)}
        • Tarikh Akhir: ${item.dueDate}
        
        Sila lakukan pembayaran ke akaun bank tuan rumah:
        Bank: ${profile.bankName}
        No. Akaun: ${profile.accountNumber}
        Penerima: ${profile.landlordName}
        
        Sila lampirkan resit transaksi selepas bayaran dibuat. Terima kasih atas kerjasama anda.
        """.trimIndent()
    } else {
        """
        Salam Sejahtera ${tenant.name},
        
        Terima kasih! Bayaran sewa rumah bulan $currentMonth bagi unit ${property.unitNumber} (${property.name}) sebanyak RM ${String.format("%.2f", item.amountPaid)} telah selamat diterima dan disahkan LUNAS.
        
        Resit rasmi telah direkodkan dalam sistem UrusSewa. Semoga hari anda menyenangkan!
        """.trimIndent()
    }
}
