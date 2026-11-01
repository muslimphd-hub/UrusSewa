package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.LandlordProfile
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReceiptItem(
    val description: String,
    val duration: String,
    val rate: Double,
    val total: Double
)

data class EditableReceiptContent(
    val businessTitle: String = "Pengurusan Hartanah & Rumah Sewa",
    val receiptSubtitle: String = "RESIT RASMI PEMBAYARAN SEWA RUMAH",
    val receiptNumber: String = "RSW-228837",
    val receiptDate: String = "29/10/2026",
    val landlordLabel: String = "MAKLUMAT PENGURUS",
    val landlordName: String = "AL MUSLIM MUSTAPA",
    val landlordPhone: String = "0139358650",
    val landlordUnit: String = "Apartmen Seri Mawar (B-04-10)",
    val tenantLabel: String = "DIBILKAN KEPADA PENYEWA",
    val tenantName: String = "Tan Wei Meng",
    val tenantPhone: String = "60163344556",
    val paymentMethod: String = "Perbankan Dalam Talian (Online)",
    val scheduleHeader: String = "TEMPOH & JADUAL SEWAAN (WAKTU MALAYSIA GMT+8)",
    val scheduleDetails: String = "Bulan Sewa: Oktober 2026   Tarikh Bayar: 2026-10-04   Tarikh Akhir: 2026-10-07",
    val items: List<ReceiptItem> = listOf(
        ReceiptItem("Bayaran Sewa Bulanan (Apartmen Seri Mawar B-04-10)", "1 Bulan (Okt 2026)", 1200.0, 1200.0)
    ),
    val totalAmount: Double = 1200.0,
    val depositNotice: String = "",
    val arrearsNotice: String = "",
    val stampTitle: String = "★ COP RASMI PENGURUS ★",
    val stampStatus: String = "DISAHKAN LUNAS",
    val stampSubstatus: String = "BAYARAN SEWA DITERIMA",
    val stampDate: String = "29/10/2026",
    val stampReg: String = "PENGURUSAN SEWAAN",
    val notesTitle: String = "SYARAT, PERATURAN & PERJANJIAN SEWA",
    val notesContent: String = "1. Resit ini adalah perakuan rasmi penerimaan bayaran sewa rumah bulanan sahaja.\n2. Bayaran bulanan seterusnya hendaklah dijelaskan sebelum tarikh akhir perjanjian sewaan.\n3. Resit digital ini sah dan dijana oleh sistem UrusSewa.",
    val hostSignatureLabel: String = "Pengurus:",
    val hostSignerName: String = "AL MUSLIM MUSTAPA",
    val recipientLabel: String = "Penyewa:",
    val recipientName: String = "Tan Wei Meng",
    val footerNotice: String = "Resit digital ini dijana secara rasmi melalui Sistem Pengurusan Sewaan UrusSewa.\nPerakuan pembayaran ini sah mengikut Akta Sewaan & Kontrak Perjanjian."
)

object DigitalReceiptGenerator {

