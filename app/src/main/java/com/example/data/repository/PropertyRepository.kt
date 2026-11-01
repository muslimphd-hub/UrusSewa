package com.example.data.repository

import com.example.data.db.MaintenanceDao
import com.example.data.db.PaymentDao
import com.example.data.db.PropertyDao
import com.example.data.db.TenantDao
import com.example.data.model.Maintenance
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PropertyRepository(
    private val propertyDao: PropertyDao,
    private val tenantDao: TenantDao,
    private val paymentDao: PaymentDao,
    private val maintenanceDao: MaintenanceDao
) {
    val allProperties: Flow<List<Property>> = propertyDao.getAllProperties()
    val allTenants: Flow<List<Tenant>> = tenantDao.getAllTenants()
    val allPayments: Flow<List<Payment>> = paymentDao.getAllPayments()
    val allMaintenance: Flow<List<Maintenance>> = maintenanceDao.getAllMaintenance()
    val overduePayments: Flow<List<Payment>> = paymentDao.getOverduePayments()

    suspend fun insertProperty(property: Property): Long = propertyDao.insertProperty(property)
    suspend fun updateProperty(property: Property) = propertyDao.updateProperty(property)
    suspend fun deleteProperty(property: Property) = propertyDao.deleteProperty(property)

    suspend fun insertTenant(tenant: Tenant): Long = tenantDao.insertTenant(tenant)
    suspend fun updateTenant(tenant: Tenant) = tenantDao.updateTenant(tenant)
    suspend fun deleteTenant(tenant: Tenant) = tenantDao.deleteTenant(tenant)

    suspend fun insertPayment(payment: Payment): Long = paymentDao.insertPayment(payment)
    suspend fun updatePayment(payment: Payment) = paymentDao.updatePayment(payment)
    suspend fun deletePayment(payment: Payment) = paymentDao.deletePayment(payment)

    suspend fun insertMaintenance(item: Maintenance): Long = maintenanceDao.insertMaintenance(item)
    suspend fun updateMaintenance(item: Maintenance) = maintenanceDao.updateMaintenance(item)
    suspend fun deleteMaintenance(item: Maintenance) = maintenanceDao.deleteMaintenance(item)

    suspend fun seedSampleDataIfEmpty() {
        // Quick check
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val p1 = Property(
            id = 1,
            name = "Kondominium Residensi Vista",
            unitNumber = "A-12-03",
            type = "Kondominium",
            address = "Seksyen 15, Bandar Baru Bangi, Selangor",
            monthlyRent = 1600.0,
            depositAmount = 3200.0,
            bankLoanPayment = 1100.0,
            isOccupied = true,
            googleMapsUrl = "https://maps.google.com/?q=Bandar+Baru+Bangi+Residensi+Vista",
            notes = "Termasuk 2 lot parkir & kad akses"
        )
        val p2 = Property(
            id = 2,
            name = "Rumah Teres 2 Tingkat",
            unitNumber = "No. 18",
            type = "Rumah Teres",
            address = "Jalan Melati 3, Seksyen 7, Shah Alam, Selangor",
            monthlyRent = 1800.0,
            depositAmount = 3600.0,
            bankLoanPayment = 1250.0,
            isOccupied = true,
            googleMapsUrl = "https://maps.google.com/?q=Seksyen+7+Shah+Alam",
            notes = "Disewa keluarga Muslim, sewa 1hb setiap bulan"
        )
        val p3 = Property(
            id = 3,
            name = "Apartmen Seri Mawar",
            unitNumber = "B-04-10",
            type = "Apartmen",
            address = "Taman Mayang Jaya, Kelana Jaya, Petaling Jaya",
            monthlyRent = 1200.0,
            depositAmount = 2400.0,
            bankLoanPayment = 850.0,
            isOccupied = true,
            googleMapsUrl = "https://maps.google.com/?q=Kelana+Jaya+Apartmen+Seri+Mawar",
            notes = "Berdekatan LRT Kelana Jaya"
        )
        val p4 = Property(
            id = 4,
            name = "Studio Suite Cyberia",
            unitNumber = "S-09-02",
            type = "Studio",
            address = "Persiaran Multimedia, Cyberjaya, Selangor",
            monthlyRent = 1100.0,
            depositAmount = 2200.0,
            bankLoanPayment = 750.0,
            isOccupied = true,
            googleMapsUrl = "https://maps.google.com/?q=Cyberia+Cyberjaya",
            notes = "Fully furnished dengan kelengkapan elektrik"
        )
        val p5 = Property(
            id = 5,
            name = "Lot Kedai Ground Floor",
            unitNumber = "G-05",
            type = "Kedai",
            address = "Pusat Perniagaan Senawang, Seremban, Negeri Sembilan",
            monthlyRent = 2500.0,
            depositAmount = 5000.0,
            bankLoanPayment = 1600.0,
            isOccupied = false,
            googleMapsUrl = "https://maps.google.com/?q=Senawang+Seremban",
            notes = "Sedia untuk disewa, strategik menghadap jalan utama"
        )

        propertyDao.insertProperties(listOf(p1, p2, p3, p4, p5))

        val t1 = Tenant(
            id = 1,
            propertyId = 1,
            name = "Muhammad Faizal bin Yusof",
            phone = "+60128839210",
            email = "faizal.yusof@gmail.com",
            icNumber = "910512-10-5431",
            contractStartDate = "2026-01-01",
            contractEndDate = "2026-12-31",
            monthlyRent = 1600.0,
            securityDeposit = 3200.0,
            utilityDeposit = 500.0,
            dueDayOfMonth = 1,
            emergencyContact = "0193321102 (Isteri)",
            isContractActive = true,
            notes = "Penyewa yang berdisiplin & cermat"
        )
        val t2 = Tenant(
            id = 2,
            propertyId = 2,
            name = "Siti Nurhaliza binti Ramli",
            phone = "+60172345678",
            email = "siti.ramli95@yahoo.com",
            icNumber = "950218-14-6122",
            contractStartDate = "2025-11-15",
            contractEndDate = "2026-11-14", // Expiring in ~17 days!
            monthlyRent = 1800.0,
            securityDeposit = 3600.0,
            utilityDeposit = 500.0,
            dueDayOfMonth = 1,
            emergencyContact = "0129988776 (Abang)",
            isContractActive = true,
            notes = "Kontrak tamat tidak lama lagi. Perlu peringatan sambung sewa."
        )
        val t3 = Tenant(
            id = 3,
            propertyId = 3,
            name = "Tan Wei Meng",
            phone = "+60163344556",
            email = "weimeng.tan@hotmail.com",
            icNumber = "880903-08-5911",
            contractStartDate = "2026-03-01",
            contractEndDate = "2027-02-28",
            monthlyRent = 1200.0,
            securityDeposit = 2400.0,
            utilityDeposit = 400.0,
            dueDayOfMonth = 5,
            emergencyContact = "0165544332 (Kakak)",
            isContractActive = true,
            notes = "Jurutera perisian di Bandar Sunway"
        )
        val t4 = Tenant(
            id = 4,
            propertyId = 4,
            name = "Arvind a/l Krishnan",
            phone = "+60112345890",
            email = "arvind.k@gmail.com",
            icNumber = "940420-10-6789",
            contractStartDate = "2026-02-01",
            contractEndDate = "2027-01-31",
            monthlyRent = 1100.0,
            securityDeposit = 2200.0,
            utilityDeposit = 300.0,
            dueDayOfMonth = 7,
            emergencyContact = "0113322110 (Bapa)",
            isContractActive = true,
            notes = "Ada tunggakan sewa terkini"
        )
        val t5 = Tenant(
            id = 5,
            propertyId = null,
            name = "Azlan bin Mansor (Bekas)",
            phone = "+60198877665",
            email = "azlan.m@gmail.com",
            icNumber = "891122-10-5543",
            contractStartDate = "2024-01-01",
            contractEndDate = "2025-01-01",
            monthlyRent = 1500.0,
            dueDayOfMonth = 1,
            emergencyContact = "0192233445",
            isContractActive = false,
            notes = "Kontrak tamat tempoh sewa. Deposit telah dipulangkan."
        )

        tenantDao.insertTenants(listOf(t1, t2, t3, t4, t5))

        val pay1 = Payment(
            id = 1,
            propertyId = 1,
            tenantId = 1,
            billingMonth = currentMonth,
            dueDate = "$currentMonth-01",
            amountDue = 1600.0,
            amountPaid = 1600.0,
            balanceDue = 0.0,
            status = "LUNAS",
            paymentDate = "$currentMonth-01",
            receiptNumber = "RES-202610-0012",
            ocrReferenceNo = "MBB9928371029",
            ocrBankName = "Maybank2u",
            cloudStorageEncryptedRef = "AES256-GCM://urussewa-vault/receipts/MBB9928371029.enc",
            notes = "Bayaran penuh diterima tepat pada masanya."
        )

        val pay2 = Payment(
            id = 2,
            propertyId = 2,
            tenantId = 2,
            billingMonth = currentMonth,
            dueDate = "$currentMonth-01",
            amountDue = 1800.0,
            amountPaid = 800.0,
            balanceDue = 1000.0,
            status = "SEPARA",
            paymentDate = "$currentMonth-03",
            receiptNumber = "RES-202610-0019",
            ocrReferenceNo = "CIMB8271928301",
            ocrBankName = "CIMB Clicks",
            cloudStorageEncryptedRef = "AES256-GCM://urussewa-vault/receipts/CIMB8271928301.enc",
            notes = "Penyewa bayar RM800 dahulu, berjanji bayar baki RM1,000 hujung minggu."
        )

        val pay3 = Payment(
            id = 3,
            propertyId = 3,
            tenantId = 3,
            billingMonth = currentMonth,
            dueDate = "$currentMonth-05",
            amountDue = 1200.0,
            amountPaid = 1200.0,
            balanceDue = 0.0,
            status = "LUNAS",
            paymentDate = "$currentMonth-04",
            receiptNumber = "RES-202610-0027",
            ocrReferenceNo = "DNOW7712391028",
            ocrBankName = "DuitNow QR",
            cloudStorageEncryptedRef = "AES256-GCM://urussewa-vault/receipts/DNOW7712391028.enc",
            notes = "Bayaran melalui DuitNow QR."
        )

        val pay4 = Payment(
            id = 4,
            propertyId = 4,
            tenantId = 4,
            billingMonth = currentMonth,
            dueDate = "$currentMonth-07",
            amountDue = 1100.0,
            amountPaid = 0.0,
            balanceDue = 1100.0,
            status = "TERTUNGGAK",
            paymentDate = null,
            receiptNumber = "",
            ocrReferenceNo = null,
            ocrBankName = null,
            cloudStorageEncryptedRef = null,
            notes = "Tertunggak melebihi 20 hari. Perlu hantar notifikasi peringatan WhatsApp/Emel."
        )

        paymentDao.insertPayments(listOf(pay1, pay2, pay3, pay4))

        val m1 = Maintenance(
            id = 1,
            propertyId = 1,
            title = "Servis Cuci Kimia Aircond Master Room",
            category = "Aircond",
            cost = 150.0,
            dateReported = "$currentMonth-10",
            status = "SELESAI",
            contractorContact = "Pak Tam Aircond (013-4455667)",
            notes = "Servis tahunan berjalan lancar."
        )
        val m2 = Maintenance(
            id = 2,
            propertyId = 2,
            title = "Pembaikan Paip Sinki Dapur Bocor",
            category = "Paip & Saliran",
            cost = 80.0,
            dateReported = "$currentMonth-18",
            status = "SELESAI",
            contractorContact = "Plumber Azman (017-8899001)",
            notes = "Tukar getah sambungan paip PVC."
        )
        val m3 = Maintenance(
            id = 3,
            propertyId = 4,
            title = "Pemeriksaan Suis Pemanas Air (Water Heater)",
            category = "Elektrik",
            cost = 120.0,
            dateReported = "$currentMonth-25",
            status = "MENUNGGU",
            contractorContact = "Wireman Sam (012-6677889)",
            notes = "Menunggu alat ganti suis trip."
        )

        maintenanceDao.insertMaintenances(listOf(m1, m2, m3))
    }

    suspend fun resetToDefaultData() {
        propertyDao.clearAll()
        tenantDao.clearAll()
        paymentDao.clearAll()
        maintenanceDao.clearAll()
        seedSampleDataIfEmpty()
    }
}
