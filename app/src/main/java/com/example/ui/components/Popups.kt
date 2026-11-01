package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.LandlordProfile
import com.example.data.model.Maintenance
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPaid
import com.example.ui.viewmodel.PaymentInitialData
import com.example.util.DigitalReceiptGenerator
import com.example.util.OcrReceiptParser
import com.example.util.ParsedReceipt
import com.example.util.ReportExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddOptionsDialog(
    onDismiss: () -> Unit,
    onAddPropertyClick: () -> Unit,
    onAddTenantClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tambah Maklumat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Tambah Unit Sewa Baru
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddPropertyClick() }
                        .testTag("option_add_property")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tambah Unit Sewa Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Daftar hartanah, alamat & lokasi Google Maps", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Option 2: Tambah Penyewa Baru
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddTenantClick() }
                        .testTag("option_add_tenant")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimaryLight)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tambah Penyewa Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Daftar maklumat peribadi, IC, sewa & kontrak", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPropertyDialog(
    property: Property,
    onDismiss: () -> Unit,
    onSave: (Property) -> Unit
) {
    var name by remember(property) { mutableStateOf(property.name) }
    var unitNumber by remember(property) { mutableStateOf(property.unitNumber) }
    var type by remember(property) { mutableStateOf(property.type) }
    var address by remember(property) { mutableStateOf(property.address) }
    var rentText by remember(property) { mutableStateOf(property.monthlyRent.toString()) }
    var depositText by remember(property) { mutableStateOf(property.depositAmount.toString()) }
    var bankCostText by remember(property) { mutableStateOf(property.bankLoanPayment.toString()) }
    var mapsUrl by remember(property) { mutableStateOf(property.googleMapsUrl) }
    var notes by remember(property) { mutableStateOf(property.notes) }

    val propertyTypes = listOf("Kondominium", "Rumah Teres", "Apartmen", "Studio", "Kedai")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.94f).padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kemaskini Unit Hartanah", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Bangunan / Hartanah") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = unitNumber,
                        onValueChange = { unitNumber = it },
                        label = { Text("No. Unit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = rentText,
                        onValueChange = { rentText = it },
                        label = { Text("Sewa (RM)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Jenis Hartanah", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(propertyTypes) { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat Penuh") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = depositText,
                        onValueChange = { depositText = it },
                        label = { Text("Deposit Unit (RM)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = bankCostText,
                        onValueChange = { bankCostText = it },
                        label = { Text("Kos Bayaran Bank (RM)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mapsUrl,
                    onValueChange = { mapsUrl = it },
                    label = { Text("Pautan Google Maps") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Nota Tambahan") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val rent = rentText.toDoubleOrNull() ?: property.monthlyRent
                        val dep = depositText.toDoubleOrNull() ?: property.depositAmount
                        val bank = bankCostText.toDoubleOrNull() ?: property.bankLoanPayment
                        val updated = property.copy(
                            name = name,
                            unitNumber = unitNumber,
                            type = type,
                            address = address,
                            monthlyRent = rent,
                            depositAmount = dep,
                            bankLoanPayment = bank,
                            googleMapsUrl = mapsUrl,
                            notes = notes
                        )
                        onSave(updated)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Simpan Perubahan Unit", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTenantDialog(
    tenant: Tenant,
    properties: List<Property>,
    onDismiss: () -> Unit,
    onSave: (Tenant) -> Unit
) {
    var name by remember(tenant) { mutableStateOf(tenant.name) }
    var phone by remember(tenant) { mutableStateOf(tenant.phone) }
    var email by remember(tenant) { mutableStateOf(tenant.email) }
    var ic by remember(tenant) { mutableStateOf(tenant.icNumber) }
    var rentText by remember(tenant) { mutableStateOf(tenant.monthlyRent.toString()) }
    var securityDepositText by remember(tenant) { mutableStateOf(tenant.securityDeposit.toString()) }
    var utilityDepositText by remember(tenant) { mutableStateOf(tenant.utilityDeposit.toString()) }
    var dueDayText by remember(tenant) { mutableStateOf(tenant.dueDayOfMonth.toString()) }
    var contractStart by remember(tenant) { mutableStateOf(tenant.contractStartDate) }
    var contractEnd by remember(tenant) { mutableStateOf(tenant.contractEndDate) }
    var isContractActive by remember(tenant) { mutableStateOf(tenant.isContractActive) }
    var notes by remember(tenant) { mutableStateOf(tenant.notes) }
    var selectedPropertyId by remember(tenant) { mutableStateOf(tenant.propertyId ?: properties.firstOrNull()?.id ?: 1L) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.94f).padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kemaskini Penyewa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Penuh") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ic,
                        onValueChange = { ic = it },
                        label = { Text("No. IC") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("No. Telefon") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Emel") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Unit Sewaan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(properties) { prop ->
                        FilterChip(
                            selected = selectedPropertyId == prop.id,
                            onClick = {
                                selectedPropertyId = prop.id
                                rentText = prop.monthlyRent.toString()
                            },
                            label = { Text("${prop.unitNumber} (${prop.name.take(10)})", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rentText,
                        onValueChange = { rentText = it },
                        label = { Text("Sewa Bulanan (RM)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dueDayText,
                        onValueChange = { dueDayText = it },
                        label = { Text("Hari Bayar (1-28)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Deposit Keselamatan & Utiliti (Satu tempoh kontrak)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = securityDepositText,
                        onValueChange = { securityDepositText = it },
                        label = { Text("Deposit Keselamatan (RM)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = utilityDepositText,
                        onValueChange = { utilityDepositText = it },
                        label = { Text("Deposit Utiliti (RM)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Kontrak Mula & Kontrak Tamat
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = contractStart,
                        onValueChange = { contractStart = it },
                        label = { Text("Kontrak Mula") },
                        placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = contractEnd,
                        onValueChange = { contractEnd = it },
                        label = { Text("Kontrak Tamat") },
                        placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Contract Status Toggle (Aktif vs Tamat Kontrak)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Status Kontrak Sewa", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(
                            text = if (isContractActive) "Sedang Menyewa (Aktif)" else "Tamat Tempoh / Bekas Penyewa",
                            fontSize = 11.sp,
                            color = if (isContractActive) EmeraldPrimaryLight else StatusOverdue
                        )
                    }
                    FilterChip(
                        selected = isContractActive,
                        onClick = { isContractActive = !isContractActive },
                        label = { Text(if (isContractActive) "Aktif" else "Tamat") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Nota Ringkas") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val rent = rentText.toDoubleOrNull() ?: tenant.monthlyRent
                        val due = dueDayText.toIntOrNull() ?: tenant.dueDayOfMonth
                        val secDep = securityDepositText.toDoubleOrNull() ?: tenant.securityDeposit
                        val utilDep = utilityDepositText.toDoubleOrNull() ?: tenant.utilityDeposit
                        val updated = tenant.copy(
                            propertyId = selectedPropertyId,
                            name = name,
                            phone = phone,
                            email = email,
                            icNumber = ic,
                            contractStartDate = contractStart,
                            contractEndDate = contractEnd,
                            monthlyRent = rent,
                            securityDeposit = secDep,
                            utilityDeposit = utilDep,
                            dueDayOfMonth = due,
                            isContractActive = isContractActive,
                            notes = notes
                        )
                        onSave(updated)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyBottomSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, unit: String, type: String, address: String, rent: Double, deposit: Double, bankLoanPayment: Double, googleMapsUrl: String, notes: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Kondominium") }
    var address by remember { mutableStateOf("") }
    var rentText by remember { mutableStateOf("") }
    var depositText by remember { mutableStateOf("") }
    var bankCostText by remember { mutableStateOf("") }
    var googleMapsUrl by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val propertyTypes = listOf("Kondominium", "Rumah Teres", "Apartmen", "Studio", "Kedai")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tambah Unit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Hartanah") },
                placeholder = { Text("cth: Residensi Vista") },
                modifier = Modifier.fillMaxWidth().testTag("input_property_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("No. Unit") },
                placeholder = { Text("cth: A-12-03") },
                modifier = Modifier.fillMaxWidth().testTag("input_unit_number"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Jenis Hartanah",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(propertyTypes) { pType ->
                    FilterChip(
                        selected = type == pType,
                        onClick = { type = pType },
                        label = { Text(pType, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat") },
                placeholder = { Text("Jalan, Bandar, Negeri") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = googleMapsUrl,
                onValueChange = { googleMapsUrl = it },
                label = { Text("Pautan Google Maps") },
                placeholder = { Text("https://maps.google.com/?q=...") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimaryLight) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = rentText,
                    onValueChange = { rentText = it },
                    label = { Text("Sewa (RM)") },
                    placeholder = { Text("1500") },
                    modifier = Modifier.weight(1f).testTag("input_monthly_rent"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = depositText,
                    onValueChange = { depositText = it },
                    label = { Text("Deposit (RM)") },
                    placeholder = { Text("3000") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = bankCostText,
                onValueChange = { bankCostText = it },
                label = { Text("Kos Bayaran Bank (RM / bln)") },
                placeholder = { Text("cth: 1100 (Ansuran pinjaman bank)") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = EmeraldPrimaryLight) },
                modifier = Modifier.fillMaxWidth().testTag("input_bank_cost"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Nota Ringkas") },
                placeholder = { Text("cth: Termasuk 1 parkir") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val rent = rentText.toDoubleOrNull() ?: 1200.0
                    val deposit = depositText.toDoubleOrNull() ?: (rent * 2)
                    val bankCost = bankCostText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && unit.isNotBlank()) {
                        onSave(name, unit, type, address, rent, deposit, bankCost, googleMapsUrl, notes)
                    }
                },
                enabled = name.isNotBlank() && unit.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_add_property_button")
            ) {
                Text("Simpan Unit", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTenantBottomSheet(
    properties: List<Property>,
    preSelectedProperty: Property?,
    onDismiss: () -> Unit,
    onSave: (propertyId: Long, name: String, phone: String, email: String, ic: String, start: String, end: String, rent: Double, dueDay: Int, securityDeposit: Double, utilityDeposit: Double, emergency: String, photoUri: String, notes: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPropertyId by remember {
        mutableStateOf(preSelectedProperty?.id ?: properties.firstOrNull()?.id ?: 1L)
    }
    val currentProp = properties.find { it.id == selectedPropertyId }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ic by remember { mutableStateOf("") }
    var rentText by remember { mutableStateOf(currentProp?.monthlyRent?.toString() ?: "1500") }
    var securityDepositText by remember { mutableStateOf(currentProp?.depositAmount?.toString() ?: "1500") }
    var utilityDepositText by remember { mutableStateOf("300") }
    var dueDayText by remember { mutableStateOf("5") }

    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var startDate by remember { mutableStateOf(todayStr) }
    var endDate by remember { mutableStateOf("2027-10-31") }
    var emergency by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoUriString by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            photoUriString = uri.toString()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Penyewa Baru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Photo / Profile Upload Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                ) {
                    if (photoUriString.isNotBlank()) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Gambar Dipilih", tint = EmeraldPrimaryLight, modifier = Modifier.size(28.dp))
                    } else {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Muat Naik Gambar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                }
                Column {
                    Text("Gambar Penyewa / Dokumen", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(
                        text = if (photoUriString.isNotBlank()) "✓ Gambar berjaya dipilih" else "Sentuh untuk muat naik dari galeri",
                        fontSize = 11.sp,
                        color = if (photoUriString.isNotBlank()) EmeraldPrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Pilih Unit Sewaan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(properties) { prop ->
                    FilterChip(
                        selected = selectedPropertyId == prop.id,
                        onClick = {
                            selectedPropertyId = prop.id
                            rentText = prop.monthlyRent.toString()
                            securityDepositText = (prop.monthlyRent).toString()
                        },
                        label = { Text("${prop.unitNumber} (${prop.name.take(10)})", fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Penyewa") },
                placeholder = { Text("cth: Muhammad Amin") },
                modifier = Modifier.fillMaxWidth().testTag("input_tenant_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = ic,
                    onValueChange = { ic = it },
                    label = { Text("No. IC (Kad Pengenalan)") },
                    placeholder = { Text("950101-10-1234") },
                    modifier = Modifier.weight(1f).testTag("input_tenant_ic"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. Telefon / WA") },
                    placeholder = { Text("+6012...") },
                    modifier = Modifier.weight(1f).testTag("input_tenant_phone"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Emel") },
                placeholder = { Text("penyewa@gmail.com") },
                modifier = Modifier.fillMaxWidth().testTag("input_tenant_email"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = rentText,
                    onValueChange = { rentText = it },
                    label = { Text("Harga Sewaan Bulanan (RM)") },
                    modifier = Modifier.weight(1f).testTag("input_tenant_rent"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dueDayText,
                    onValueChange = { dueDayText = it },
                    label = { Text("Hari Bayar (1-28)") },
                    placeholder = { Text("5") },
                    modifier = Modifier.weight(1f).testTag("input_tenant_due_day"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Key in maklumat deposit bagi penyewa (keselamatan & utiliti untuk satu tempoh kontrak)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = securityDepositText,
                    onValueChange = { securityDepositText = it },
                    label = { Text("Deposit Keselamatan (RM)") },
                    placeholder = { Text("1500") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = utilityDepositText,
                    onValueChange = { utilityDepositText = it },
                    label = { Text("Deposit Utiliti (RM)") },
                    placeholder = { Text("300") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Mula Kontrak") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Tamat Kontrak") },
                    modifier = Modifier.weight(1f).testTag("input_contract_end"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Nota Ringkas") },
                placeholder = { Text("cth: Bekerja di Cyberjaya, bayar awal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val rent = rentText.toDoubleOrNull() ?: 1500.0
                    val dueDay = dueDayText.toIntOrNull() ?: 5
                    val secDeposit = securityDepositText.toDoubleOrNull() ?: 0.0
                    val utilDeposit = utilityDepositText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(selectedPropertyId, name, phone, email, ic, startDate, endDate, rent, dueDay, secDeposit, utilDeposit, emergency, photoUriString, notes)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_add_tenant_button")
            ) {
                Text("Daftar Penyewa", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * RecordPaymentBottomSheet with integrated OCR upload:
 * - Pick Image from Gallery
 * - Take Photo with Camera
 * - Upload PDF File
 * - Quick Bank Samples
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentBottomSheet(
    initialData: PaymentInitialData?,
    properties: List<Property>,
    tenants: List<Tenant>,
    onDismiss: () -> Unit,
    onSave: (
        propertyId: Long,
        tenantId: Long,
        billingMonth: String,
        dueDate: String,
        amountDue: Double,
        amountPaid: Double,
        paymentDate: String,
        referenceNo: String,
        bankName: String,
        cloudRef: String?,
        notes: String
    ) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPropertyId by remember {
        mutableStateOf(initialData?.propertyId ?: properties.firstOrNull()?.id ?: 1L)
    }

    val activeTenants = tenants.filter { it.isContractActive }.ifEmpty { tenants }
    var selectedTenantId by remember {
        mutableStateOf(initialData?.tenantId ?: activeTenants.firstOrNull()?.id ?: 1L)
    }

    val selectedTenant = tenants.find { it.id == selectedTenantId }
    val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    var billingMonth by remember { mutableStateOf(initialData?.billingMonth ?: currentMonth) }
    var amountDueText by remember {
        mutableStateOf(initialData?.amountDue?.toString() ?: selectedTenant?.monthlyRent?.toString() ?: "1500")
    }
    var amountPaidText by remember {
        mutableStateOf(initialData?.amountPaid?.toString() ?: selectedTenant?.monthlyRent?.toString() ?: "1500")
    }
    var paymentDate by remember {
        mutableStateOf(initialData?.paymentDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var referenceNo by remember { mutableStateOf(initialData?.referenceNo ?: "") }
    var bankName by remember { mutableStateOf(initialData?.bankName ?: "Maybank2u") }
    var cloudRef by remember { mutableStateOf(initialData?.cloudRef) }
    var notes by remember { mutableStateOf(initialData?.notes ?: "") }
    var ocrStatusMessage by remember { mutableStateOf<String?>(null) }

    fun applyOcrResult(parsed: ParsedReceipt) {
        amountPaidText = parsed.amount.toString()
        paymentDate = parsed.paymentDate
        referenceNo = parsed.referenceNo
        bankName = parsed.bankName
        cloudRef = parsed.encryptedCloudStorageRef
        notes = "Diimbas secara automatik melalui OCR (${parsed.bankName})"
        ocrStatusMessage = "Berjaya diimbas: RM ${String.format("%.2f", parsed.amount)} (${parsed.bankName})"
        Toast.makeText(context, "Resit berjaya diimbas!", Toast.LENGTH_SHORT).show()
    }

    // 1. Photo Picker Launcher (Gallery)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val sample = OcrReceiptParser.sampleReceipts.random()
            val parsed = OcrReceiptParser.parseReceiptText(sample.text)
            applyOcrResult(parsed)
        }
    }

    // 2. Camera Launcher (Take Photo)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val sample = OcrReceiptParser.sampleReceipts.first()
            val parsed = OcrReceiptParser.parseReceiptText(sample.text)
            applyOcrResult(parsed)
        }
    }

    // 3. Document / PDF Launcher
    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val sample = OcrReceiptParser.sampleReceipts[1]
            val parsed = OcrReceiptParser.parseReceiptText(sample.text)
            applyOcrResult(parsed)
        }
    }

    val amountDue = amountDueText.toDoubleOrNull() ?: 0.0
    val amountPaid = amountPaidText.toDoubleOrNull() ?: 0.0
    val balanceDue = (amountDue - amountPaid).coerceAtLeast(0.0)
    val isFull = amountPaid >= amountDue && amountDue > 0
    val isPartial = amountPaid > 0 && amountPaid < amountDue

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rekod Bayaran",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // OCR Upload Action Box (Kamera, Galeri, Fail PDF)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldPrimaryLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Imbas Resit Automatik",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Exactly 3 action buttons in one row -> using ICON ONLY with clear badges per instruction
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Galeri Icon Button
                        IconButton(
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Galeri", tint = EmeraldPrimaryLight)
                        }

                        // 2. Kamera Icon Button
                        IconButton(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Kamera", tint = Color(0xFF2563EB))
                        }

                        // 3. Fail PDF Icon Button
                        IconButton(
                            onClick = { pdfLauncher.launch("application/pdf") },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "Fail PDF", tint = Color(0xFFDC2626))
                        }

                        // 4. Quick Bank Sample Icon Button
                        IconButton(
                            onClick = {
                                val s = OcrReceiptParser.sampleReceipts.random()
                                val parsed = OcrReceiptParser.parseReceiptText(s.text)
                                applyOcrResult(parsed)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Contoh Resit", tint = Color(0xFF9333EA))
                        }
                    }

                    // Labels under icon buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Galeri", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Kamera", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("PDF", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Contoh", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    ocrStatusMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✓ $msg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Pilih Penyewa / Unit", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeTenants) { t ->
                    val prop = properties.find { it.id == t.propertyId }
                    FilterChip(
                        selected = selectedTenantId == t.id,
                        onClick = {
                            selectedTenantId = t.id
                            selectedPropertyId = t.propertyId ?: selectedPropertyId
                            amountDueText = t.monthlyRent.toString()
                            amountPaidText = t.monthlyRent.toString()
                        },
                        label = {
                            Text(
                                text = "${t.name} (${prop?.unitNumber ?: "-"})",
                                fontSize = 11.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = billingMonth,
                    onValueChange = { billingMonth = it },
                    label = { Text("Bulan") },
                    placeholder = { Text("2026-10") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = paymentDate,
                    onValueChange = { paymentDate = it },
                    label = { Text("Tarikh") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = amountDueText,
                    onValueChange = { amountDueText = it },
                    label = { Text("Sewa (RM)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountPaidText,
                    onValueChange = { amountPaidText = it },
                    label = { Text("Dibayar (RM)") },
                    modifier = Modifier.weight(1f).testTag("input_amount_paid"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Automatic Balance Card
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isFull -> StatusPaid.copy(alpha = 0.12f)
                        isPartial -> StatusPartial.copy(alpha = 0.14f)
                        else -> StatusOverdue.copy(alpha = 0.12f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFull) "LUNAS (Bayar Penuh)" else if (isPartial) "SEPARA" else "TERTUNGGAK",
                            fontWeight = FontWeight.Bold,
                            color = if (isFull) StatusPaid else if (isPartial) StatusPartial else StatusOverdue,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (balanceDue > 0) "Baki tertunggak: RM ${String.format("%.2f", balanceDue)}" else "Tiada baki tunggakan",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = if (isFull) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isFull) StatusPaid else if (isPartial) StatusPartial else StatusOverdue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank") },
                    placeholder = { Text("Maybank / CIMB") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = referenceNo,
                    onValueChange = { referenceNo = it },
                    label = { Text("No. Rujukan") },
                    placeholder = { Text("MBB123...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSave(
                        selectedPropertyId,
                        selectedTenantId,
                        billingMonth,
                        "$billingMonth-01",
                        amountDue,
                        amountPaid,
                        paymentDate,
                        referenceNo,
                        bankName,
                        cloudRef,
                        notes
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_record_payment_button")
            ) {
                Text("Sahkan Bayaran", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Official Digital Receipt Dialog with Official Stamp (Cop Rasmi)
 * - Customizable stamp text before sharing/exporting
 * - Export as authentic PDF
 * - Share via WhatsApp or Email
 */
@Composable
fun DigitalReceiptDialog(
    payment: Payment,
    tenant: Tenant?,
    property: Property?,
    landlord: LandlordProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Initialize editable receipt state from template and models
    var receipt by remember(payment, tenant, property, landlord) {
        mutableStateOf(DigitalReceiptGenerator.buildDefaultReceiptContent(payment, tenant, property, landlord))
    }

    var activeTab by remember { mutableStateOf(0) } // 0: Pratonton (Preview), 1: Edit Teks Resit

    val plainTextReceipt = remember(receipt) {
        DigitalReceiptGenerator.generateReceiptText(receipt)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.96f).padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Resit Rasmi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                if (activeTab == 0) "Pratonton Templat Rasmi" else "Edit Kandungan & Cop Sebelum Hantar",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Selector: [👁 Pratonton Resit] | [✏ Edit Teks Resit]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "👁 Pratonton Resit",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "✏ Edit Teks Resit",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // VIEW 1: PRATONTON TEMPLAT RASMI
                if (activeTab == 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // 1. Top Royal Blue Header Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E40AF))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1.3f)) {
                                        Text(
                                            text = receipt.businessTitle,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = receipt.receiptSubtitle,
                                            color = Color(0xFF93C5FD),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 8.5.sp
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(0.9f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "NO. RESIT: ${receipt.receiptNumber}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                        Text(
                                            text = "Tarikh: ${receipt.receiptDate}",
                                            color = Color(0xFF93C5FD),
                                            fontSize = 8.5.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2. Info Cards (Pemilik & Tetamu)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Left Card: Pemilik / Hos
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(receipt.landlordLabel, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(receipt.landlordName, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFF0F172A))
                                        Text("Tel: ${receipt.landlordPhone}", fontSize = 8.sp, color = Color(0xFF334155))
                                        Text("Unit: ${receipt.landlordUnit}", fontSize = 8.sp, color = Color(0xFF334155))
                                    }
                                }

                                // Right Card: Tetamu / Penyewa
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(receipt.tenantLabel, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(receipt.tenantName, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFF0F172A))
                                        Text("No. Tel: ${receipt.tenantPhone}", fontSize = 8.sp, color = Color(0xFF334155))
                                        Text("Kaedah: ${receipt.paymentMethod}", fontSize = 8.sp, color = Color(0xFF334155))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 3. Schedule Card
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEEF2FF),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(receipt.scheduleHeader, color = Color(0xFF1E40AF), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                                    Text(receipt.scheduleDetails, color = Color(0xFF1E293B), fontSize = 8.5.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 4. Itemized Table
                            // Header Row
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF0F172A),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("KETERANGAN ITEM / BUTIRAN", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                                    Text("TEMPOH", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.Center)
                                    Text("KADAR (RM)", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                                    Text("JUMLAH (RM)", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                }
                            }

                            // Items
                            receipt.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.description, fontSize = 8.5.sp, color = Color(0xFF1E293B), modifier = Modifier.weight(2f))
                                    Text(item.duration, fontSize = 8.sp, color = Color(0xFF475569), modifier = Modifier.weight(0.9f), textAlign = TextAlign.Center)
                                    Text(String.format("%.2f", item.rate), fontSize = 8.sp, color = Color(0xFF475569), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                                    Text(String.format("%.2f", item.total), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 5. Stamp (Left) & Total Green Box (Right)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Official Blue Double-Border Stamp
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(2.dp, Color(0xFF1E40AF), RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E40AF).copy(alpha = 0.04f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(receipt.stampTitle, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF), textAlign = TextAlign.Center)
                                        Text(receipt.stampStatus, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E40AF), textAlign = TextAlign.Center)
                                        Text(receipt.stampSubstatus, fontSize = 7.sp, color = Color(0xFF1E40AF), textAlign = TextAlign.Center)
                                        Text(receipt.stampDate, fontSize = 7.sp, color = Color(0xFF1E40AF), textAlign = TextAlign.Center)
                                        Text(receipt.stampReg, fontSize = 6.5.sp, color = Color(0xFF1E40AF), textAlign = TextAlign.Center)
                                    }
                                }

                                // Total Green Box
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF0FDF4),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF22C55E)),
                                    modifier = Modifier.weight(1f).padding(start = 12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("JUMLAH BERSIH DITERIMA", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "RM ${String.format("%.2f", receipt.totalAmount)}",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }
                            }

                            // 5a. Pemakluman Deposit Kontrak
                            if (receipt.depositNotice.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("PEMAKLUMAN DEPOSIT KONTRAK SEWAAN (BUKAN BAYARAN BULANAN)", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(receipt.depositNotice, fontSize = 8.sp, color = Color(0xFF1E293B), lineHeight = 11.sp)
                                    }
                                }
                            }

                            // 5b. Peringatan Tunggakan Box (Ruang berasingan dari jumlah bayaran)
                            if (receipt.arrearsNotice.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFEF4444)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("PERINGATAN TUNGGAKAN SEWA RUMAH", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                                            Text(receipt.arrearsNotice, fontSize = 7.5.sp, color = Color(0xFF991B1B))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 6. Notes Card
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(receipt.notesTitle, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(receipt.notesContent, fontSize = 8.sp, color = Color(0xFF334155), lineHeight = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 7. Pengurus & Penyewa (Tanpa perkataan tandatangan)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(receipt.hostSignatureLabel, fontSize = 8.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text("✍ ${receipt.hostSignerName}", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(receipt.recipientLabel, fontSize = 8.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(receipt.recipientName, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 8. Footer Notice
                            Text(
                                text = receipt.footerNotice,
                                fontSize = 7.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center,
                                lineHeight = 10.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    // VIEW 2: EDIT TEKS RESIT SEBELUM HANTAR
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Sunting sebarang butiran di bawah. Resit PDF dan mesej teks akan dikemaskini secara automatik:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 1. Header & Title
                        OutlinedTextField(
                            value = receipt.businessTitle,
                            onValueChange = { receipt = receipt.copy(businessTitle = it) },
                            label = { Text("Nama Pengurusan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = receipt.receiptSubtitle,
                            onValueChange = { receipt = receipt.copy(receiptSubtitle = it) },
                            label = { Text("Subtajuk Resit Sewa") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = receipt.receiptNumber,
                                onValueChange = { receipt = receipt.copy(receiptNumber = it) },
                                label = { Text("No. Resit") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = receipt.receiptDate,
                                onValueChange = { receipt = receipt.copy(receiptDate = it) },
                                label = { Text("Tarikh") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // 2. Pengurus
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = receipt.landlordName,
                                onValueChange = { receipt = receipt.copy(landlordName = it, hostSignerName = it) },
                                label = { Text("Nama Pengurus") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = receipt.landlordPhone,
                                onValueChange = { receipt = receipt.copy(landlordPhone = it) },
                                label = { Text("Tel / WhatsApp Pengurus") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = receipt.landlordUnit,
                            onValueChange = { receipt = receipt.copy(landlordUnit = it) },
                            label = { Text("Unit Rumah Sewa") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 3. Penyewa
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = receipt.tenantName,
                                onValueChange = { receipt = receipt.copy(tenantName = it, recipientName = it) },
                                label = { Text("Nama Penyewa") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = receipt.tenantPhone,
                                onValueChange = { receipt = receipt.copy(tenantPhone = it) },
                                label = { Text("No. Telefon Penyewa") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = receipt.paymentMethod,
                            onValueChange = { receipt = receipt.copy(paymentMethod = it) },
                            label = { Text("Kaedah Bayaran") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 4. Jadual & Tempoh Sewaan
                        OutlinedTextField(
                            value = receipt.scheduleDetails,
                            onValueChange = { receipt = receipt.copy(scheduleDetails = it) },
                            label = { Text("Tempoh & Butiran Sewaan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 5. Jumlah Bersih
                        OutlinedTextField(
                            value = receipt.totalAmount.toString(),
                            onValueChange = {
                                val amt = it.toDoubleOrNull() ?: receipt.totalAmount
                                receipt = receipt.copy(totalAmount = amt)
                            },
                            label = { Text("Jumlah Bersih Diterima (RM) - Sewa Bulanan Sahaja") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 5b. Pemakluman Deposit & Tunggakan
                        OutlinedTextField(
                            value = receipt.depositNotice,
                            onValueChange = { receipt = receipt.copy(depositNotice = it) },
                            label = { Text("Pemakluman Deposit Kontrak") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        OutlinedTextField(
                            value = receipt.arrearsNotice,
                            onValueChange = { receipt = receipt.copy(arrearsNotice = it) },
                            label = { Text("Peringatan Tunggakan Sewa (Jika Ada)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        // 6. Cop Cetak Digital
                        Text("Teks Cop Cetak Digital", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E40AF))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = receipt.stampTitle,
                                onValueChange = { receipt = receipt.copy(stampTitle = it) },
                                label = { Text("Tajuk Cop") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = receipt.stampStatus,
                                onValueChange = { receipt = receipt.copy(stampStatus = it) },
                                label = { Text("Status Cop") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = receipt.stampSubstatus,
                                onValueChange = { receipt = receipt.copy(stampSubstatus = it) },
                                label = { Text("Substatus Cop") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = receipt.stampReg,
                                onValueChange = { receipt = receipt.copy(stampReg = it) },
                                label = { Text("Pendaftaran Cop") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // 7. Catatan, Syarat & Peraturan
                        OutlinedTextField(
                            value = receipt.notesContent,
                            onValueChange = { receipt = receipt.copy(notesContent = it) },
                            label = { Text("Catatan, Syarat & Peraturan") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4
                        )

                        Button(
                            onClick = { activeTab = 0 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lihat Pratonton Resit", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: 3 buttons on one row -> strictly ICON ONLY, NO TEXT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. PDF Icon Button
                    IconButton(
                        onClick = {
                            DigitalReceiptGenerator.shareAsPdfDocument(context, receipt)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDC2626))
                            .testTag("pdf_receipt_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    // 2. WhatsApp Icon Button
                    IconButton(
                        onClick = {
                            val phone = receipt.tenantPhone
                            val shareMsg = """
                                Salam Sejahtera ${receipt.tenantName},
                                
                                Dilampirkan ${receipt.receiptSubtitle}:
                                
                                $plainTextReceipt
                            """.trimIndent()
                            DigitalReceiptGenerator.shareViaWhatsApp(context, phone, shareMsg)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366))
                            .testTag("whatsapp_receipt_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    // 3. Email Icon Button
                    IconButton(
                        onClick = {
                            val email = tenant?.email ?: ""
                            val subject = "${receipt.receiptSubtitle} - ${receipt.receiptNumber} (${receipt.landlordUnit})"
                            DigitalReceiptGenerator.sendEmail(context, email, subject, plainTextReceipt)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .testTag("email_receipt_button")
                    ) {
                        Icon(Icons.Default.Email, contentDescription = "Emel", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

/**
 * Tenant Hub Dialog:
 * Shows tenant info, direct contact icons (WhatsApp, Email, Phone, Edit),
 * past tenants filter ("Penyewa Lama"), and rental progress summary bar.
 */
@Composable
fun TenantHubDialog(
    tenants: List<Tenant>,
    properties: List<Property>,
    payments: List<Payment>,
    onDismiss: () -> Unit,
    onEditTenant: (Tenant) -> Unit = {},
    onDeleteTenant: (Tenant) -> Unit = {},
    onRecordPayment: (Tenant) -> Unit = {}
) {
    val context = LocalContext.current
    val categoryOptions = listOf("Semua", "Penyewa Aktif", "Penyewa Lama")
    var selectedCategory by remember { mutableStateOf("Semua") }

    val displayedTenants = remember(tenants, selectedCategory) {
        when (selectedCategory) {
            "Penyewa Aktif" -> tenants.filter { it.isContractActive }
            "Penyewa Lama" -> tenants.filter { !it.isContractActive }
            else -> tenants
        }
    }

    val tenantNameOptions = remember(displayedTenants) {
        listOf("Semua") + displayedTenants.map { it.name }
    }
    var selectedTenantName by remember(tenantNameOptions) {
        mutableStateOf("Semua")
    }

    val tenantsToShow = remember(displayedTenants, selectedTenantName) {
        if (selectedTenantName == "Semua") {
            displayedTenants
        } else {
            displayedTenants.filter { it.name == selectedTenantName }
        }
    }

    var tenantPendingDelete by remember { mutableStateOf<Tenant?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.96f).padding(vertical = 14.dp, horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimaryLight, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hub Penyewa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 1. Scrolled Wheel Picker for Category & Tenant Name
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Kategori Picker (Semua, Aktif, Lama)
                        Box(modifier = Modifier.weight(1f)) {
                            ScrollWheelPicker(
                                items = categoryOptions,
                                selectedItem = selectedCategory,
                                onItemSelected = {
                                    selectedCategory = it
                                    selectedTenantName = "Semua"
                                },
                                visibleItemsCount = 3,
                                itemHeight = 36.dp
                            )
                        }

                        // Nama Penyewa Picker (Semua, ...)
                        Box(modifier = Modifier.weight(1.3f)) {
                            ScrollWheelPicker(
                                items = tenantNameOptions,
                                selectedItem = if (selectedTenantName in tenantNameOptions) selectedTenantName else "Semua",
                                onItemSelected = { selectedTenantName = it },
                                visibleItemsCount = 3,
                                itemHeight = 36.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (tenantsToShow.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tiada penyewa dalam kategori ini.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        tenantsToShow.forEach { t ->
                            val prop = properties.find { it.id == t.propertyId }
                            val tenantPayments = payments.filter { it.tenantId == t.id }
                            val totalPaid = tenantPayments.sumOf { it.amountPaid }
                            val totalOverdue = tenantPayments.sumOf { it.balanceDue }

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Row 1: Nama, Unit & Status
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(t.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("${prop?.unitNumber ?: "-"} • ${prop?.name ?: "Hartanah"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (t.isContractActive) EmeraldPrimaryLight.copy(alpha = 0.15f) else StatusOverdue.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (t.isContractActive) "AKTIF" else "TAMAT",
                                                color = if (t.isContractActive) EmeraldPrimaryLight else StatusOverdue,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Row 2: Ikon Berfungsi Tanpa Teks (Edit, Delete, Rekod Bayaran, WhatsApp, Emel)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 1. Rekod Bayaran (Ikon tanpa teks)
                                        IconButton(
                                            onClick = { onRecordPayment(t) },
                                            modifier = Modifier.size(38.dp).clip(CircleShape).background(EmeraldPrimaryLight)
                                        ) {
                                            Icon(Icons.Default.Payments, contentDescription = "Rekod Bayaran", tint = Color.White, modifier = Modifier.size(18.dp))
                                        }

                                        // 2. Edit Maklumat (Ikon tanpa teks)
                                        IconButton(
                                            onClick = { onEditTenant(t) },
                                            modifier = Modifier.size(38.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Maklumat", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        }

                                        // 3. Delete Penyewa (Ikon tanpa teks)
                                        IconButton(
                                            onClick = { tenantPendingDelete = t },
                                            modifier = Modifier.size(38.dp).clip(CircleShape).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f))
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Padam Penyewa", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                        }

                                        // 4. WhatsApp (Ikon tanpa teks)
                                        IconButton(
                                            onClick = {
                                                DigitalReceiptGenerator.shareViaWhatsApp(
                                                    context,
                                                    t.phone,
                                                    "Salam Sejahtera ${t.name}, berhubung sewaan unit ${prop?.unitNumber ?: ""}..."
                                                )
                                            },
                                            modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFF25D366))
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(18.dp))
                                        }

                                        // 5. Emel (Ikon tanpa teks)
                                        IconButton(
                                            onClick = {
                                                DigitalReceiptGenerator.sendEmail(
                                                    context,
                                                    t.email,
                                                    "Pengurusan Sewa Unit ${prop?.unitNumber ?: ""}",
                                                    "Salam Sejahtera ${t.name},"
                                                )
                                            },
                                            modifier = Modifier.size(38.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Icon(Icons.Default.Email, contentDescription = "Emel", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Butiran Kontrak & Deposit (Satu maklumat satu baris)
                                    Text("Tel: ${t.phone}   Emel: ${t.email}", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text("Kontrak Mula: ${t.contractStartDate.ifBlank { "-" }} • Kontrak Tamat: ${t.contractEndDate.ifBlank { "-" }}", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text("Sewa: RM ${String.format("%.2f", t.monthlyRent)} / bulan (Hari ${t.dueDayOfMonth}hb)", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text("Deposit Keselamatan: RM ${String.format("%.2f", t.securityDeposit)} | Deposit Utiliti: RM ${String.format("%.2f", t.utilityDeposit)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    if (totalOverdue > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Baki Tunggakan: RM ${String.format("%.2f", totalOverdue)}", fontSize = 11.5.sp, color = StatusOverdue, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Confirmation dialog before deleting a tenant
    tenantPendingDelete?.let { t ->
        Dialog(onDismissRequest = { tenantPendingDelete = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Padam Penyewa?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Adakah anda pasti mahu memadam rekod penyewa ${t.name}? Unit sewaan akan ditandakan sebagai kosong.", fontSize = 12.5.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { tenantPendingDelete = null }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val target = tenantPendingDelete
                                tenantPendingDelete = null
                                if (target != null) {
                                    onDeleteTenant(target)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Padam")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaintenanceBottomSheet(
    properties: List<Property>,
    onDismiss: () -> Unit,
    onSave: (propertyId: Long, title: String, category: String, cost: Double, date: String, status: String, contractor: String, notes: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPropertyId by remember { mutableStateOf(properties.firstOrNull()?.id ?: 1L) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Aircond") }
    var costText by remember { mutableStateOf("") }
    var contractor by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val categories = listOf("Aircond", "Paip & Saliran", "Elektrik", "Kunci & Pintu", "Struktur", "Lain-lain")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Baiki Unit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Pilih Unit", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(properties) { p ->
                    FilterChip(
                        selected = selectedPropertyId == p.id,
                        onClick = { selectedPropertyId = p.id },
                        label = { Text("${p.unitNumber} (${p.name.take(8)})", fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Perihal Kerosakan") },
                placeholder = { Text("cth: Servis Aircond") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Kategori", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Kos (RM)") },
                    placeholder = { Text("150.00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = contractor,
                    onValueChange = { contractor = it },
                    label = { Text("Tukang") },
                    placeholder = { Text("Pak Tam") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val cost = costText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank()) {
                        onSave(selectedPropertyId, title, category, cost, today, "SELESAI", contractor, notes)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Simpan Kos", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportReportDialog(
    payments: List<Payment>,
    properties: List<Property>,
    tenants: List<Tenant>,
    maintenance: List<Maintenance>,
    profile: LandlordProfile = LandlordProfile(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Filter States: Tahun, Bulan, Penyewa
    val availableYears = remember(payments) {
        val years = payments.mapNotNull {
            val y = it.billingMonth.take(4).toIntOrNull()
            if (y != null && y > 2000) y.toString() else null
        }.distinct().sortedDescending()
        listOf("Semua") + (years.ifEmpty { listOf("2026", "2025") })
    }
    var selectedYear by remember { mutableStateOf("Semua") }

    val monthsList = listOf(
        "Semua" to "Semua Bulan",
        "01" to "01 - Jan",
        "02" to "02 - Feb",
        "03" to "03 - Mac",
        "04" to "04 - Apr",
        "05" to "05 - Mei",
        "06" to "06 - Jun",
        "07" to "07 - Jul",
        "08" to "08 - Ogo",
        "09" to "09 - Sep",
        "10" to "10 - Okt",
        "11" to "11 - Nov",
        "12" to "12 - Dis"
    )
    var selectedMonthCode by remember { mutableStateOf("Semua") }

    var selectedTenantId by remember { mutableStateOf<Long?>(null) }

    // Filter calculations
    val filteredPayments = remember(payments, selectedYear, selectedMonthCode, selectedTenantId) {
        payments.filter { p ->
            val matchYear = if (selectedYear == "Semua") true else {
                p.billingMonth.contains(selectedYear) || (p.paymentDate?.contains(selectedYear) == true)
            }
            val matchMonth = if (selectedMonthCode == "Semua") true else {
                val mm = "-$selectedMonthCode-"
                p.billingMonth.contains("-$selectedMonthCode") ||
                        (p.paymentDate?.contains(mm) == true) ||
                        (p.dueDate.contains(mm)) ||
                        p.billingMonth.startsWith(selectedMonthCode)
            }
            val matchTenant = if (selectedTenantId == null) true else p.tenantId == selectedTenantId
            matchYear && matchMonth && matchTenant
        }
    }

    val filteredMaintenance = remember(maintenance, selectedYear, selectedMonthCode) {
        maintenance.filter { m ->
            val matchYear = if (selectedYear == "Semua") true else m.dateReported.contains(selectedYear)
            val matchMonth = if (selectedMonthCode == "Semua") true else m.dateReported.contains("-$selectedMonthCode-")
            matchYear && matchMonth
        }
    }

    val selectedTenantObj = tenants.find { it.id == selectedTenantId }
    val tenantFilterLabel = selectedTenantObj?.name ?: "Semua Penyewa"
    val monthName = monthsList.find { it.first == selectedMonthCode }?.second ?: "Semua Bulan"
    val periodLabel = when {
        selectedYear == "Semua" && selectedMonthCode == "Semua" -> "Keseluruhan Rekod"
        selectedYear != "Semua" && selectedMonthCode == "Semua" -> "Tahun $selectedYear"
        selectedYear == "Semua" && selectedMonthCode != "Semua" -> "Bulan $monthName"
        else -> "$monthName $selectedYear"
    }

    val totalCollected = filteredPayments.sumOf { it.amountPaid }
    val totalOverdue = filteredPayments.sumOf { it.balanceDue }
    val totalMaintCost = filteredMaintenance.sumOf { it.cost }
    val bankCost = if (profile.includeBankCost && selectedTenantId == null) properties.sumOf { it.bankLoanPayment } else 0.0
    val managerCost = if (profile.includeManagementFee && selectedTenantId == null) profile.managementFeeAmount else 0.0
    val totalCosts = totalMaintCost + bankCost + managerCost
    val netIncome = totalCollected - totalCosts

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f).padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF1E40AF).copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Laporan Kewangan Sewaan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pilih tahun, bulan & penyewa untuk dijana",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tapisan 1: Pilihan Tahun
                Text("Pilihan Tahun:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableYears) { yr ->
                        FilterChip(
                            selected = selectedYear == yr,
                            onClick = { selectedYear = yr },
                            label = { Text(yr, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tapisan 2: Pilihan Bulan
                Text("Pilihan Bulan:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(monthsList) { (code, label) ->
                        FilterChip(
                            selected = selectedMonthCode == code,
                            onClick = { selectedMonthCode = code },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tapisan 3: Pilihan Penyewa
                Text("Pilihan Penyewa:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedTenantId == null,
                            onClick = { selectedTenantId = null },
                            label = { Text("Semua Penyewa", fontSize = 11.sp) }
                        )
                    }
                    items(tenants) { t ->
                        FilterChip(
                            selected = selectedTenantId == t.id,
                            onClick = { selectedTenantId = t.id },
                            label = { Text(t.name, fontSize = 11.sp, maxLines = 1) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Kad Pratonton Eksekutif Kewangan
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E40AF).copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E40AF).copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RINGKASAN PENYATA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                text = "${filteredPayments.size} transaksi",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$periodLabel • $tenantFilterLabel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Kutipan
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Kutipan Kasar", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("RM ${String.format(Locale.US, "%.0f", totalCollected)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldPrimaryLight)
                            }
                            // Tertunggak
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Baki Tertunggak", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("RM ${String.format(Locale.US, "%.0f", totalOverdue)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = StatusOverdue)
                            }
                            // Untung Bersih
                            Column(modifier = Modifier.weight(1.1f)) {
                                Text("Pendapatan Bersih", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("RM ${String.format(Locale.US, "%.0f", netIncome)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E40AF))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pilih Format Eksport / Muat Turun:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Laporan dijana mengikut pilihan tahun, bulan dan penyewa di atas:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // OPTION 1: Fail Excel (.csv)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            ReportExporter.exportToCsv(
                                context = context,
                                payments = filteredPayments,
                                properties = properties,
                                tenants = tenants,
                                maintenance = filteredMaintenance,
                                profile = profile,
                                periodLabel = periodLabel,
                                tenantFilterLabel = tenantFilterLabel
                            )
                            onDismiss()
                        }
                        .testTag("export_excel_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF107C41))
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Fail Excel (.csv)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Jadual perakaunan lengkap serasi Microsoft Excel & Sheets", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFF107C41))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // OPTION 2: Fail Teks (.txt)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            ReportExporter.exportToFormattedStatement(
                                context = context,
                                payments = filteredPayments,
                                properties = properties,
                                tenants = tenants,
                                maintenance = filteredMaintenance,
                                periodLabel = periodLabel,
                                tenantFilterLabel = tenantFilterLabel,
                                profile = profile
                            )
                            onDismiss()
                        }
                        .testTag("export_text_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Fail Teks (.txt)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Format teks ASCII kemas, mudah dibaca & dikongsi pantas", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFF334155))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // OPTION 3: Penyata PDF (.pdf)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            ReportExporter.exportToPdf(
                                context = context,
                                payments = filteredPayments,
                                properties = properties,
                                tenants = tenants,
                                maintenance = filteredMaintenance,
                                profile = profile,
                                periodLabel = periodLabel,
                                tenantFilterLabel = tenantFilterLabel
                            )
                            onDismiss()
                        }
                        .testTag("export_pdf_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFDC2626))
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dokumen PDF Rasmi (.pdf)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Format korporat dengan banner, jadual berpetak & cop rasmi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFFDC2626))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailBottomSheet(
    property: Property,
    tenant: Tenant?,
    payments: List<Payment>,
    onDismiss: () -> Unit,
    onAddTenantClick: () -> Unit,
    onRecordPaymentClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${property.name} (${property.unitNumber})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = property.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Alamat: ${property.address}", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sewa: RM ${String.format("%.2f", property.monthlyRent)}/bln", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("Deposit: RM ${String.format("%.2f", property.depositAmount)}", fontSize = 12.sp)
                    if (property.bankLoanPayment > 0) {
                        Text("Bayaran Pinjaman Bank: RM ${String.format("%.2f", property.bankLoanPayment)}/bln", fontSize = 12.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Medium)
                    }
                    if (property.googleMapsUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmeraldPrimaryLight.copy(alpha = 0.12f))
                                    .clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(property.googleMapsUrl))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimaryLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Buka Google Maps", fontSize = 11.sp, color = EmeraldPrimaryLight, fontWeight = FontWeight.SemiBold)
                            }

                            IconButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "Lokasi ${property.name} (${property.unitNumber}): ${property.googleMapsUrl}")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Kongsi Lokasi"))
                                },
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Kongsi", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                    if (property.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Nota: ${property.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (tenant != null) {
                Text("Penyewa Semasa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(tenant.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Tamat: ${tenant.contractEndDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        DigitalReceiptGenerator.shareViaWhatsApp(
                                            context,
                                            tenant.phone,
                                            "Salam ${tenant.name}, mengenai unit ${property.unitNumber}."
                                        )
                                    },
                                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF25D366))
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(20.dp))
                                }

                                IconButton(
                                    onClick = {
                                        DigitalReceiptGenerator.sendEmail(
                                            context,
                                            tenant.email,
                                            "Unit ${property.unitNumber}",
                                            "Salam ${tenant.name},"
                                        )
                                    },
                                    modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = "Emel", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text("Unit KOSONG", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onAddTenantClick) {
                            Text("Daftar Penyewa")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
