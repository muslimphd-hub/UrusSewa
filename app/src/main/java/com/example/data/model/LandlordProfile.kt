package com.example.data.model

data class LandlordProfile(
    val landlordName: String = "AL MUSLIM MUSTAPA",
    val phone: String = "0139358650",
    val email: String = "almuslim@ukm.edu.my",
    val bankName: String = "Maybank",
    val accountNumber: String = "1642 1234 5678",
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val appDesignTheme: String = "KOMPREHENSIF", // "KOMPREHENSIF", "ANDROID", "IOS", "MINIMALIST"
    val appDisplayMode: String = "KOMPREHENSIF", // "KOMPREHENSIF", "STANDARD", "RINGKAS"
    val widgetDisplayMode: String = "KOMPREHENSIF", // "KOMPREHENSIF", "RINGKAS"
    val notifyOverdue: Boolean = true,
    val notifyContractExpiry: Boolean = true,
    val notifyPushRealtime: Boolean = true,
    val officialStampText: String = "★ COP RASMI TUAN RUMAH ★\nDISAHKAN LUNAS\nBAYARAN SEWA DITERIMA\nPENGURUSAN SEWAAN",
    val businessName: String = "Pengurusan Hartanah & Rumah Sewa",
    val receiptSubtitleTemplate: String = "RESIT RASMI PEMBAYARAN SEWA RUMAH",
    val defaultNotes: String = "1. Resit ini adalah perakuan rasmi penerimaan bayaran sewa rumah.\n2. Bayaran bulanan hendaklah dijelaskan sebelum tarikh akhir perjanjian sewaan.\n3. Resit digital ini sah dan dijana oleh sistem UrusSewa.",
    val includeBankCost: Boolean = true,
    val managementFeeAmount: Double = 300.0,
    val includeManagementFee: Boolean = true
)