    fun buildDefaultReceiptContent(
        payment: Payment,
        tenant: Tenant?,
        property: Property?,
        landlord: LandlordProfile
    ): EditableReceiptContent {
        val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val defaultNum = if (payment.receiptNumber.isNotBlank()) payment.receiptNumber else "RSW-${System.currentTimeMillis().toString().takeLast(6)}"
        val propertyUnitDesc = property?.let { "${it.name} (${it.unitNumber})" } ?: "Apartmen Seri Mawar (B-04-10)"
        val tenantName = tenant?.name ?: "Tan Wei Meng"
        val tenantPhone = tenant?.phone ?: "60163344556"

        val stampLines = landlord.officialStampText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val sTitle = stampLines.getOrNull(0) ?: "★ COP RASMI PENGURUS ★"
        val sStatus = stampLines.getOrNull(1) ?: "DISAHKAN LUNAS"
        val sSubstatus = stampLines.getOrNull(2) ?: "BAYARAN SEWA DITERIMA"
        val sReg = stampLines.getOrNull(3) ?: "PENGURUSAN SEWAAN"

        // Hanya sewa bulanan sahaja dimasukkan dalam item dan jumlah bersih
        val rentItem = ReceiptItem(
            description = "Bayaran Sewa Bulanan ($propertyUnitDesc)",
            duration = if (payment.billingMonth.isNotBlank()) "Bulan ${payment.billingMonth}" else "1 Bulan",
            rate = payment.amountPaid,
            total = payment.amountPaid
        )
        val itemList = listOf(rentItem)
        val grandTotal = payment.amountPaid

        // Maklumat deposit sebagai pemakluman satu tempoh kontrak (bukan bayaran bulanan)
        val secDep = tenant?.securityDeposit ?: 0.0
        val utilDep = tenant?.utilityDeposit ?: 0.0
        val totalDep = if (secDep + utilDep > 0) secDep + utilDep else (property?.depositAmount ?: 0.0)
        val startPeriod = tenant?.contractStartDate?.ifBlank { "-" } ?: "-"
        val endPeriod = tenant?.contractEndDate?.ifBlank { "-" } ?: "-"
        val depNoticeText = if (totalDep > 0) {
            "Deposit Keselamatan: RM ${String.format("%.2f", if (secDep > 0) secDep else totalDep * 0.75)} | Deposit Utiliti: RM ${String.format("%.2f", if (utilDep > 0) utilDep else totalDep * 0.25)}\nTempoh Kontrak: $startPeriod hingga $endPeriod (Cagaran satu kontrak penuh, bukan sewa bulanan)."
        } else {
            ""
        }

        // Peringatan tunggakan jika ada
        val arrearsNoticeText = if (payment.balanceDue > 0) {
            "PERINGATAN TUNGGAKAN: Terdapat baki tunggakan sewa sebanyak RM ${String.format("%.2f", payment.balanceDue)} bagi unit ini. Sila jelaskan segera."
        } else {
            ""
        }

        val scheduleStr = "Bulan Sewaan: ${payment.billingMonth}   Tarikh Bayaran: ${payment.paymentDate ?: todayStr}   Tarikh Akhir: ${payment.dueDate}"

        return EditableReceiptContent(
            businessTitle = landlord.businessName.ifBlank { property?.name ?: "Pengurusan Hartanah & Rumah Sewa" },
            receiptSubtitle = landlord.receiptSubtitleTemplate.ifBlank { "RESIT RASMI PEMBAYARAN SEWA RUMAH" },
            receiptNumber = defaultNum,
            receiptDate = payment.paymentDate ?: todayStr,
            landlordLabel = "MAKLUMAT PENGURUS",
            landlordName = landlord.landlordName,
            landlordPhone = landlord.phone,
            landlordUnit = propertyUnitDesc,
            tenantLabel = "DIBILKAN KEPADA PENYEWA",
            tenantName = tenantName,
            tenantPhone = tenantPhone,
            paymentMethod = payment.ocrBankName?.let { "Perbankan Dalam Talian ($it)" } ?: "Perbankan Dalam Talian (Online)",
            scheduleHeader = "TEMPOH & JADUAL SEWAAN (WAKTU MALAYSIA GMT+8)",
            scheduleDetails = scheduleStr,
            items = itemList,
            totalAmount = grandTotal,
            depositNotice = depNoticeText,
            arrearsNotice = arrearsNoticeText,
            stampTitle = sTitle,
            stampStatus = sStatus,
            stampSubstatus = sSubstatus,
            stampDate = todayStr,
            stampReg = sReg,
            notesTitle = "SYARAT, PERATURAN & PERJANJIAN SEWA",
            notesContent = landlord.defaultNotes.ifBlank {
                "1. Resit ini adalah perakuan rasmi penerimaan bayaran sewa rumah bulanan sahaja.\n2. Bayaran bulanan seterusnya hendaklah diselesaikan sebelum tarikh akhir setiap bulan mengikut perjanjian sewaan.\n3. Resit digital ini sah dan dijana oleh sistem UrusSewa."
            },
            hostSignatureLabel = "Pengurus:",
            hostSignerName = landlord.landlordName,
            recipientLabel = "Penyewa:",
            recipientName = tenantName,
            footerNotice = "Resit digital ini dijana secara rasmi melalui Sistem Pengurusan Sewaan UrusSewa.\nPerakuan pembayaran ini sah mengikut Akta Sewaan & Kontrak Perjanjian."
        )
    }

