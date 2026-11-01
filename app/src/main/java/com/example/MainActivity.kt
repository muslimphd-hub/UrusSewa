package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddMaintenanceBottomSheet
import com.example.ui.components.AddOptionsDialog
import com.example.ui.components.AddPropertyBottomSheet
import com.example.ui.components.AddTenantBottomSheet
import com.example.ui.components.DigitalReceiptDialog
import com.example.ui.components.EditPropertyDialog
import com.example.ui.components.EditTenantDialog
import com.example.ui.components.ExportReportDialog
import com.example.ui.components.PropertyDetailBottomSheet
import com.example.ui.components.RecordPaymentBottomSheet
import com.example.ui.components.TenantHubDialog
import com.example.ui.viewmodel.PaymentInitialData
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PaymentsScreen
import com.example.ui.screens.PropertiesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.UrusSewaTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val landlordProfile by viewModel.landlordProfile.collectAsStateWithLifecycle()

            // Dynamic Dark mode evaluation
            val isDark = when (landlordProfile.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            UrusSewaTheme(darkTheme = isDark) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val tenants by viewModel.tenants.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val maintenance by viewModel.maintenanceList.collectAsStateWithLifecycle()
    val landlordProfile by viewModel.landlordProfile.collectAsStateWithLifecycle()
    val paymentFilter by viewModel.paymentFilter.collectAsStateWithLifecycle()
    val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()
    val inAppNotifications by viewModel.inAppNotifications.collectAsStateWithLifecycle()

    // Dialog & Sheet States
    val showAddOptionsDialog by viewModel.showAddOptionsDialog.collectAsStateWithLifecycle()
    val showEditPropertyDialog by viewModel.showEditPropertyDialog.collectAsStateWithLifecycle()
    val showEditTenantDialog by viewModel.showEditTenantDialog.collectAsStateWithLifecycle()
    val showAddPropertySheet by viewModel.showAddPropertySheet.collectAsStateWithLifecycle()
    val showAddTenantSheet by viewModel.showAddTenantSheet.collectAsStateWithLifecycle()
    val showRecordPaymentSheet by viewModel.showRecordPaymentSheet.collectAsStateWithLifecycle()
    val showOcrScannerDialog by viewModel.showOcrScannerDialog.collectAsStateWithLifecycle()
    val showDigitalReceiptDialog by viewModel.showDigitalReceiptDialog.collectAsStateWithLifecycle()
    val showMaintenanceSheet by viewModel.showMaintenanceSheet.collectAsStateWithLifecycle()
    val showExportDialog by viewModel.showExportDialog.collectAsStateWithLifecycle()
    val showTenantHubDialog by viewModel.showTenantHubDialog.collectAsStateWithLifecycle()
    val selectedPropertyDetail by viewModel.selectedPropertyDetail.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.HOME,
                    onClick = { viewModel.setTab(AppTab.HOME) },
                    icon = {
                        Icon(
                            if (currentTab == AppTab.HOME) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Utama"
                        )
                    },
                    label = null,
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.PROPERTIES,
                    onClick = { viewModel.setTab(AppTab.PROPERTIES) },
                    icon = {
                        Icon(
                            if (currentTab == AppTab.PROPERTIES) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Hartanah"
                        )
                    },
                    label = null,
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("nav_tab_properties")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.PAYMENTS,
                    onClick = { viewModel.setTab(AppTab.PAYMENTS) },
                    icon = {
                        Icon(
                            if (currentTab == AppTab.PAYMENTS) Icons.Filled.Payments else Icons.Outlined.Payments,
                            contentDescription = "Kutipan"
                        )
                    },
                    label = null,
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("nav_tab_payments")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.CALENDAR,
                    onClick = { viewModel.setTab(AppTab.CALENDAR) },
                    icon = {
                        Icon(
                            if (currentTab == AppTab.CALENDAR) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Kalendar"
                        )
                    },
                    label = null,
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("nav_tab_calendar")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.SETTINGS,
                    onClick = { viewModel.setTab(AppTab.SETTINGS) },
                    icon = {
                        Icon(
                            if (currentTab == AppTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Tetapan"
                        )
                    },
                    label = null,
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                AppTab.HOME -> DashboardScreen(
                    viewModel = viewModel,
                    properties = properties,
                    tenants = tenants,
                    payments = payments,
                    maintenance = maintenance,
                    profile = landlordProfile,
                    notifications = inAppNotifications
                )
                AppTab.PROPERTIES -> PropertiesScreen(
                    viewModel = viewModel,
                    properties = properties,
                    tenants = tenants,
                    payments = payments
                )
                AppTab.PAYMENTS -> PaymentsScreen(
                    viewModel = viewModel,
                    payments = payments,
                    properties = properties,
                    tenants = tenants,
                    profile = landlordProfile,
                    filter = paymentFilter
                )
                AppTab.CALENDAR -> CalendarScreen(
                    viewModel = viewModel,
                    payments = payments,
                    properties = properties,
                    tenants = tenants,
                    maintenance = maintenance,
                    profile = landlordProfile,
                    selectedDate = selectedCalendarDate
                )
                AppTab.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    profile = landlordProfile,
                    payments = payments,
                    properties = properties,
                    tenants = tenants,
                    maintenance = maintenance
                )
            }
        }
    }

    // Modal Popups / Bottom Sheets
    if (showAddOptionsDialog) {
        AddOptionsDialog(
            onDismiss = { viewModel.closeAddOptions() },
            onAddPropertyClick = {
                viewModel.closeAddOptions()
                viewModel.openAddProperty()
            },
            onAddTenantClick = {
                viewModel.closeAddOptions()
                viewModel.openAddTenant()
            }
        )
    }

    showEditPropertyDialog?.let { propToEdit ->
        EditPropertyDialog(
            property = propToEdit,
            onDismiss = { viewModel.closeEditProperty() },
            onSave = { updatedProp ->
                viewModel.updateProperty(updatedProp)
            }
        )
    }

    showEditTenantDialog?.let { tenantToEdit ->
        EditTenantDialog(
            tenant = tenantToEdit,
            properties = properties,
            onDismiss = { viewModel.closeEditTenant() },
            onSave = { updatedTenant ->
                viewModel.updateTenant(updatedTenant)
                viewModel.closeEditTenant()
            }
        )
    }

    if (showAddPropertySheet) {
        AddPropertyBottomSheet(
            onDismiss = { viewModel.closeAddProperty() },
            onSave = { name, unit, type, address, rent, deposit, bankCost, mapsUrl, notes ->
                viewModel.addProperty(name, unit, type, address, rent, deposit, bankCost, mapsUrl, notes)
            }
        )
    }

    showAddTenantSheet?.let { req ->
        AddTenantBottomSheet(
            properties = properties,
            preSelectedProperty = req.preSelectedProperty,
            onDismiss = { viewModel.closeAddTenant() },
            onSave = { propId, name, phone, email, ic, start, end, rent, dueDay, secDep, utilDep, emergency, photoUri, notes ->
                viewModel.addTenant(propId, name, phone, email, ic, start, end, rent, secDep, utilDep, dueDay, emergency, photoUri, notes)
            }
        )
    }

    showRecordPaymentSheet?.let { initialData ->
        RecordPaymentBottomSheet(
            initialData = initialData,
            properties = properties,
            tenants = tenants,
            onDismiss = { viewModel.closeRecordPayment() },
            onSave = { propId, tenantId, month, dueDate, amountDue, amountPaid, payDate, refNo, bank, cloudRef, notes ->
                viewModel.recordPayment(propId, tenantId, month, dueDate, amountDue, amountPaid, payDate, refNo, bank, cloudRef, notes)
            }
        )
    }

    if (showOcrScannerDialog) {
        viewModel.closeOcrScanner()
        viewModel.openRecordPayment()
    }

    showDigitalReceiptDialog?.let { p ->
        val prop = properties.find { it.id == p.propertyId }
        val t = tenants.find { it.id == p.tenantId }
        DigitalReceiptDialog(
            payment = p,
            tenant = t,
            property = prop,
            landlord = landlordProfile,
            onDismiss = { viewModel.closeDigitalReceipt() }
        )
    }

    if (showMaintenanceSheet) {
        AddMaintenanceBottomSheet(
            properties = properties,
            onDismiss = { viewModel.closeMaintenanceSheet() },
            onSave = { propId, title, cat, cost, date, status, contractor, notes ->
                viewModel.addMaintenance(propId, title, cat, cost, date, status, contractor, notes)
            }
        )
    }

    if (showExportDialog) {
        ExportReportDialog(
            payments = payments,
            properties = properties,
            tenants = tenants,
            maintenance = maintenance,
            profile = landlordProfile,
            onDismiss = { viewModel.closeExportDialog() }
        )
    }

    if (showTenantHubDialog) {
        TenantHubDialog(
            tenants = tenants,
            properties = properties,
            payments = payments,
            onDismiss = { viewModel.closeTenantHub() },
            onEditTenant = { tenant ->
                viewModel.openEditTenant(tenant)
            },
            onDeleteTenant = { tenant ->
                viewModel.deleteTenant(tenant)
            },
            onRecordPayment = { tenant ->
                viewModel.closeTenantHub()
                viewModel.openRecordPayment(
                    PaymentInitialData(
                        propertyId = tenant.propertyId ?: 1L,
                        tenantId = tenant.id,
                        amountDue = tenant.monthlyRent
                    )
                )
            }
        )
    }

    selectedPropertyDetail?.let { prop ->
        val tenant = tenants.find { it.propertyId == prop.id }
        val propPayments = payments.filter { it.propertyId == prop.id }
        PropertyDetailBottomSheet(
            property = prop,
            tenant = tenant,
            payments = propPayments,
            onDismiss = { viewModel.selectPropertyDetail(null) },
            onAddTenantClick = {
                viewModel.selectPropertyDetail(null)
                viewModel.openAddTenant(prop)
            },
            onRecordPaymentClick = {
                viewModel.selectPropertyDetail(null)
                viewModel.openRecordPayment()
            }
        )
    }
}
