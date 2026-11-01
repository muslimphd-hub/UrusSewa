package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
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
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    profile: LandlordProfile,
    payments: List<Payment>,
    properties: List<Property>,
    tenants: List<Tenant>,
    maintenance: List<Maintenance>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var landlordName by remember(profile) { mutableStateOf(profile.landlordName) }
    var phone by remember(profile) { mutableStateOf(profile.phone) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var bankName by remember(profile) { mutableStateOf(profile.bankName) }
    var accountNumber by remember(profile) { mutableStateOf(profile.accountNumber) }
    var officialStampText by remember(profile) { mutableStateOf(profile.officialStampText) }
    var businessName by remember(profile) { mutableStateOf(profile.businessName) }
    var receiptSubtitle by remember(profile) { mutableStateOf(profile.receiptSubtitleTemplate) }
    var defaultNotes by remember(profile) { mutableStateOf(profile.defaultNotes) }

    var themeMode by remember(profile) { mutableStateOf(profile.themeMode) }
    var appDisplayMode by remember(profile) { mutableStateOf(profile.appDisplayMode) }
    var widgetDisplayMode by remember(profile) { mutableStateOf(profile.widgetDisplayMode) }
    var notifyOverdue by remember(profile) { mutableStateOf(profile.notifyOverdue) }
    var notifyContract by remember(profile) { mutableStateOf(profile.notifyContractExpiry) }
    var notifyPush by remember(profile) { mutableStateOf(profile.notifyPushRealtime) }
    var includeBankCost by remember(profile) { mutableStateOf(profile.includeBankCost) }
    var includeManagementFee by remember(profile) { mutableStateOf(profile.includeManagementFee) }
    var managementFeeText by remember(profile) { mutableStateOf(if (profile.managementFeeAmount > 0) profile.managementFeeAmount.toInt().toString() else "300") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tetapan & Penyata",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Profil tuan rumah, tema, pemberitahuan & eksport fail",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Paparan & Tema (Mod Gelap)
            Text(
                text = "Paparan Tema Aplikasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pilih mod paparan yang selesa untuk waktu siang atau malam:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Mod Cerah (Light Mode)
                        IconButton(
                            onClick = {
                                themeMode = "LIGHT"
                                viewModel.saveLandlordProfile(profile.copy(themeMode = "LIGHT"))
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (themeMode == "LIGHT") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("theme_light_chip")
                        ) {
                            Icon(
                                Icons.Default.LightMode,
                                contentDescription = "Mod Cerah",
                                tint = if (themeMode == "LIGHT") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // 2. Mod Gelap (Dark Mode)
                        IconButton(
                            onClick = {
                                themeMode = "DARK"
                                viewModel.saveLandlordProfile(profile.copy(themeMode = "DARK"))
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (themeMode == "DARK") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("theme_dark_chip")
                        ) {
                            Icon(
                                Icons.Default.DarkMode,
                                contentDescription = "Mod Gelap",
                                tint = if (themeMode == "DARK") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // 3. Sistem (Auto/System)
                        IconButton(
                            onClick = {
                                themeMode = "SYSTEM"
                                viewModel.saveLandlordProfile(profile.copy(themeMode = "SYSTEM"))
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (themeMode == "SYSTEM") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("theme_system_chip")
                        ) {
                            Icon(
                                Icons.Default.Brightness4,
                                contentDescription = "Sistem",
                                tint = if (themeMode == "SYSTEM") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1B: Gaya Reka Bentuk Aplikasi (Android, iOS, Minimalis)
            Text(
                text = "Gaya Reka Bentuk Aplikasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pilih citarasa antaramuka mengikut sistem kegemaran anda:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    var currentDesign by remember(profile) { mutableStateOf(profile.appDesignTheme) }

                    val designOptions = listOf(
                        Triple("KOMPREHENSIF", "Komprehensif (Eksekutif)", "Paparan data menyeluruh, analisis penuh & semua butiran aset"),
                        Triple("ANDROID", "Android (M3)", "Google Material Design"),
                        Triple("IOS", "iOS (Cupertino)", "Gaya Apple & Rounded"),
                        Triple("MINIMAL", "Minimalis", "Reka Bentuk Ringkas")
                    )

                    designOptions.forEach { (key, title, subtitle) ->
                        val isSelected = currentDesign == key
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent)
                                .clickable {
                                    currentDesign = key
                                    viewModel.saveLandlordProfile(profile.copy(appDesignTheme = key))
                                    Toast.makeText(context, "Gaya $title dipilih", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (isSelected) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(24.dp).clip(CircleShape).background(EmeraldPrimaryLight)
                                ) {
                                    Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (key != designOptions.last().first) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1C: Pilihan Paparan Aplikasi Secara Umum
            Text(
                text = "Pilihan Paparan Aplikasi Secara Umum",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tahap perincian data & analitik bagi keseluruhan aplikasi:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val appModeOptions = listOf(
                        Triple("KOMPREHENSIF", "Mod Komprehensif (Lengkap)", "Memaparkan semua analitik mendalam, graf operasi, pecahan kos bank & pengurus, dan widget lengkap"),
                        Triple("STANDARD", "Mod Standard (Seimbang)", "Paparan seimbang dan standard bagi kegunaan harian"),
                        Triple("RINGKAS", "Mod Ringkas (Padat)", "Paparan padat dan ringkas bagi pemantauan pantas")
                    )

                    appModeOptions.forEach { (key, title, subtitle) ->
                        val isSelected = appDisplayMode == key
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent)
                                .clickable {
                                    appDisplayMode = key
                                    viewModel.saveLandlordProfile(profile.copy(appDisplayMode = key))
                                    Toast.makeText(context, "$title dipilih", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (isSelected) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(24.dp).clip(CircleShape).background(EmeraldPrimaryLight)
                                ) {
                                    Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (key != appModeOptions.last().first) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1D: Pilihan Paparan Widget Status Sewa
            Text(
                text = "Pilihan Paparan Widget Status Sewa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Format paparan widget status sewa bulan semasa di papan pemuka:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val widgetModeOptions = listOf(
                        Triple("KOMPREHENSIF", "Paparan Komprehensif (Disyorkan)", "Memaparkan status pembayaran bulan semasa untuk semua unit, progress bar sewaan, tarikh akhir, perincian dibayar/tunggakan, dan link terus WhatsApp"),
                        Triple("RINGKAS", "Paparan Ringkas", "Memaparkan senarai padat unit sewa, status badge, baki tunggakan & link WhatsApp sepintas lalu")
                    )

                    widgetModeOptions.forEach { (key, title, subtitle) ->
                        val isSelected = widgetDisplayMode == key
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent)
                                .clickable {
                                    widgetDisplayMode = key
                                    viewModel.saveLandlordProfile(profile.copy(widgetDisplayMode = key))
                                    Toast.makeText(context, "$title dipilih", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (isSelected) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(24.dp).clip(CircleShape).background(EmeraldPrimaryLight)
                                ) {
                                    Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (key != widgetModeOptions.last().first) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Profil Tuan Rumah & Akaun Bank
            Text(
                text = "Profil Tuan Rumah & Akaun Bayaran",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Maklumat ini dimasukkan secara automatik dalam resit rasmi dan mesej WhatsApp.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = landlordName,
                        onValueChange = { landlordName = it },
                        label = { Text("Nama Tuan Rumah / Pengurus") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("No. Telefon") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Emel") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Nama Bank") },
                            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                            placeholder = { Text("Maybank / CIMB") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { accountNumber = it },
                            label = { Text("No. Akaun Bank") },
                            placeholder = { Text("164212345678") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = officialStampText,
                        onValueChange = { officialStampText = it },
                        label = { Text("Teks Cop Rasmi Resit") },
                        placeholder = { Text("DISEMAK & DISAHKAN\nURUSSEWA MANAGEMENT\nLUNAS") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val updated = profile.copy(
                                landlordName = landlordName,
                                phone = phone,
                                email = email,
                                bankName = bankName,
                                accountNumber = accountNumber,
                                themeMode = themeMode,
                                appDisplayMode = appDisplayMode,
                                widgetDisplayMode = widgetDisplayMode,
                                notifyOverdue = notifyOverdue,
                                notifyContractExpiry = notifyContract,
                                notifyPushRealtime = notifyPush,
                                officialStampText = officialStampText,
                                businessName = businessName,
                                receiptSubtitleTemplate = receiptSubtitle,
                                defaultNotes = defaultNotes,
                                includeBankCost = includeBankCost,
                                includeManagementFee = includeManagementFee,
                                managementFeeAmount = managementFeeText.toDoubleOrNull() ?: profile.managementFeeAmount
                            )
                            viewModel.saveLandlordProfile(updated)
                            Toast.makeText(context, "Profil berjaya disimpan!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Profil", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2B: Pengiraan Kos Kewangan & Pengurus
            Text(
                text = "Pengiraan Kos Kewangan & Pengurus",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pilih kos operasi yang ingin diambil kira dalam penyata kewangan & untung bersih:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Toggle 1: Ambil Kira Kos Bayaran Bank
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ambil Kira Kos Bayaran Bank", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                "Tolak ansuran pinjaman bank setiap unit daripada kutipan sewa kasar",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = includeBankCost,
                            onCheckedChange = {
                                includeBankCost = it
                                viewModel.saveLandlordProfile(profile.copy(includeBankCost = it))
                            }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Toggle 2: Ambil Kira Kos Pengurus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ambil Kira Kos Pengurus", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                "Tolak upah/elaun pengurusan hartanah bulanan sebagai kos operasi",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = includeManagementFee,
                            onCheckedChange = {
                                includeManagementFee = it
                                viewModel.saveLandlordProfile(profile.copy(includeManagementFee = it))
                            }
                        )
                    }

                    if (includeManagementFee) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = managementFeeText,
                            onValueChange = {
                                managementFeeText = it
                                val fee = it.toDoubleOrNull() ?: 0.0
                                viewModel.saveLandlordProfile(profile.copy(managementFeeAmount = fee))
                            },
                            label = { Text("Kos Pengurus (RM / bulan)") },
                            placeholder = { Text("300") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimaryLight) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2C: Cop Cetak Digital & Templat Resit
            Text(
                text = "Cop Cetak Digital & Templat Resit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ubah suai cop rasmi dan teks templat yang dicetak pada resit pembayaran rasmi:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pratonton Cop Cetak Digital Rasmi:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Live Visual Rubber Stamp Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E40AF).copy(alpha = 0.05f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .border(2.dp, Color(0xFF1E40AF), RoundedCornerShape(10.dp))
                                .padding(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color(0xFF1E40AF), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    officialStampText.lines().forEachIndexed { idx, line ->
                                        if (line.isNotBlank()) {
                                            Text(
                                                text = line.trim(),
                                                color = Color(0xFF1E40AF),
                                                fontSize = if (idx == 0 || idx == 1) 11.sp else 9.5.sp,
                                                fontWeight = if (idx == 0 || idx == 1) FontWeight.Bold else FontWeight.SemiBold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = officialStampText,
                        onValueChange = { officialStampText = it },
                        label = { Text("Teks Cop Cetak Digital") },
                        placeholder = { Text("★ COP RASMI HOMESTAY ★\nDISAHKAN LUNAS\nPEMBAYARAN DITERIMA\nPENGURUSAN BERDAFTAR") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Nama Premis / Homestay / Perniagaan") },
                        placeholder = { Text("Homestay Paradise Tiny House") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = receiptSubtitle,
                        onValueChange = { receiptSubtitle = it },
                        label = { Text("Subtajuk Templat Resit") },
                        placeholder = { Text("RESIT RASMI PEMBAYARAN & TEMPAHAN HOMESTAY") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = defaultNotes,
                        onValueChange = { defaultNotes = it },
                        label = { Text("Catatan & Syarat Lalai Resit") },
                        placeholder = { Text("Terima kasih kerana memilih...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val updated = profile.copy(
                                officialStampText = officialStampText,
                                businessName = businessName,
                                receiptSubtitleTemplate = receiptSubtitle,
                                defaultNotes = defaultNotes
                            )
                            viewModel.saveLandlordProfile(updated)
                            Toast.makeText(context, "Cop & Templat resit berjaya dikemaskini!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_stamp_template_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Cop & Templat Resit", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Pilihan Pemberitahuan (Notifications)
            Text(
                text = "Pilihan Pemberitahuan Pintar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Toggle 1: Overdue
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Peringatan Sewa Tertunggak", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Beri amaran apabila penyewa belum melunaskan sewa selepas tarikh akhir", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = notifyOverdue,
                            onCheckedChange = {
                                notifyOverdue = it
                                viewModel.saveLandlordProfile(profile.copy(notifyOverdue = it))
                            }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Toggle 2: Contract Expiry
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Peringatan Tamat Kontrak Sewa", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Amaran awal 30 hari sebelum perjanjian sewaan tamat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = notifyContract,
                            onCheckedChange = {
                                notifyContract = it
                                viewModel.saveLandlordProfile(profile.copy(notifyContractExpiry = it))
                            }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Toggle 3: Realtime Push
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pemberitahuan Tolak Masa Nyata", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Pemberitahuan tolak sistem sejurus resit disahkan atau dibaca", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = notifyPush,
                            onCheckedChange = {
                                notifyPush = it
                                viewModel.saveLandlordProfile(profile.copy(notifyPushRealtime = it))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 4: Penyata Kewangan & Eksport Fail
            Text(
                text = "Penyata Kewangan & Laporan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Muat turun laporan lengkap sewaan, kutipan dan penyelenggaraan untuk rekod percukaian LHDN atau simpanan:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Format PDF / Excel CSV",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        IconButton(
                            onClick = { viewModel.openExportDialog() },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("export_statement_settings_button")
                        ) {
                            Icon(
                                Icons.Default.FileDownload,
                                contentDescription = "Muat Turun",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 5: Data & Keselamatan
            Text(
                text = "Keselamatan & Pengurusan Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimaryLight, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Storan Awan Berenkripsi AES-256", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Semua rekod resit, audit transaksi, dan data penyewa disimpan secara persendirian untuk kegunaan tuan rumah sahaja.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.resetData() },
                        modifier = Modifier.fillMaxWidth().testTag("reset_sample_data_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Muat Semula", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
