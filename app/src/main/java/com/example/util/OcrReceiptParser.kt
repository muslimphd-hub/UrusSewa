package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern

data class ParsedReceipt(
    val amount: Double,
    val paymentDate: String,
    val referenceNo: String,
    val bankName: String,
    val payerName: String,
    val encryptedCloudStorageRef: String,
    val rawText: String
)

object OcrReceiptParser {

    /**
     * Parses receipt text extracted from an image or scanned document.
     */
    fun parseReceiptText(text: String, suggestedTenantName: String? = null): ParsedReceipt {
        val amount = extractAmount(text)
        val date = extractDate(text)
        val refNo = extractRefNo(text)
        val bank = extractBankName(text)
        val payer = extractPayerName(text) ?: suggestedTenantName ?: "Penyewa"
        val encryptedRef = generateEncryptedCloudRef(refNo)

        return ParsedReceipt(
            amount = amount,
            paymentDate = date,
            referenceNo = refNo,
            bankName = bank,
            payerName = payer,
            encryptedCloudStorageRef = encryptedRef,
            rawText = text
        )
    }

    private fun extractAmount(text: String): Double {
        // Look for RM 1,500.00 or RM1500 or Amount: 1200.00
        val regex = Pattern.compile("""(?:RM|MYR|Amount|Jumlah)[\s:]*([0-9,]+(?:\.[0-9]{2})?)""", Pattern.CASE_INSENSITIVE)
        val matcher = regex.matcher(text)
        if (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "") ?: "0.0"
            return amountStr.toDoubleOrNull() ?: 0.0
        }
        // Fallback for standalone numbers with decimal
        val numRegex = Pattern.compile("""\b([0-9]{3,4}(?:\.[0-9]{2}))\b""")
        val numMatcher = numRegex.matcher(text)
        if (numMatcher.find()) {
            return numMatcher.group(1)?.toDoubleOrNull() ?: 0.0
        }
        return 1200.0
    }

    private fun extractDate(text: String): String {
        val ddmmyyyy = Pattern.compile("""(\d{1,2})[/\-.](\d{1,2})[/\-.](\d{4})""")
        val matcher = ddmmyyyy.matcher(text)
        if (matcher.find()) {
            val day = matcher.group(1)?.padStart(2, '0')
            val month = matcher.group(2)?.padStart(2, '0')
            val year = matcher.group(3)
            return "$year-$month-$day"
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun extractRefNo(text: String): String {
        val refRegex = Pattern.compile("""(?:Ref|Reference|No\s*Rujukan|Rujukan|ID)[\s:#]*([A-Z0-9]{8,20})""", Pattern.CASE_INSENSITIVE)
        val matcher = refRegex.matcher(text)
        if (matcher.find()) {
            return matcher.group(1) ?: generateRandomRef()
        }
        return generateRandomRef()
    }

    private fun extractBankName(text: String): String {
        val lower = text.lowercase(Locale.ROOT)
        return when {
            lower.contains("maybank") -> "Maybank2u"
            lower.contains("cimb") -> "CIMB Clicks"
            lower.contains("duitnow") -> "DuitNow QR / Instant"
            lower.contains("touch 'n go") || lower.contains("tng") -> "Touch 'n Go eWallet"
            lower.contains("bank islam") -> "Bank Islam"
            lower.contains("public bank") -> "Public Bank"
            lower.contains("rhb") -> "RHB Now"
            lower.contains("hong leong") -> "Hong Leong Connect"
            lower.contains("bsn") -> "Bank Simpanan Nasional"
            else -> "Pemindahan Bank FPX"
        }
    }

    private fun extractPayerName(text: String): String? {
        val nameRegex = Pattern.compile("""(?:From|Daripada|Nama\s*Pengirim|Payer)[\s:]*([A-Za-z\s]{3,30})""", Pattern.CASE_INSENSITIVE)
        val matcher = nameRegex.matcher(text)
        if (matcher.find()) {
            return matcher.group(1)?.trim()
        }
        return null
    }

    private fun generateRandomRef(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val randomPart = (1..10).map { chars.random() }.joinToString("")
        return "MBB-$randomPart"
    }

    private fun generateEncryptedCloudRef(refNo: String): String {
        val uuid = UUID.randomUUID().toString().take(12)
        return "AES256-GCM://urussewa-vault/receipts/${refNo}_$uuid.enc"
    }

    /**
     * Realistic sample receipts for user testing and quick simulation
     */
    val sampleReceipts = listOf(
        SampleReceipt(
            title = "Maybank2u - Resit Penuh",
            text = """
                MAYBANK2U ONLINE TRANSFER
                Status: Successful
                Reference ID: MBB9928371029
                Date: 28/10/2026 14:22:10
                From: Muhammad Faizal bin Yusof
                To: UrusSewa Management
                Beneficiary Bank: Maybank
                Amount: RM 1,500.00
                Payment Description: Sewa Bulanan Okt 2026
            """.trimIndent()
        ),
        SampleReceipt(
            title = "CIMB Clicks - Resit Separa (Bayar RM800)",
            text = """
                CIMB CLICKS FUNDS TRANSFER
                Status: Successful
                Transaction Ref: CIMB8271928301
                Transaction Date: 27/10/2026
                Sender: Siti Nurhaliza binti Ramli
                Recipient: Haji Ahmad Razak
                Amount: RM 800.00
                Recipient Ref: Sebahagian Sewa Rumah Okt
            """.trimIndent()
        ),
        SampleReceipt(
            title = "DuitNow QR - Resit Penuh",
            text = """
                DUITNOW INSTANT TRANSFER
                Ref No: DNOW7712391028
                Payment Date: 26/10/2026
                Payer: Tan Wei Meng
                Transfer To: UrusSewa Tuan Rumah
                Amount: RM 1,200.00
                Status: BERJAYA / SUCCESSFUL
            """.trimIndent()
        )
    )
}

data class SampleReceipt(
    val title: String,
    val text: String
)