    fun generateReceiptText(receipt: EditableReceiptContent): String {
        return buildString {
            appendLine("==================================================")
            appendLine(receipt.businessTitle.uppercase())
            appendLine(receipt.receiptSubtitle)
            appendLine("NO. RESIT: ${receipt.receiptNumber}")
            appendLine("Tarikh: ${receipt.receiptDate}")
            appendLine("==================================================")
            appendLine()
            appendLine(receipt.landlordLabel)
            appendLine(receipt.landlordName)
            appendLine("Tel / WhatsApp: ${receipt.landlordPhone}")
            appendLine("Unit: ${receipt.landlordUnit}")
            appendLine()
            appendLine(receipt.tenantLabel)
            appendLine(receipt.tenantName)
            appendLine("No. Tel: ${receipt.tenantPhone}")
            appendLine("Kaedah Bayar: ${receipt.paymentMethod}")
            appendLine()
            appendLine(receipt.scheduleHeader)
            appendLine(receipt.scheduleDetails)
            appendLine()
            appendLine("--------------------------------------------------")
            appendLine("KETERANGAN ITEM / BUTIRAN | TEMPOH | KADAR | JUMLAH")
            appendLine("--------------------------------------------------")
            for (item in receipt.items) {
                appendLine("• ${item.description}")
                appendLine("  Tempoh: ${item.duration} | Kadar: RM ${String.format("%.2f", item.rate)} | Jumlah: RM ${String.format("%.2f", item.total)}")
            }
            appendLine("--------------------------------------------------")
            appendLine("JUMLAH BERSIH DITERIMA : RM ${String.format("%.2f", receipt.totalAmount)}")
            appendLine("--------------------------------------------------")
            if (receipt.depositNotice.isNotBlank()) {
                appendLine()
                appendLine("[PEMAKLUMAN DEPOSIT KONTRAK]")
                appendLine(receipt.depositNotice)
            }
            if (receipt.arrearsNotice.isNotBlank()) {
                appendLine()
                appendLine("**************************************************")
                appendLine(receipt.arrearsNotice)
                appendLine("**************************************************")
            }
            appendLine()
            appendLine(receipt.stampTitle)
            appendLine("${receipt.stampStatus} - ${receipt.stampSubstatus}")
            appendLine("Tarikh: ${receipt.stampDate}")
            appendLine(receipt.stampReg)
            appendLine()
            appendLine(receipt.notesTitle)
            appendLine(receipt.notesContent)
            appendLine()
            appendLine("${receipt.hostSignatureLabel} ${receipt.hostSignerName}")
            appendLine("${receipt.recipientLabel} ${receipt.recipientName}")
            appendLine()
            appendLine(receipt.footerNotice)
        }
    }

    // Backward-compatibility
    fun generateReceiptText(
        payment: Payment,
        tenant: Tenant?,
        property: Property?,
        landlord: LandlordProfile,
        customStamp: String? = null
    ): String {
        val model = buildDefaultReceiptContent(payment, tenant, property, landlord)
        return generateReceiptText(model)
    }

