package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.LandlordProfile
import com.example.data.model.Maintenance
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import com.example.data.repository.PropertyRepository
import com.example.util.InAppNotification
import com.example.util.NotificationHelper
import com.example.util.NotificationType
import com.example.util.ParsedReceipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTab(val title: String) {
    HOME("Utama"),
    PROPERTIES("Hartanah"),
    PAYMENTS("Kutipan"),
    CALENDAR("Kalendar"),
    SETTINGS("Tetapan")
}

data class AddTenantRequest(val preSelectedProperty: Property? = null)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PropertyRepository(
        db.propertyDao(),
        db.tenantDao(),
        db.paymentDao(),
        db.maintenanceDao()
    )

    private val prefs = application.getSharedPreferences("urus_sewa_prefs", Context.MODE_PRIVATE)

    // Flows from DB
    val properties: StateFlow<List<Property>> = repository.allProperties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenants: StateFlow<List<Tenant>> = repository.allTenants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<Payment>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maintenanceList: StateFlow<List<Maintenance>> = repository.allMaintenance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI State
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab = _currentTab.asStateFlow()

    private val _landlordProfile = MutableStateFlow(loadLandlordProfile())
    val landlordProfile = _landlordProfile.asStateFlow()

    private val _propertyFilter = MutableStateFlow("SEMUA") // "SEMUA", "DISEWA", "KOSONG"
    val propertyFilter = _propertyFilter.asStateFlow()

    private val _paymentFilter = MutableStateFlow("SEMUA") // "SEMUA", "TERTUNGGAK", "LUNAS", "SEPARA"
    val paymentFilter = _paymentFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val selectedCalendarDate = _selectedCalendarDate.asStateFlow()

    // Dialog / Sheet states
    private val _showAddPropertySheet = MutableStateFlow(false)
    val showAddPropertySheet = _showAddPropertySheet.asStateFlow()

    private val _showAddTenantSheet = MutableStateFlow<AddTenantRequest?>(null)
    val showAddTenantSheet = _showAddTenantSheet.asStateFlow()

    private val _showRecordPaymentSheet = MutableStateFlow<PaymentInitialData?>(null)
    val showRecordPaymentSheet = _showRecordPaymentSheet.asStateFlow()

    private val _showOcrScannerDialog = MutableStateFlow(false)
    val showOcrScannerDialog = _showOcrScannerDialog.asStateFlow()

    private val _showDigitalReceiptDialog = MutableStateFlow<Payment?>(null)
    val showDigitalReceiptDialog = _showDigitalReceiptDialog.asStateFlow()

    private val _showMaintenanceSheet = MutableStateFlow(false)
    val showMaintenanceSheet = _showMaintenanceSheet.asStateFlow()

    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog = _showExportDialog.asStateFlow()

    private val _selectedPropertyDetail = MutableStateFlow<Property?>(null)
    val selectedPropertyDetail = _selectedPropertyDetail.asStateFlow()

    private val _inAppNotifications = MutableStateFlow<List<InAppNotification>>(emptyList())
    val inAppNotifications = _inAppNotifications.asStateFlow()

    init {
        NotificationHelper.initNotificationChannels(application)
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
            checkAutomaticAlerts()
        }
    }

    private val _showTenantHubDialog = MutableStateFlow(false)
    val showTenantHubDialog = _showTenantHubDialog.asStateFlow()

    private val _showAddOptionsDialog = MutableStateFlow(false)
    val showAddOptionsDialog = _showAddOptionsDialog.asStateFlow()

    private val _showEditTenantDialog = MutableStateFlow<Tenant?>(null)
    val showEditTenantDialog = _showEditTenantDialog.asStateFlow()

    private val _showEditPropertyDialog = MutableStateFlow<Property?>(null)
    val showEditPropertyDialog = _showEditPropertyDialog.asStateFlow()

    private fun loadLandlordProfile(): LandlordProfile {
        return LandlordProfile(
            landlordName = prefs.getString("landlord_name", "AL MUSLIM MUSTAPA") ?: "AL MUSLIM MUSTAPA",
            phone = prefs.getString("landlord_phone", "0139358650") ?: "0139358650",
            email = prefs.getString("landlord_email", "almuslim@ukm.edu.my") ?: "almuslim@ukm.edu.my",
            bankName = prefs.getString("landlord_bank", "Maybank") ?: "Maybank",
            accountNumber = prefs.getString("landlord_account", "1642 1234 5678") ?: "1642 1234 5678",
            themeMode = prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM",
            appDesignTheme = prefs.getString("app_design_theme", "KOMPREHENSIF") ?: "KOMPREHENSIF",
            appDisplayMode = prefs.getString("app_display_mode", "KOMPREHENSIF") ?: "KOMPREHENSIF",
            widgetDisplayMode = prefs.getString("widget_display_mode", "KOMPREHENSIF") ?: "KOMPREHENSIF",
            notifyOverdue = prefs.getBoolean("notify_overdue", true),
            notifyContractExpiry = prefs.getBoolean("notify_contract", true),
            notifyPushRealtime = prefs.getBoolean("notify_push", true),
            officialStampText = prefs.getString("official_stamp", "★ COP RASMI TUAN RUMAH ★\nDISAHKAN LUNAS\nBAYARAN SEWA DITERIMA\nPENGURUSAN SEWAAN") ?: "★ COP RASMI TUAN RUMAH ★\nDISAHKAN LUNAS\nBAYARAN SEWA DITERIMA\nPENGURUSAN SEWAAN",
            businessName = prefs.getString("business_name", "Pengurusan Hartanah & Rumah Sewa") ?: "Pengurusan Hartanah & Rumah Sewa",
            receiptSubtitleTemplate = prefs.getString("receipt_subtitle", "RESIT RASMI PEMBAYARAN SEWA RUMAH") ?: "RESIT RASMI PEMBAYARAN SEWA RUMAH",
            defaultNotes = prefs.getString("default_notes", "1. Resit ini adalah perakuan rasmi penerimaan bayaran sewa rumah.\n2. Bayaran bulanan hendaklah dijelaskan sebelum tarikh akhir perjanjian sewaan.\n3. Resit digital ini sah dan dijana oleh sistem UrusSewa.") ?: "1. Resit ini adalah perakuan rasmi penerimaan bayaran sewa rumah.\n2. Bayaran bulanan hendaklah dijelaskan sebelum tarikh akhir perjanjian sewaan.\n3. Resit digital ini sah dan dijana oleh sistem UrusSewa.",
            includeBankCost = prefs.getBoolean("include_bank_cost", true),
            managementFeeAmount = prefs.getFloat("management_fee_amount", 300.0f).toDouble(),
            includeManagementFee = prefs.getBoolean("include_management_fee", true)
        )
    }

    fun saveLandlordProfile(profile: LandlordProfile) {
        _landlordProfile.value = profile
        prefs.edit().apply {
            putString("landlord_name", profile.landlordName)
            putString("landlord_phone", profile.phone)
            putString("landlord_email", profile.email)
            putString("landlord_bank", profile.bankName)
            putString("landlord_account", profile.accountNumber)
            putString("theme_mode", profile.themeMode)
            putString("app_design_theme", profile.appDesignTheme)
            putString("app_display_mode", profile.appDisplayMode)
            putString("widget_display_mode", profile.widgetDisplayMode)
            putBoolean("notify_overdue", profile.notifyOverdue)
            putBoolean("notify_contract", profile.notifyContractExpiry)
            putBoolean("notify_push", profile.notifyPushRealtime)
            putString("official_stamp", profile.officialStampText)
            putString("business_name", profile.businessName)
            putString("receipt_subtitle", profile.receiptSubtitleTemplate)
            putString("default_notes", profile.defaultNotes)
            putBoolean("include_bank_cost", profile.includeBankCost)
            putFloat("management_fee_amount", profile.managementFeeAmount.toFloat())
            putBoolean("include_management_fee", profile.includeManagementFee)
            apply()
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setPropertyFilter(filter: String) {
        _propertyFilter.value = filter
    }

    fun setPaymentFilter(filter: String) {
        _paymentFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCalendarDate(date: String) {
        _selectedCalendarDate.value = date
    }

    // Modal controls
    fun openAddOptions() { _showAddOptionsDialog.value = true }
    fun closeAddOptions() { _showAddOptionsDialog.value = false }

    fun openAddProperty() {
        closeAddOptions()
        _showAddPropertySheet.value = true
    }
    fun closeAddProperty() { _showAddPropertySheet.value = false }

    fun openAddTenant(property: Property? = null) {
        closeAddOptions()
        _showAddTenantSheet.value = AddTenantRequest(property)
    }
    fun closeAddTenant() { _showAddTenantSheet.value = null }

    fun openEditProperty(property: Property) { _showEditPropertyDialog.value = property }
    fun closeEditProperty() { _showEditPropertyDialog.value = null }

    fun openEditTenant(tenant: Tenant) { _showEditTenantDialog.value = tenant }
    fun closeEditTenant() { _showEditTenantDialog.value = null }

    fun openRecordPayment(data: PaymentInitialData? = PaymentInitialData()) { _showRecordPaymentSheet.value = data }
    fun closeRecordPayment() { _showRecordPaymentSheet.value = null }

    fun openOcrScanner() { _showOcrScannerDialog.value = true }
    fun closeOcrScanner() { _showOcrScannerDialog.value = false }

    fun openDigitalReceipt(payment: Payment) { _showDigitalReceiptDialog.value = payment }
    fun closeDigitalReceipt() { _showDigitalReceiptDialog.value = null }

    fun openMaintenanceSheet() { _showMaintenanceSheet.value = true }
    fun closeMaintenanceSheet() { _showMaintenanceSheet.value = false }

    fun openExportDialog() { _showExportDialog.value = true }
    fun closeExportDialog() { _showExportDialog.value = false }

    fun openTenantHub() { _showTenantHubDialog.value = true }
    fun closeTenantHub() { _showTenantHubDialog.value = false }

    fun selectPropertyDetail(property: Property?) { _selectedPropertyDetail.value = property }

    // Actions
    fun addProperty(
        name: String,
        unit: String,
        type: String,
        address: String,
        rent: Double,
        deposit: Double,
        bankLoanPayment: Double = 0.0,
        googleMapsUrl: String,
        notes: String
    ) {
        viewModelScope.launch {
            val mapsUrl = googleMapsUrl.ifBlank {
                "https://maps.google.com/?q=${java.net.URLEncoder.encode("$name $address", "UTF-8")}"
            }
            val property = Property(
                name = name,
                unitNumber = unit,
                type = type,
                address = address,
                monthlyRent = rent,
                depositAmount = deposit,
                bankLoanPayment = bankLoanPayment,
                isOccupied = false,
                googleMapsUrl = mapsUrl,
                notes = notes
            )
            repository.insertProperty(property)
            closeAddProperty()
            triggerNotification(
                title = "Hartanah Baru Ditambah",
                message = "Unit $unit di $name telah berjaya didaftarkan.",
                type = NotificationType.MAINTENANCE
            )
        }
    }

    fun updateProperty(property: Property) {
        viewModelScope.launch {
            repository.updateProperty(property)
            closeEditProperty()
            triggerNotification(
                title = "Hartanah Dikemaskini",
                message = "Unit ${property.unitNumber} (${property.name}) telah dikemaskini.",
                type = NotificationType.MAINTENANCE
            )
        }
    }

    fun deleteProperty(property: Property) {
        viewModelScope.launch {
            repository.deleteProperty(property)
            triggerNotification(
                title = "Hartanah Dipadam",
                message = "Unit ${property.unitNumber} telah berjaya dipadam.",
                type = NotificationType.MAINTENANCE
            )
        }
    }

    fun addTenant(
        propertyId: Long,
        name: String,
        phone: String,
        email: String,
        ic: String,
        startDate: String,
        endDate: String,
        rent: Double,
        securityDeposit: Double = 0.0,
        utilityDeposit: Double = 0.0,
        dueDay: Int,
        emergency: String,
        photoUri: String,
        notes: String
    ) {
        viewModelScope.launch {
            val tenant = Tenant(
                propertyId = propertyId,
                name = name,
                phone = phone,
                email = email,
                icNumber = ic,
                contractStartDate = startDate,
                contractEndDate = endDate,
                monthlyRent = rent,
                securityDeposit = securityDeposit,
                utilityDeposit = utilityDeposit,
                dueDayOfMonth = dueDay,
                emergencyContact = emergency,
                photoUri = photoUri,
                isContractActive = true,
                notes = notes
            )
            val tenantId = repository.insertTenant(tenant)

            // Update property occupancy and sync deposit
            val prop = properties.value.find { it.id == propertyId }
            if (prop != null) {
                repository.updateProperty(
                    prop.copy(
                        isOccupied = true,
                        depositAmount = securityDeposit + utilityDeposit
                    )
                )
            }

            // Create initial pending payment for current month
            val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            val dueDateStr = "$currentMonth-${dueDay.toString().padStart(2, '0')}"
            val initialPayment = Payment(
                propertyId = propertyId,
                tenantId = tenantId,
                billingMonth = currentMonth,
                dueDate = dueDateStr,
                amountDue = rent,
                amountPaid = 0.0,
                balanceDue = rent,
                status = "BELUM_BAYAR",
                notes = "Rekod sewa bulan pertama"
            )
            repository.insertPayment(initialPayment)

            closeAddTenant()
            triggerNotification(
                title = "Penyewa Baru Didaftarkan",
                message = "$name telah diselaraskan ke unit ${prop?.unitNumber ?: ""}.",
                type = NotificationType.PAYMENT
            )
        }
    }

    fun updateTenant(tenant: Tenant) {
        viewModelScope.launch {
            repository.updateTenant(tenant)
            // Update property occupancy and sync deposit if contract is terminated or active
            val prop = properties.value.find { it.id == tenant.propertyId }
            if (prop != null && !tenant.isContractActive) {
                repository.updateProperty(prop.copy(isOccupied = false))
            } else if (prop != null && tenant.isContractActive) {
                repository.updateProperty(
                    prop.copy(
                        isOccupied = true,
                        depositAmount = tenant.securityDeposit + tenant.utilityDeposit
                    )
                )
            }
            closeEditTenant()
            triggerNotification(
                title = "Maklumat Penyewa Dikemaskini",
                message = "Maklumat bagi ${tenant.name} telah disimpan.",
                type = NotificationType.PAYMENT
            )
        }
    }

    fun deleteTenant(tenant: Tenant) {
        viewModelScope.launch {
            repository.deleteTenant(tenant)
            // Update property to unoccupied
            tenant.propertyId?.let { pid ->
                val prop = properties.value.find { it.id == pid }
                if (prop != null) {
                    repository.updateProperty(prop.copy(isOccupied = false))
                }
            }
            triggerNotification(
                title = "Penyewa Dipadam",
                message = "Rekod penyewa ${tenant.name} telah dipadam.",
                type = NotificationType.PAYMENT
            )
        }
    }

    fun recordPayment(
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
    ) {
        viewModelScope.launch {
            val balance = amountDue - amountPaid
            val status = when {
                balance <= 0.0 -> "LUNAS"
                amountPaid > 0.0 -> "SEPARA"
                else -> "TERTUNGGAK"
            }
            val receiptNum = "RES-${SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())}-${(1000..9999).random()}"

            val payment = Payment(
                propertyId = propertyId,
                tenantId = tenantId,
                billingMonth = billingMonth,
                dueDate = dueDate,
                amountDue = amountDue,
                amountPaid = amountPaid,
                balanceDue = if (balance < 0) 0.0 else balance,
                status = status,
                paymentDate = paymentDate,
                receiptNumber = receiptNum,
                ocrReferenceNo = referenceNo.ifBlank { null },
                ocrBankName = bankName.ifBlank { null },
                cloudStorageEncryptedRef = cloudRef ?: "AES256-GCM://urussewa-vault/${referenceNo.ifBlank { "MANUAL" }}.enc",
                notes = notes
            )
            repository.insertPayment(payment)
            closeRecordPayment()

            val tenantName = tenants.value.find { it.id == tenantId }?.name ?: "Penyewa"
            val statusLabel = if (status == "LUNAS") "Penuh" else "Separa (Baki RM ${String.format("%.2f", balance)})"

            triggerNotification(
                title = "Kemaskini Bayaran Sewa Masa Nyata",
                message = "Bayaran RM ${String.format("%.2f", amountPaid)} ($statusLabel) dari $tenantName telah disahkan.",
                type = NotificationType.PAYMENT,
                isPush = true
            )
        }
    }

    fun applyOcrResultToPayment(parsed: ParsedReceipt) {
        closeOcrScanner()
        // Try to match tenant by name or first tenant
        val matchedTenant = tenants.value.find {
            it.name.contains(parsed.payerName, ignoreCase = true) ||
                    parsed.rawText.contains(it.name, ignoreCase = true)
        } ?: tenants.value.firstOrNull()

        val matchedProperty = properties.value.find { it.id == matchedTenant?.propertyId }

        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val defaultDue = matchedTenant?.monthlyRent ?: parsed.amount

        openRecordPayment(
            PaymentInitialData(
                propertyId = matchedProperty?.id ?: 1,
                tenantId = matchedTenant?.id ?: 1,
                billingMonth = currentMonth,
                dueDate = "$currentMonth-01",
                amountDue = defaultDue,
                amountPaid = parsed.amount,
                paymentDate = parsed.paymentDate,
                referenceNo = parsed.referenceNo,
                bankName = parsed.bankName,
                cloudRef = parsed.encryptedCloudStorageRef,
                notes = "Diimbas secara automatik melalui OCR (${parsed.bankName})"
            )
        )
    }

    fun addMaintenance(
        propertyId: Long,
        title: String,
        category: String,
        cost: Double,
        date: String,
        status: String,
        contractor: String,
        notes: String
    ) {
        viewModelScope.launch {
            val item = Maintenance(
                propertyId = propertyId,
                title = title,
                category = category,
                cost = cost,
                dateReported = date,
                status = status,
                contractorContact = contractor,
                notes = notes
            )
            repository.insertMaintenance(item)
            closeMaintenanceSheet()
            triggerNotification(
                title = "Kos Penyelenggaraan Dicatat",
                message = "$title bagi unit bernilai RM ${String.format("%.2f", cost)} disimpan.",
                type = NotificationType.MAINTENANCE
            )
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetToDefaultData()
            triggerNotification(
                title = "Data Contoh Dimuatkan",
                message = "Sistem telah menetapkan semula data contoh hartanah dan sewaan.",
                type = NotificationType.PAYMENT
            )
        }
    }

    private fun triggerNotification(
        title: String,
        message: String,
        type: NotificationType,
        isPush: Boolean = false
    ) {
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val item = InAppNotification(
            title = title,
            message = message,
            timestamp = timeStr,
            type = type
        )
        _inAppNotifications.value = listOf(item) + _inAppNotifications.value.take(19)

        if (landlordProfile.value.notifyPushRealtime || isPush) {
            NotificationHelper.sendPushNotification(
                getApplication(),
                title = title,
                message = message,
                isAlert = type == NotificationType.OVERDUE || type == NotificationType.CONTRACT
            )
        }
    }

    private fun checkAutomaticAlerts() {
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Check overdue payments
        val overdueCount = payments.value.count { it.status == "TERTUNGGAK" || it.balanceDue > 0 }
        if (overdueCount > 0 && landlordProfile.value.notifyOverdue) {
            triggerNotification(
                title = "Peringatan Sewa Tertunggak",
                message = "Terdapat $overdueCount unit yang mempunyai tunggakan sewa yang memerlukan perhatian.",
                type = NotificationType.OVERDUE,
                isPush = true
            )
        }

        // Check contract expiries (within 30 days)
        for (tenant in tenants.value) {
            try {
                val endDate = sdf.parse(tenant.contractEndDate)
                if (endDate != null) {
                    val diffDays = (endDate.time - now) / (1000 * 60 * 60 * 24)
                    if (diffDays in 0..30 && landlordProfile.value.notifyContractExpiry) {
                        triggerNotification(
                            title = "Peringatan Kontrak Tamat",
                            message = "Kontrak bagi ${tenant.name} akan tamat dalam tempoh $diffDays hari.",
                            type = NotificationType.CONTRACT,
                            isPush = true
                        )
                    }
                }
            } catch (_: Exception) {}
        }
    }
}

data class PaymentInitialData(
    val propertyId: Long = 1,
    val tenantId: Long = 1,
    val billingMonth: String = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()),
    val dueDate: String = SimpleDateFormat("yyyy-MM-01", Locale.getDefault()).format(Date()),
    val amountDue: Double = 1500.0,
    val amountPaid: Double = 1500.0,
    val paymentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val referenceNo: String = "",
    val bankName: String = "Maybank2u",
    val cloudRef: String? = null,
    val notes: String = ""
)
