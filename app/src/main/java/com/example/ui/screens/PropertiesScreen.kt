package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
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
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusPaid
import com.example.ui.viewmodel.MainViewModel
import com.example.util.DigitalReceiptGenerator

@Composable
fun PropertiesScreen(
    viewModel: MainViewModel,
    properties: List<Property>,
    tenants: List<Tenant>,
    payments: List<Payment>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("SEMUA") } // SEMUA, DISEWA, KOSONG

    val filteredProperties = properties.filter { prop ->
        val matchesFilter = when (selectedFilter) {
            "DISEWA" -> prop.isOccupied
            "KOSONG" -> !prop.isOccupied
            else -> true
        }
        val matchesSearch = prop.name.contains(searchQuery, ignoreCase = true) ||
                prop.unitNumber.contains(searchQuery, ignoreCase = true) ||
                prop.address.contains(searchQuery, ignoreCase = true) ||
                prop.type.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    var propertyPendingDelete by remember { mutableStateOf<Property?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddProperty() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_property")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Hartanah")
            }
        },
        modifier = modifier.fillMaxSize().testTag("properties_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Senarai Hartanah & Unit Sewa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pengurusan unit, status penghuni & kontrak sewa",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari unit, bangunan, atau alamat...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("search_property_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips (Horizontally Scrollable LazyRow)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        "SEMUA" to "Semua (${properties.size})",
                        "DISEWA" to "Disewa (${properties.count { it.isOccupied }})",
                        "KOSONG" to "Kosong (${properties.count { !it.isOccupied }})"
                    )
                    items(filterOptions) { (filterKey, label) ->
                        FilterChip(
                            selected = selectedFilter == filterKey,
                            onClick = { selectedFilter = filterKey },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredProperties.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tiada hartanah dijumpai",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tekan butang '+' di bawah untuk menambah unit hartanah sewa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredProperties) { prop ->
                    val tenant = tenants.find { it.propertyId == prop.id }

                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { viewModel.selectPropertyDetail(prop) }
                            .testTag("property_card_${prop.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Baris 1: Unit, Nama, Ikon Edit, Ikon Delete (jika kosong), Status Badge
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
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Text(
                                            text = prop.unitNumber.take(3),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = prop.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${prop.type} • Unit ${prop.unitNumber}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Ikon Edit Maklumat Unit (Tanpa Teks)
                                    IconButton(
                                        onClick = { viewModel.openEditProperty(prop) },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Unit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }

                                    // Ikon Delete Unit jika kosong (Tanpa Teks)
                                    if (!prop.isOccupied) {
                                        IconButton(
                                            onClick = { propertyPendingDelete = prop },
                                            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f))
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Padam Unit", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (prop.isOccupied) StatusPaid.copy(alpha = 0.15f) else StatusOverdue.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (prop.isOccupied) "DISEWA" else "KOSONG",
                                            color = if (prop.isOccupied) StatusPaid else StatusOverdue,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Baris 2: Alamat & Pautan Google Maps
                            Text(
                                text = "Alamat: ${prop.address}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (prop.googleMapsUrl.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
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
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prop.googleMapsUrl))
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {}
                                            }
                                            .padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimaryLight, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Peta", fontSize = 11.sp, color = EmeraldPrimaryLight, fontWeight = FontWeight.SemiBold)
                                    }

                                    IconButton(
                                        onClick = {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, "Lokasi ${prop.name} (${prop.unitNumber}): ${prop.googleMapsUrl}")
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Kongsi Lokasi"))
                                        },
                                        modifier = Modifier.size(28.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Kongsi", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Baris 3: Kadar Sewa
                            Text(
                                text = "Sewa: RM ${String.format("%.2f", prop.monthlyRent)} / bulan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Baris 4: Deposit Unit
                            Text(
                                text = "Deposit Unit: RM ${String.format("%.2f", prop.depositAmount)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Baris 5: Kos Bayaran Bank
                            if (prop.bankLoanPayment > 0) {
                                Text(
                                    text = "Bayaran Bank: RM ${String.format("%.2f", prop.bankLoanPayment)} / bulan",
                                    fontSize = 12.sp,
                                    color = Color(0xFFD97706),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Blok Penyewa: Satu maklumat satu baris
                            if (tenant != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                 ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimaryLight, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(tenant.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }

                                            // Icon-only buttons for WhatsApp and Email
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        DigitalReceiptGenerator.shareViaWhatsApp(
                                                            context,
                                                            tenant.phone,
                                                            "Salam Sejahtera ${tenant.name}, mengenai sewaan di unit ${prop.unitNumber}..."
                                                        )
                                                    },
                                                    modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFF25D366))
                                                ) {
                                                    Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(18.dp))
                                                }

                                                IconButton(
                                                    onClick = {
                                                        DigitalReceiptGenerator.sendEmail(
                                                            context,
                                                            tenant.email,
                                                            "Pengurusan Sewa Unit ${prop.unitNumber}",
                                                            "Salam Sejahtera ${tenant.name},"
                                                        )
                                                    },
                                                    modifier = Modifier.size(38.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                                                ) {
                                                    Icon(Icons.Default.Email, contentDescription = "Emel", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        // Selain 'Kontrak Tamat' tambah juga 'Kontrak Mula' sebelumnya
                                        Text(
                                            text = "Kontrak Mula: ${tenant.contractStartDate.ifBlank { "-" }} • Kontrak Tamat: ${tenant.contractEndDate.ifBlank { "-" }}",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Deposit Keselamatan: RM ${String.format("%.2f", tenant.securityDeposit)} | Deposit Utiliti: RM ${String.format("%.2f", tenant.utilityDeposit)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { viewModel.openAddTenant(prop) },
                                    modifier = Modifier.fillMaxWidth().height(42.dp)
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Daftar Penyewa", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
    }

    // Confirmation dialog before deleting a property
    propertyPendingDelete?.let { p ->
        Dialog(onDismissRequest = { propertyPendingDelete = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Padam Unit Hartanah?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Adakah anda pasti mahu memadam unit ${p.unitNumber} (${p.name})? Tindakan ini tidak boleh diundur.", fontSize = 12.5.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { propertyPendingDelete = null }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val target = propertyPendingDelete
                                propertyPendingDelete = null
                                if (target != null) {
                                    viewModel.deleteProperty(target)
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