    /**
     * Generates a PDF replicating the official template provided by the user.
     */
    fun shareAsPdfDocument(
        context: Context,
        receipt: EditableReceiptContent
    ) {
        try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Colors
            val primaryNavy = Color.rgb(30, 64, 175)     // #1E40AF
            val cyanLight = Color.rgb(147, 197, 253)     // #93C5FD
            val darkSlate = Color.rgb(15, 23, 42)        // #0F172A
            val softGreen = Color.rgb(240, 253, 244)     // #F0FDF4
            val greenBorder = Color.rgb(34, 197, 94)     // #22C55E
            val greenText = Color.rgb(21, 128, 61)       // #15803D
            val stampBlue = Color.rgb(30, 64, 175)       // #1E40AF
            val lightBg = Color.rgb(248, 250, 252)       // #F8FAFC
            val borderGray = Color.rgb(226, 232, 240)    // #E2E8F0
            val textMuted = Color.rgb(100, 116, 139)     // #64748B

            // Paints
            val bannerPaint = Paint().apply {
                color = primaryNavy
                style = Paint.Style.FILL
            }
            val titlePaint = Paint().apply {
                color = Color.WHITE
                textSize = 17f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subtitlePaint = Paint().apply {
                color = cyanLight
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bannerRightPaint = Paint().apply {
                color = Color.WHITE
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }
            val bannerDatePaint = Paint().apply {
                color = cyanLight
                textSize = 8.5f
                textAlign = Paint.Align.RIGHT
            }

            // 1. Draw Top Royal Blue Banner
            val bannerRect = RectF(25f, 25f, 570f, 92f)
            canvas.drawRoundRect(bannerRect, 10f, 10f, bannerPaint)

            canvas.drawText(receipt.businessTitle, 40f, 55f, titlePaint)
            canvas.drawText(receipt.receiptSubtitle, 40f, 75f, subtitlePaint)

            canvas.drawText("NO. RESIT: ${receipt.receiptNumber}", 555f, 55f, bannerRightPaint)
            canvas.drawText("Tarikh: ${receipt.receiptDate}", 555f, 75f, bannerDatePaint)

            // 2. Pengurus & Penyewa 2-column cards
            val boxBorderPaint = Paint().apply {
                color = borderGray
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            val boxFillPaint = Paint().apply {
                color = lightBg
                style = Paint.Style.FILL
            }

            val cardTop = 104f
            val cardHeight = 78f
            // Left Card: Pengurus
            canvas.drawRoundRect(RectF(25f, cardTop, 292f, cardTop + cardHeight), 8f, 8f, boxFillPaint)
            canvas.drawRoundRect(RectF(25f, cardTop, 292f, cardTop + cardHeight), 8f, 8f, boxBorderPaint)

            // Right Card: Penyewa
            canvas.drawRoundRect(RectF(302f, cardTop, 570f, cardTop + cardHeight), 8f, 8f, boxFillPaint)
            canvas.drawRoundRect(RectF(302f, cardTop, 570f, cardTop + cardHeight), 8f, 8f, boxBorderPaint)

            val headerTagPaint = Paint().apply {
                color = textMuted
                textSize = 7.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val personNamePaint = Paint().apply {
                color = Color.BLACK
                textSize = 10.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyDetailPaint = Paint().apply {
                color = Color.rgb(51, 65, 85)
                textSize = 8.5f
            }

            // Populate Left Card
            canvas.drawText(receipt.landlordLabel, 36f, cardTop + 16f, headerTagPaint)
            canvas.drawText(receipt.landlordName, 36f, cardTop + 32f, personNamePaint)
            canvas.drawText("Tel / WhatsApp: ${receipt.landlordPhone}", 36f, cardTop + 48f, bodyDetailPaint)
            canvas.drawText("Unit: ${receipt.landlordUnit}", 36f, cardTop + 64f, bodyDetailPaint)

            // Populate Right Card
            canvas.drawText(receipt.tenantLabel, 314f, cardTop + 16f, headerTagPaint)
            canvas.drawText(receipt.tenantName, 314f, cardTop + 32f, personNamePaint)
            canvas.drawText("No. Tel: ${receipt.tenantPhone}", 314f, cardTop + 48f, bodyDetailPaint)
            canvas.drawText("Kaedah Bayar: ${receipt.paymentMethod}", 314f, cardTop + 64f, bodyDetailPaint)

            // 3. Schedule banner
            val schedTop = cardTop + cardHeight + 10f
            val schedHeight = 36f
            val schedFillPaint = Paint().apply {
                color = Color.rgb(238, 242, 255) // light indigo
                style = Paint.Style.FILL
            }
            val schedBorderPaint = Paint().apply {
                color = Color.rgb(199, 210, 254)
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas.drawRoundRect(RectF(25f, schedTop, 570f, schedTop + schedHeight), 6f, 6f, schedFillPaint)
            canvas.drawRoundRect(RectF(25f, schedTop, 570f, schedTop + schedHeight), 6f, 6f, schedBorderPaint)

            val schedHeaderPaint = Paint().apply {
                color = primaryNavy
                textSize = 7.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val schedBodyPaint = Paint().apply {
                color = Color.rgb(30, 41, 59)
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(receipt.scheduleHeader, 36f, schedTop + 14f, schedHeaderPaint)
            canvas.drawText(receipt.scheduleDetails, 36f, schedTop + 28f, schedBodyPaint)

            // 4. Itemized Table (Rent only)
            val tableTop = schedTop + schedHeight + 14f
            val thPaint = Paint().apply {
                color = darkSlate
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(RectF(25f, tableTop, 570f, tableTop + 22f), 4f, 4f, thPaint)

            val thTextPaint = Paint().apply {
                color = Color.WHITE
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("KETERANGAN ITEM / BUTIRAN", 36f, tableTop + 14f, thTextPaint)
            canvas.drawText("TEMPOH", 325f, tableTop + 14f, thTextPaint)
            canvas.drawText("KADAR (RM)", 405f, tableTop + 14f, thTextPaint)
            canvas.drawText("JUMLAH (RM)", 495f, tableTop + 14f, thTextPaint)

            var rowY = tableTop + 24f
            val rowTextPaint = Paint().apply {
                color = Color.rgb(30, 41, 59)
                textSize = 8.5f
            }
            val rowTotalPaint = Paint().apply {
                color = Color.rgb(15, 23, 42)
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val rowLinePaint = Paint().apply {
                color = Color.rgb(241, 245, 249)
                strokeWidth = 1f
            }

            for (item in receipt.items) {
                rowY += 22f
                canvas.drawText(item.description, 36f, rowY, rowTextPaint)
                canvas.drawText(item.duration, 325f, rowY, rowTextPaint)
                canvas.drawText(String.format("%.2f", item.rate), 405f, rowY, rowTextPaint)
                canvas.drawText(String.format("%.2f", item.total), 495f, rowY, rowTotalPaint)
                canvas.drawLine(25f, rowY + 6f, 570f, rowY + 6f, rowLinePaint)
            }

            // 5. Official Rubber Stamp (Left) & Total Green Box (Right)
            val middleSectionTop = rowY + 18f

            // Total Box (Right: 302f to 570f)
            val totalBoxRect = RectF(302f, middleSectionTop, 570f, middleSectionTop + 62f)
            val totalBoxFill = Paint().apply {
                color = softGreen
                style = Paint.Style.FILL
            }
            val totalBoxStroke = Paint().apply {
                color = greenBorder
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }
            canvas.drawRoundRect(totalBoxRect, 8f, 8f, totalBoxFill)
            canvas.drawRoundRect(totalBoxRect, 8f, 8f, totalBoxStroke)

            val totalLabelPaint = Paint().apply {
                color = greenText
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val totalValuePaint = Paint().apply {
                color = greenText
                textSize = 19f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("JUMLAH BERSIH DITERIMA", 318f, middleSectionTop + 34f, totalLabelPaint)
            canvas.drawText("RM ${String.format("%.2f", receipt.totalAmount)}", 555f, middleSectionTop + 39f, totalValuePaint)

            // Official Stamp (Left: 60f to 260f)
            val stampBoxRect = RectF(60f, middleSectionTop - 2f, 260f, middleSectionTop + 66f)
            val stampBorderPaint = Paint().apply {
                color = stampBlue
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            val stampInnerBorder = Paint().apply {
                color = stampBlue
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas.drawRoundRect(stampBoxRect, 8f, 8f, stampBorderPaint)
            canvas.drawRoundRect(RectF(63f, middleSectionTop + 1f, 257f, middleSectionTop + 63f), 6f, 6f, stampInnerBorder)

            val stampTextP = Paint().apply {
                color = stampBlue
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            val stampSubP = Paint().apply {
                color = stampBlue
                textSize = 7f
                textAlign = Paint.Align.CENTER
            }
            val centerX = 160f
            canvas.drawText(receipt.stampTitle, centerX, middleSectionTop + 13f, stampTextP)
            canvas.drawText(receipt.stampStatus, centerX, middleSectionTop + 26f, stampTextP)
            canvas.drawText(receipt.stampSubstatus, centerX, middleSectionTop + 38f, stampSubP)
            canvas.drawText(receipt.stampDate, centerX, middleSectionTop + 49f, stampSubP)
            canvas.drawText(receipt.stampReg, centerX, middleSectionTop + 60f, stampSubP)

            var currentSectionY = middleSectionTop + 72f

            // 5a. Pemakluman Deposit Kontrak Box (jika ada)
            if (receipt.depositNotice.isNotBlank()) {
                val depRect = RectF(25f, currentSectionY, 570f, currentSectionY + 38f)
                val depBgPaint = Paint().apply {
                    color = Color.rgb(241, 245, 249) // Slate 100
                    style = Paint.Style.FILL
                }
                val depBorderPaint = Paint().apply {
                    color = Color.rgb(203, 213, 225) // Slate 300
                    style = Paint.Style.STROKE
                    strokeWidth = 1f
                }
                canvas.drawRoundRect(depRect, 6f, 6f, depBgPaint)
                canvas.drawRoundRect(depRect, 6f, 6f, depBorderPaint)

                val depTitlePaint = Paint().apply {
                    color = Color.rgb(71, 85, 105)
                    textSize = 7.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val depTextPaint = Paint().apply {
                    color = Color.rgb(30, 41, 59)
                    textSize = 7.5f
                }
                canvas.drawText("PEMAKLUMAN DEPOSIT KONTRAK SEWAAN (BUKAN BAYARAN BULANAN):", 36f, currentSectionY + 13f, depTitlePaint)
                val depLines = receipt.depositNotice.lines()
                var depLineY = currentSectionY + 25f
                for (dLine in depLines.take(2)) {
                    canvas.drawText(dLine, 36f, depLineY, depTextPaint)
                    depLineY += 10f
                }
                currentSectionY += 44f
            }

            // 5b. Peringatan Tunggakan Box (Ruang berbeza dengan jumlah yang dibayar)
            if (receipt.arrearsNotice.isNotBlank()) {
                val arrRect = RectF(25f, currentSectionY, 570f, currentSectionY + 34f)
                val arrBgPaint = Paint().apply {
                    color = Color.rgb(254, 242, 242) // Red 50
                    style = Paint.Style.FILL
                }
                val arrBorderPaint = Paint().apply {
                    color = Color.rgb(239, 68, 68) // Red 500
                    style = Paint.Style.STROKE
                    strokeWidth = 1.2f
                }
                canvas.drawRoundRect(arrRect, 6f, 6f, arrBgPaint)
                canvas.drawRoundRect(arrRect, 6f, 6f, arrBorderPaint)

                val arrTitlePaint = Paint().apply {
                    color = Color.rgb(185, 28, 28) // Red 700
                    textSize = 8f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val arrTextPaint = Paint().apply {
                    color = Color.rgb(153, 27, 27)
                    textSize = 7.5f
                }
                canvas.drawText("⚠ PERINGATAN TUNGGAKAN SEWA RUMAH:", 36f, currentSectionY + 13f, arrTitlePaint)
                canvas.drawText(receipt.arrearsNotice.replace("PERINGATAN TUNGGAKAN: ", ""), 36f, currentSectionY + 26f, arrTextPaint)
                currentSectionY += 40f
            }

            // 6. Catatan, Syarat & Peraturan
            val notesTop = currentSectionY
            val notesRect = RectF(25f, notesTop, 570f, notesTop + 48f)
            canvas.drawRoundRect(notesRect, 6f, 6f, boxFillPaint)
            canvas.drawRoundRect(notesRect, 6f, 6f, boxBorderPaint)

            val notesHeaderPaint = Paint().apply {
                color = textMuted
                textSize = 7.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(receipt.notesTitle, 36f, notesTop + 14f, notesHeaderPaint)

            val notesBodyPaint = Paint().apply {
                color = Color.rgb(51, 65, 85)
                textSize = 7.5f
            }
            val words = receipt.notesContent.split(" ")
            var lineBuf = ""
            var noteY = notesTop + 26f
            for (w in words) {
                if (notesBodyPaint.measureText("$lineBuf $w") < 510f) {
                    lineBuf = if (lineBuf.isEmpty()) w else "$lineBuf $w"
                } else {
                    canvas.drawText(lineBuf, 36f, noteY, notesBodyPaint)
                    noteY += 10f
                    lineBuf = w
                }
            }
            if (lineBuf.isNotEmpty()) {
                canvas.drawText(lineBuf, 36f, noteY, notesBodyPaint)
            }

            // 7. Pengurus & Penyewa (Perkataan tandatangan / perakuan dibuang)
            val sigTop = notesTop + 62f
            val sigHeaderPaint = Paint().apply {
                color = textMuted
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val sigNamePaint = Paint().apply {
                color = primaryNavy
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            canvas.drawText(receipt.hostSignatureLabel, 40f, sigTop, sigHeaderPaint)
            canvas.drawText("✍ ${receipt.hostSignerName}", 40f, sigTop + 18f, sigNamePaint)

            canvas.drawText(receipt.recipientLabel, 380f, sigTop, sigHeaderPaint)
            canvas.drawText(receipt.recipientName, 380f, sigTop + 18f, sigNamePaint)

            // 8. Footer
            val footerPaint = Paint().apply {
                color = textMuted
                textSize = 7.5f
                textAlign = Paint.Align.CENTER
            }
            val footerLines = receipt.footerNotice.lines()
            var fY = 800f
            for (fLine in footerLines) {
                canvas.drawText(fLine, 297f, fY, footerPaint)
                fY += 12f
            }

            pdfDocument.finishPage(page)

            val cleanNum = receipt.receiptNumber.replace(Regex("[^a-zA-Z0-9-]"), "")
            val fileName = "Resit_${cleanNum.ifBlank { "Rasmi" }}.pdf"
            val file = File(exportDir, fileName)
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()

            val uri: Uri = try {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } catch (e: Exception) {
                Uri.fromFile(file)
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "${receipt.receiptSubtitle} - ${receipt.receiptNumber}")
                putExtra(Intent.EXTRA_TEXT, "Dilampirkan ${receipt.receiptSubtitle} [${receipt.receiptNumber}] bagi ${receipt.landlordUnit}.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Kongsi Resit PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Ralat menjana fail PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Compatibility method
    fun shareAsPdfDocument(
        context: Context,
        payment: Payment,
        receiptText: String,
        stampText: String
    ) {
        val dummyContent = EditableReceiptContent(
            receiptNumber = payment.receiptNumber.ifBlank { "HST-${payment.id}" },
            receiptDate = payment.paymentDate ?: SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            totalAmount = payment.amountPaid
        )
        shareAsPdfDocument(context, dummyContent)
    }

    fun shareViaWhatsApp(
        context: Context,
        phoneNumber: String,
        message: String
    ) {
        try {
            var cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
            if (cleanPhone.startsWith("0")) {
                cleanPhone = "60" + cleanPhone.substring(1)
            }
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            shareGenericText(context, message, "Hantar WhatsApp")
        }
    }

    fun sendEmail(
        context: Context,
        recipientEmail: String,
        subject: String,
        body: String
    ) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(Intent.createChooser(intent, "Pilih Emel"))
        } catch (e: Exception) {
            Toast.makeText(context, "Aplikasi emel tidak dijumpai", Toast.LENGTH_SHORT).show()
        }
    }

    fun buildOverdueReminderWhatsApp(
        payment: Payment,
        tenant: Tenant?,
        property: Property?,
        landlord: LandlordProfile
    ): String {
        val tenantName = tenant?.name ?: "Tuan/Puan"
        val unit = property?.let { "${it.name} (Unit ${it.unitNumber})" } ?: "rumah sewa"
        val baki = String.format("%.2f", payment.balanceDue)
        val bulan = payment.billingMonth

        return """
            Salam Sejahtera $tenantName,
            
            Peringatan mesra sewa bagi *$unit*.
            
            Sewa bulan *$bulan* mempunyai baki tertunggak *RM $baki*.
            
            Akaun Bayaran:
            Bank: ${landlord.bankName}
            No Akaun: ${landlord.accountNumber}
            Nama: ${landlord.landlordName}
            
            Sila maklumkan selepas bayaran dibuat. Terima kasih.
            
            Yang Benar,
            ${landlord.landlordName}
        """.trimIndent()
    }

    fun shareGenericText(context: Context, text: String, title: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}
