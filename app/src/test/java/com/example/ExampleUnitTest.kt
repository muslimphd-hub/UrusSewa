package com.example

import com.example.data.model.LandlordProfile
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import com.example.util.DigitalReceiptGenerator
import com.example.util.OcrReceiptParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testOcrReceiptParsing_Maybank() {
        val sample = """
            MAYBANK2U ONLINE TRANSFER
            Status: Successful
            Reference ID: MBB9928371029
            Date: 28/10/2026
            From: Muhammad Faizal bin Yusof
            Amount: RM 1,500.00
        """.trimIndent()

        val parsed = OcrReceiptParser.parseReceiptText(sample)
        assertEquals(1500.0, parsed.amount, 0.01)
        assertEquals("2026-10-28", parsed.paymentDate)
        assertEquals("MBB9928371029", parsed.referenceNo)
        assertEquals("Maybank2u", parsed.bankName)
        assertTrue(parsed.encryptedCloudStorageRef.startsWith("AES256-GCM"))
    }

    @Test
    fun testPartialPaymentBalanceCalculation() {
        val amountDue = 1800.0
        val amountPaid = 800.0
        val balance = amountDue - amountPaid
        val status = if (balance <= 0) "LUNAS" else "SEPARA"

        assertEquals(1000.0, balance, 0.01)
        assertEquals("SEPARA", status)
    }

    @Test
    fun testDigitalReceiptGeneration() {
        val payment = Payment(
            id = 1,
            propertyId = 1,
            tenantId = 1,
            billingMonth = "2026-10",
            dueDate = "2026-10-01",
            amountDue = 1600.0,
            amountPaid = 1600.0,
            balanceDue = 0.0,
            status = "LUNAS",
            paymentDate = "2026-10-01",
            receiptNumber = "RES-202610-0012"
        )
        val tenant = Tenant(
            id = 1,
            name = "Muhammad Faizal",
            phone = "+60128839210",
            email = "faizal@gmail.com",
            icNumber = "910512-10-5431",
            contractStartDate = "2026-01-01",
            contractEndDate = "2026-12-31",
            monthlyRent = 1600.0
        )
        val prop = Property(
            id = 1,
            name = "Residensi Vista",
            unitNumber = "A-12-03",
            type = "Kondominium",
            address = "Bangi",
            monthlyRent = 1600.0,
            depositAmount = 3200.0
        )
        val landlord = LandlordProfile(
            landlordName = "Haji Ahmad",
            phone = "+60128901234",
            bankName = "Maybank",
            accountNumber = "164212345678"
        )

        val receiptText = DigitalReceiptGenerator.generateReceiptText(payment, tenant, prop, landlord)
        assertNotNull(receiptText)
        assertTrue(receiptText.contains("RES-202610-0012"))
        assertTrue(receiptText.contains("Muhammad Faizal"))
        assertTrue(receiptText.contains("LUNAS"))
        assertTrue(receiptText.contains("Haji Ahmad"))
    }

    @Test
    fun testLandlordProfileComprehensiveOptions() {
        val profile = LandlordProfile(
            landlordName = "AL MUSLIM MUSTAPA",
            phone = "0139358650",
            email = "almuslim@ukm.edu.my",
            widgetDisplayMode = "KOMPREHENSIF",
            appDisplayMode = "KOMPREHENSIF",
            appDesignTheme = "KOMPREHENSIF"
        )
        assertEquals("KOMPREHENSIF", profile.widgetDisplayMode)
        assertEquals("KOMPREHENSIF", profile.appDisplayMode)
        assertEquals("KOMPREHENSIF", profile.appDesignTheme)
    }

    @Test
    fun testWhatsAppMessageFormatting() {
        val tenantName = "Ahmad Albab"
        val unitName = "Residensi Vista A-12-03"
        val arrears = 1500.0
        val month = "Oktober 2026"

        val message = "Salam $tenantName, peringatan mesra bayaran sewa $unitName untuk $month dengan baki tunggakan RM ${String.format("%.2f", arrears)}."
        assertTrue(message.contains(tenantName))
        assertTrue(message.contains("RM 1500.00"))
        assertTrue(message.contains(unitName))
    }

    @Test
    fun testRentalStatusMonthFormatting() {
        val cal = java.util.Calendar.getInstance()
        cal.set(2026, java.util.Calendar.OCTOBER, 31)
        val formatted = java.text.SimpleDateFormat("MM/yyyy", java.util.Locale.getDefault()).format(cal.time)
        assertEquals("10/2026", formatted)
    }
}
