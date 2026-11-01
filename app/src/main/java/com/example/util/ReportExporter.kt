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
import com.example.data.model.Maintenance
import com.example.data.model.Payment
import com.example.data.model.Property
import com.example.data.model.Tenant
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {

    /**
     * Eksport Penyata Kewangan dalam bentuk fail Excel (.csv) dengan format jadual profesional & UTF-8 BOM.
     */
    fun exportToCsv(
        context: Context,
        payments: List<Payment>,
        properties: List<Property>,
        tenants: List<Tenant>,
        maintenance: List<Maintenance>,
        profile: LandlordProfile? = null,
        periodLabel: String = "Semua Tempoh",
        tenantFilterLabel: String = "Semua Penyewa"
    ) {
        try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(exportDir, "Penyata_Kewangan_Excel_$timeStamp.csv")
            val outputStream = FileOutputStream(file)

            // UTF-8 BOM so Microsoft Excel renders Malay characters & symbols correctly
            outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            val writer = outputStream.bufferedWriter(Charsets.UTF_8)

            val propMap = properties.associateBy { it.id }
            val tenantMap = tenants.associateBy { it.id }

            val totalDue = payments.sumOf { it.amountDue }
            val totalCollected = payments.sumOf { it.amountPaid }
            val totalOverdue = payments.sumOf { it.balanceDue }
            val totalMaintenance = maintenance.sumOf { it.cost }
            val bankCost = if (profile?.includeBankCost == true) properties.sumOf { it.bankLoanPayment } else 0.0
            val managerCost = if (profile?.includeManagementFee == true) profile.managementFeeAmount else 0.0
            val totalCosts = totalMaintenance + bankCost + managerCost
            val netIncome = totalCollected - totalCosts
            val collectionRate = if (totalDue > 0) (totalCollected / totalDue * 100) else 0.0

            val orgTitle = profile?.businessName?.ifBlank { "PENGURUSAN HARTANAH & RUMAH SEWA" } ?: "PENGURUSAN HARTANAH & RUMAH SEWA"

            // Header Laporan
            writer.append("========================================================================================\n")
            writer.append("\"$orgTitle\"\n")
            writer.append("\"LAPORAN PENYATA KEWANGAN SEWAAN HARTANAH (RASMI)\"\n")
            writer.append("\"Tempoh Penyata:\",\"$periodLabel\"\n")
            writer.append("\"Penapis Penyewa:\",\"$tenantFilterLabel\"\n")
            writer.append("\"Tarikh Dijana:\",\"${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}\"\n")
            writer.append("\"Nama Tuan Rumah / Hos:\",\"${profile?.landlordName ?: "AL MUSLIM MUSTAPA"}\"\n")
            writer.append("\"No. Telefon:\",\"${profile?.phone ?: "0139358650"}\"\n")
            writer.append("========================================================================================\n\n")

            // Ringkasan Eksekutif
            writer.append("--- RINGKASAN EKSEKUTIF KEWANGAN ---\n")
            writer.append("\"Perkara\",\"Jumlah (RM)\",\"Catatan\"\n")
            writer.append("\"Jumlah Nilai Sewa Ditagih\",${String.format(Locale.US, "%.2f", totalDue)},\"Nilai sewaan sewajarnya\"\n")
            writer.append("\"Jumlah Kutipan Diterima\",${String.format(Locale.US, "%.2f", totalCollected)},\"Kutipan sebenar diterima\"\n")
            writer.append("\"Jumlah Baki Sewa Tertunggak\",${String.format(Locale.US, "%.2f", totalOverdue)},\"Baki belum dijelaskan\"\n")
            writer.append("\"Kos Penyelenggaraan & Pembaikan\",${String.format(Locale.US, "%.2f", totalMaintenance)},\"Kos pembaikan unit\"\n")
            writer.append("\"Kos Ansuran Pinjaman Bank\",${String.format(Locale.US, "%.2f", bankCost)},\"${if (profile?.includeBankCost == true) "Diambil kira" else "Tidak diambil kira"}\"\n")
            writer.append("\"Kos Pengurus Hartanah\",${String.format(Locale.US, "%.2f", managerCost)},\"${if (profile?.includeManagementFee == true) "Diambil kira" else "Tidak diambil kira"}\"\n")
            writer.append("\"JUMLAH SEMUA KOS OPERASI\",${String.format(Locale.US, "%.2f", totalCosts)},\"Jumlah kos keseluruhan\"\n")
            writer.append("\"PENDAPATAN BERSIH SELEPAS KOS\",${String.format(Locale.US, "%.2f", netIncome)},\"Kutipan tolak semua kos\"\n")
            writer.append("\"Kadar Kutipan Semasa (%)\",\"${String.format(Locale.US, "%.1f", collectionRate)}%\",\"Peratus kejayaan kutipan\"\n\n")

            // Section 1: Transaksi Kutipan Sewa
            writer.append("--- JADUAL TRANSAKSI KUTIPAN SEWAAN ---\n")
            writer.append("No,No Resit,Bulan Sewa,Hartanah,No Unit,Penyewa,No Telefon,Tarikh Akhir,Nilai Sewa (RM),Dibayar (RM),Baki (RM),Status,Tarikh Bayaran,Kaedah Bayar / Bank,No Rujukan\n")

            payments.forEachIndexed { index, p ->
                val prop = propMap[p.propertyId]
                val tnt = tenantMap[p.tenantId]
                writer.append("${index + 1},")
                writer.append("\"${p.receiptNumber}\",")
                writer.append("\"${p.billingMonth}\",")
                writer.append("\"${prop?.name ?: "-"}\",")
                writer.append("\"${prop?.unitNumber ?: "-"}\",")
                writer.append("\"${tnt?.name ?: "-"}\",")
                writer.append("\"${tnt?.phone ?: "-"}\",")
                writer.append("\"${p.dueDate}\",")
                writer.append("${String.format(Locale.US, "%.2f", p.amountDue)},")
                writer.append("${String.format(Locale.US, "%.2f", p.amountPaid)},")
                writer.append("${String.format(Locale.US, "%.2f", p.balanceDue)},")
                writer.append("\"${p.status}\",")
                writer.append("\"${p.paymentDate ?: "-"}\",")
                writer.append("\"${p.ocrBankName ?: "Perbankan Dalam Talian"}\",")
                writer.append("\"${p.ocrReferenceNo ?: "-"}\"\n")
            }

            // Section 2: Penyelenggaraan
            writer.append("\n--- JADUAL PENYELENGGARAAN & PEMBAIKAN UNIT ---\n")
            writer.append("No,Hartanah,No Unit,Kategori,Perihal Pembaikan,Kos (RM),Tarikh Laporan,Status,Kontraktor\n")
            if (maintenance.isEmpty()) {
                writer.append("Tiada rekod penyelenggaraan bagi tempoh ini,,,,,,,,\n")
            } else {
                maintenance.forEachIndexed { index, m ->
                    val prop = propMap[m.propertyId]
                    writer.append("${index + 1},")
                    writer.append("\"${prop?.name ?: "-"}\",")
                    writer.append("\"${prop?.unitNumber ?: "-"}\",")
                    writer.append("\"${m.category}\",")
                    writer.append("\"${m.title}\",")
                    writer.append("${String.format(Locale.US, "%.2f", m.cost)},")
                    writer.append("\"${m.dateReported}\",")
                    writer.append("\"${m.status}\",")
                    writer.append("\"${m.contractorContact}\"\n")
                }
            }

            // Section 3: Ansuran Bank Per Unit
            writer.append("\n--- STATUS KOS PINJAMAN BANK & SEWA ASAS ---\n")
            writer.append("No,Nama Hartanah,No Unit,Sewa Asas (RM),Ansuran Bank (RM),Termasuk Dalam Kiraan\n")
            properties.forEachIndexed { index, pr ->
                writer.append("${index + 1},")
                writer.append("\"${pr.name}\",")
                writer.append("\"${pr.unitNumber}\",")
                writer.append("${String.format(Locale.US, "%.2f", pr.monthlyRent)},")
                writer.append("${String.format(Locale.US, "%.2f", pr.bankLoanPayment)},")
                writer.append("\"${if (profile?.includeBankCost == true) "YA" else "TIDAK"}\"\n")
            }

            writer.flush()
            writer.close()
            outputStream.close()

            shareFile(context, file, "text/csv", "Kongsi Penyata Kewangan Excel (.csv)")
        } catch (e: Exception) {
            Toast.makeText(context, "Ralat mengeksport fail Excel: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Eksport Penyata Kewangan dalam bentuk fail Teks (.txt) bertaraf profesional dengan sempadan ASCII.
     */
    fun exportToFormattedStatement(
        context: Context,
        payments: List<Payment>,
        properties: List<Property>,
        tenants: List<Tenant>,
        maintenance: List<Maintenance>,
        periodLabel: String = "Semua Tempoh",
        tenantFilterLabel: String = "Semua Penyewa",
        profile: LandlordProfile? = null
    ) {
        try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(exportDir, "Penyata_Kewangan_Teks_$timeStamp.txt")
            val writer = FileWriter(file)

            val propMap = properties.associateBy { it.id }
            val tenantMap = tenants.associateBy { it.id }

            val totalDue = payments.sumOf { it.amountDue }
            val totalCollected = payments.sumOf { it.amountPaid }
            val totalOverdue = payments.sumOf { it.balanceDue }
            val totalMaintenance = maintenance.sumOf { it.cost }
            val bankCost = if (profile?.includeBankCost == true) properties.sumOf { it.bankLoanPayment } else 0.0
            val managerCost = if (profile?.includeManagementFee == true) profile.managementFeeAmount else 0.0
            val totalCosts = totalMaintenance + bankCost + managerCost
            val netIncome = totalCollected - totalCosts
            val collectionRate = if (totalDue > 0) (totalCollected / totalDue * 100) else 0.0

            val orgTitle = profile?.businessName?.ifBlank { "PENGURUSAN HARTANAH & RUMAH SEWA" } ?: "PENGURUSAN HARTANAH & RUMAH SEWA"

            val statement = buildString {
                appendLine("================================================================================")
                appendLine("                 $orgTitle")
                appendLine("           LAPORAN PENYATA KEWANGAN SEWAAN HARTANAH (RASMI)")
                appendLine("================================================================================")
                appendLine("Aplikasi       : UrusSewa Landlord Management")
                appendLine("Tarikh Dijana  : ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}")
                appendLine("Tempoh Laporan : $periodLabel")
                appendLine("Penapis Penyewa: $tenantFilterLabel")
                appendLine("Tuan Rumah     : ${profile?.landlordName ?: "AL MUSLIM MUSTAPA"} (${profile?.phone ?: "0139358650"})")
                appendLine("Emel           : ${profile?.email ?: "almuslim@ukm.edu.my"}")
                appendLine("--------------------------------------------------------------------------------")
                appendLine("RINGKASAN EKSEKUTIF KEWANGAN:")
                appendLine("1. Jumlah Nilai Sewa Sewajarnya : RM ${String.format(Locale.US, "%.2f", totalDue)}")
                appendLine("2. Jumlah Kutipan Sebenar       : RM ${String.format(Locale.US, "%.2f", totalCollected)}")
                appendLine("3. Jumlah Baki Tertunggak        : RM ${String.format(Locale.US, "%.2f", totalOverdue)}")
                appendLine("4. Kos Penyelenggaraan & Baiki  : RM ${String.format(Locale.US, "%.2f", totalMaintenance)}")
                appendLine("5. Kos Ansuran Pinjaman Bank    : RM ${String.format(Locale.US, "%.2f", bankCost)} ${if (profile?.includeBankCost == true) "(Diambil kira)" else "(Tidak diambil kira)"}")
                appendLine("6. Kos Pengurusan Hartanah      : RM ${String.format(Locale.US, "%.2f", managerCost)} ${if (profile?.includeManagementFee == true) "(Diambil kira)" else "(Tidak diambil kira)"}")
                appendLine("--------------------------------------------------------------------------------")
                appendLine("JUMLAH SEMUA KOS OPERASI        : RM ${String.format(Locale.US, "%.2f", totalCosts)}")
                appendLine("PENDAPATAN BERSIH SELEPAS KOS   : RM ${String.format(Locale.US, "%.2f", netIncome)}")
                appendLine("Kadar Kutipan Semasa            : ${String.format(Locale.US, "%.1f", collectionRate)}%")
                appendLine("================================================================================")
                appendLine()
                appendLine("SENARAI REKOD TRANSAKSI KUTIPAN SEWA:")
                if (payments.isEmpty()) {
                    appendLine("  (Tiada rekod bayaran bagi kriteria tapisan ini)")
                } else {
                    payments.forEachIndexed { i, p ->
                        val prop = propMap[p.propertyId]
                        val t = tenantMap[p.tenantId]
                        appendLine("${i + 1}. [${p.billingMonth}] Unit ${prop?.unitNumber ?: "-"} - ${t?.name ?: "Penyewa"}")
                        appendLine("   No Resit : ${p.receiptNumber} | Tarikh Bayar: ${p.paymentDate ?: "-"}")
                        appendLine("   Ditagih  : RM ${String.format(Locale.US, "%.2f", p.amountDue)} | Dibayar: RM ${String.format(Locale.US, "%.2f", p.amountPaid)} | Baki: RM ${String.format(Locale.US, "%.2f", p.balanceDue)}")
                        appendLine("   Status   : ${p.status} | Kaedah: ${p.ocrBankName ?: "Online"} | Ruj: ${p.ocrReferenceNo ?: "-"}")
                        appendLine("   -----------------------------------------------------------------------------")
                    }
                }
                appendLine()
                appendLine("SENARAI KOS PENYELENGGARAAN & PEMBAIKAN UNIT:")
                if (maintenance.isEmpty()) {
                    appendLine("  (Tiada rekod penyelenggaraan bagi tempoh ini)")
                } else {
                    maintenance.forEachIndexed { i, m ->
                        val prop = propMap[m.propertyId]
                        appendLine("${i + 1}. [${m.dateReported}] ${prop?.unitNumber ?: "-"} - ${m.category}: ${m.title}")
                        appendLine("   Kos: RM ${String.format(Locale.US, "%.2f", m.cost)} | Status: ${m.status} | Kontraktor: ${m.contractorContact}")
                    }
                }
                appendLine()
                appendLine("================================================================================")
                appendLine("PENGESAHAN DOKUMEN & JEJAK AUDIT:")
                appendLine("Cop Rasmi Tuan Rumah:")
                profile?.officialStampText?.lines()?.forEach { line ->
                    if (line.isNotBlank()) appendLine("  [ $line ]")
                }
                appendLine()
                appendLine("Tandatangan Tuan Rumah: ${profile?.landlordName ?: "AL MUSLIM MUSTAPA"}")
                appendLine("Disahkan oleh Sistem Pengurusan Aset Hartanah UrusSewa berenkripsi selamat.")
                appendLine("================================================================================")
            }

            writer.write(statement)
            writer.flush()
            writer.close()

            shareFile(context, file, "text/plain", "Kongsi Penyata Kewangan Teks (.txt)")
        } catch (e: Exception) {
            Toast.makeText(context, "Ralat menjana fail teks: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Eksport Penyata Kewangan dalam bentuk fail PDF (.pdf) bertaraf korporat profesional.
     */
    fun exportToPdf(
        context: Context,
        payments: List<Payment>,
        properties: List<Property>,
        tenants: List<Tenant>,
        maintenance: List<Maintenance>,
        profile: LandlordProfile? = null,
        periodLabel: String = "Semua Tempoh",
        tenantFilterLabel: String = "Semua Penyewa"
    ) {
        val pdfDocument = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val propMap = properties.associateBy { it.id }
            val tenantMap = tenants.associateBy { it.id }

            val totalDue = payments.sumOf { it.amountDue }
            val totalCollected = payments.sumOf { it.amountPaid }
            val totalOverdue = payments.sumOf { it.balanceDue }
            val totalMaintenance = maintenance.sumOf { it.cost }
            val bankCost = if (profile?.includeBankCost == true) properties.sumOf { it.bankLoanPayment } else 0.0
            val managerCost = if (profile?.includeManagementFee == true) profile.managementFeeAmount else 0.0
            val totalCosts = totalMaintenance + bankCost + managerCost
            val netIncome = totalCollected - totalCosts

            // Palette
            val primaryNavy = Color.rgb(30, 64, 175)     // #1E40AF
            val darkSlate = Color.rgb(15, 23, 42)        // #0F172A
            val lightBg = Color.rgb(248, 250, 252)       // #F8FAFC
            val borderGray = Color.rgb(226, 232, 240)    // #E2E8F0
            val textDark = Color.rgb(15, 23, 42)
            val textMuted = Color.rgb(100, 116, 139)
            val successGreen = Color.rgb(21, 128, 61)    // #15803D
            val alertRed = Color.rgb(220, 38, 38)        // #DC2626

            // 1. Top Header Banner (Biru Korporat)
            val bannerRect = RectF(24f, 24f, 571f, 88f)
            val bannerPaint = Paint().apply {
                color = primaryNavy
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(bannerRect, 8f, 8f, bannerPaint)

            val titlePaint = Paint().apply {
                color = Color.WHITE
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subtitlePaint = Paint().apply {
                color = Color.rgb(191, 219, 254) // #BFDBFE
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val metaRightPaint = Paint().apply {
                color = Color.WHITE
                textSize = 8.5f
                textAlign = Paint.Align.RIGHT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val metaDatePaint = Paint().apply {
                color = Color.rgb(191, 219, 254)
                textSize = 8f
                textAlign = Paint.Align.RIGHT
            }

            val orgName = profile?.businessName?.ifBlank { "Pengurusan Hartanah & Rumah Sewa" } ?: "Pengurusan Hartanah & Rumah Sewa"
            canvas.drawText(orgName.uppercase(), 38f, 50f, titlePaint)
            canvas.drawText("LAPORAN PENYATA KEWANGAN SEWAAN HARTANAH (RASMI)", 38f, 68f, subtitlePaint)

            canvas.drawText("TEMPOH: $periodLabel", 555f, 50f, metaRightPaint)
            canvas.drawText("Dijana: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}", 555f, 68f, metaDatePaint)

            // 2. Info Bar (Tuan Rumah & Penapis)
            val infoTop = 96f
            val infoFillPaint = Paint().apply {
                color = lightBg
                style = Paint.Style.FILL
            }
            val infoBorderPaint = Paint().apply {
                color = borderGray
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas.drawRoundRect(RectF(24f, infoTop, 571f, infoTop + 34f), 6f, 6f, infoFillPaint)
            canvas.drawRoundRect(RectF(24f, infoTop, 571f, infoTop + 34f), 6f, 6f, infoBorderPaint)

            val infoTextPaint = Paint().apply {
                color = textDark
                textSize = 8f
            }
            val boldInfoPaint = Paint().apply {
                color = textDark
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("Tuan Rumah: ${profile?.landlordName ?: "AL MUSLIM MUSTAPA"} | Tel: ${profile?.phone ?: "0139358650"} | Emel: ${profile?.email ?: "almuslim@ukm.edu.my"}", 34f, infoTop + 14f, boldInfoPaint)
            canvas.drawText("Tapisan: Tempoh [$periodLabel] • Penyewa [$tenantFilterLabel] • Mod Kos Bank [${if (profile?.includeBankCost == true) "Aktif" else "Tidak"}]", 34f, infoTop + 26f, infoTextPaint)

            // 3. 4 KPI Summary Cards (Grid)
            val kpiTop = infoTop + 42f
            val kpiWidth = (547f - 30f) / 4f // 4 equal cards
            val kpiHeight = 50f

            val kpiData = listOf(
                Triple("Jumlah Kutipan", "RM ${String.format(Locale.US, "%.2f", totalCollected)}", successGreen),
                Triple("Sewa Tertunggak", "RM ${String.format(Locale.US, "%.2f", totalOverdue)}", alertRed),
                Triple("Kos Operasi", "RM ${String.format(Locale.US, "%.2f", totalCosts)}", Color.rgb(234, 88, 12)),
                Triple("Pendapatan Bersih", "RM ${String.format(Locale.US, "%.2f", netIncome)}", primaryNavy)
            )

            kpiData.forEachIndexed { i, (label, value, tint) ->
                val cardLeft = 24f + i * (kpiWidth + 10f)
                val cardRight = cardLeft + kpiWidth
                val cardRect = RectF(cardLeft, kpiTop, cardRight, kpiTop + kpiHeight)

                val cardBg = Paint().apply {
                    color = lightBg
                    style = Paint.Style.FILL
                }
                val cardStroke = Paint().apply {
                    color = borderGray
                    style = Paint.Style.STROKE
                    strokeWidth = 1f
                }
                canvas.drawRoundRect(cardRect, 6f, 6f, cardBg)
                canvas.drawRoundRect(cardRect, 6f, 6f, cardStroke)

                // Top colored indicator line
                val linePaint = Paint().apply {
                    color = tint
                    strokeWidth = 3f
                    style = Paint.Style.STROKE
                }
                canvas.drawLine(cardLeft + 6f, kpiTop, cardRight - 6f, kpiTop, linePaint)

                val kpiLabelPaint = Paint().apply {
                    color = textMuted
                    textSize = 7f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val kpiValPaint = Paint().apply {
                    color = tint
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }

                canvas.drawText(label, cardLeft + 8f, kpiTop + 18f, kpiLabelPaint)
                canvas.drawText(value, cardLeft + 8f, kpiTop + 36f, kpiValPaint)
            }

            // 4. Section: Transaksi Kutipan Sewa
            val tableTop = kpiTop + kpiHeight + 14f
            val secTitlePaint = Paint().apply {
                color = textDark
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("JADUAL TRANSAKSI KUTIPAN SEWAAN", 24f, tableTop, secTitlePaint)

            // Table Header (Dark Slate)
            val thTop = tableTop + 6f
            val thPaint = Paint().apply {
                color = darkSlate
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(RectF(24f, thTop, 571f, thTop + 18f), 4f, 4f, thPaint)

            val thTextPaint = Paint().apply {
                color = Color.WHITE
                textSize = 7.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val thRightText = Paint().apply {
                color = Color.WHITE
                textSize = 7.5f
                textAlign = Paint.Align.RIGHT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            canvas.drawText("No Resit", 32f, thTop + 12f, thTextPaint)
            canvas.drawText("Bulan", 100f, thTop + 12f, thTextPaint)
            canvas.drawText("Unit & Hartanah", 160f, thTop + 12f, thTextPaint)
            canvas.drawText("Penyewa", 280f, thTop + 12f, thTextPaint)
            canvas.drawText("Ditagih", 390f, thTop + 12f, thRightText)
            canvas.drawText("Dibayar", 450f, thTop + 12f, thRightText)
            canvas.drawText("Baki", 505f, thTop + 12f, thRightText)
            canvas.drawText("Status", 540f, thTop + 12f, thTextPaint)

            var rowY = thTop + 18f
            val rowHeight = 16f
            val maxRowsToShow = 14
            val displayPayments = payments.take(maxRowsToShow)

            val tdText = Paint().apply {
                color = textDark
                textSize = 7.5f
            }
            val tdBold = Paint().apply {
                color = textDark
                textSize = 7.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val tdRightGreen = Paint().apply {
                color = successGreen
                textSize = 7.5f
                textAlign = Paint.Align.RIGHT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val tdRightRed = Paint().apply {
                color = alertRed
                textSize = 7.5f
                textAlign = Paint.Align.RIGHT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val tdRightMuted = Paint().apply {
                color = textMuted
                textSize = 7.5f
                textAlign = Paint.Align.RIGHT
            }

            displayPayments.forEachIndexed { i, p ->
                val prop = propMap[p.propertyId]
                val tnt = tenantMap[p.tenantId]

                if (i % 2 == 1) {
                    val zebraPaint = Paint().apply {
                        color = Color.rgb(248, 250, 252)
                        style = Paint.Style.FILL
                    }
                    canvas.drawRect(24f, rowY, 571f, rowY + rowHeight, zebraPaint)
                }

                canvas.drawText(p.receiptNumber.take(10), 32f, rowY + 11f, tdText)
                canvas.drawText(p.billingMonth.take(10), 100f, rowY + 11f, tdText)
                canvas.drawText("${prop?.unitNumber ?: "-"} (${prop?.name?.take(10) ?: "-"})", 160f, rowY + 11f, tdText)
                canvas.drawText(tnt?.name?.take(16) ?: "Penyewa", 280f, rowY + 11f, tdBold)
                canvas.drawText("RM ${String.format(Locale.US, "%.0f", p.amountDue)}", 390f, rowY + 11f, tdRightMuted)
                canvas.drawText("RM ${String.format(Locale.US, "%.0f", p.amountPaid)}", 450f, rowY + 11f, tdRightGreen)
                canvas.drawText("RM ${String.format(Locale.US, "%.0f", p.balanceDue)}", 505f, rowY + 11f, if (p.balanceDue > 0) tdRightRed else tdRightMuted)

                val statusColor = when (p.status) {
                    "LUNAS" -> successGreen
                    "SEPARA" -> Color.rgb(217, 119, 6)
                    else -> alertRed
                }
                val statusPaint = Paint().apply {
                    color = statusColor
                    textSize = 7.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText(p.status, 540f, rowY + 11f, statusPaint)

                rowY += rowHeight
            }

            if (payments.size > maxRowsToShow) {
                val morePaint = Paint().apply {
                    color = textMuted
                    textSize = 7f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                }
                canvas.drawText("... dan ${payments.size - maxRowsToShow} lagi rekod transaksi (lihat lampiran CSV/Excel untuk senarai penuh)", 32f, rowY + 12f, morePaint)
                rowY += 16f
            }

            // 5. Section: Penyelenggaraan Ringkas
            val maintTop = rowY + 10f
            canvas.drawText("PENYELENGGARAAN & KOS PENGURUSAN", 24f, maintTop, secTitlePaint)

            val maintBoxTop = maintTop + 6f
            val maintBoxHeight = 44f
            canvas.drawRoundRect(RectF(24f, maintBoxTop, 571f, maintBoxTop + maintBoxHeight), 6f, 6f, infoFillPaint)
            canvas.drawRoundRect(RectF(24f, maintBoxTop, 571f, maintBoxTop + maintBoxHeight), 6f, 6f, infoBorderPaint)

            val mText1 = if (maintenance.isEmpty()) {
                "Penyelenggaraan: Tiada rekod pembaikan dilaporkan bagi tempoh ini."
            } else {
                "Penyelenggaraan: ${maintenance.size} rekod diselesaikan (Jumlah Kos: RM ${String.format(Locale.US, "%.2f", totalMaintenance)})"
            }
            val mText2 = "Ansuran Bank: RM ${String.format(Locale.US, "%.2f", bankCost)} [${if (profile?.includeBankCost == true) "Diambil Kira Dalam Untung Bersih" else "Dikecualikan"}] • Kos Pengurus: RM ${String.format(Locale.US, "%.2f", managerCost)}"

            canvas.drawText(mText1, 34f, maintBoxTop + 16f, boldInfoPaint)
            canvas.drawText(mText2, 34f, maintBoxTop + 32f, infoTextPaint)

            // 6. Cop Rasmi & Tandatangan di Bawah
            val bottomTop = maintBoxTop + maintBoxHeight + 14f

            // Cop Rasmi Rubber Stamp (Blue double ring)
            val stampBox = RectF(24f, bottomTop, 220f, bottomTop + 68f)
            val stampOuter = Paint().apply {
                color = primaryNavy
                style = Paint.Style.STROKE
                strokeWidth = 1.8f
            }
            val stampInner = Paint().apply {
                color = primaryNavy
                style = Paint.Style.STROKE
                strokeWidth = 0.8f
            }
            canvas.drawRoundRect(stampBox, 6f, 6f, stampOuter)
            canvas.drawRoundRect(RectF(27f, bottomTop + 3f, 217f, bottomTop + 65f), 4f, 4f, stampInner)

            val stampLines = profile?.officialStampText?.lines()?.map { it.trim() }?.filter { it.isNotBlank() }
                ?: listOf("★ COP RASMI TUAN RUMAH ★", "DISAHKAN LUNAS", "BAYARAN SEWA DITERIMA", "PENGURUSAN SEWAAN")

            val stampTextPaint = Paint().apply {
                color = primaryNavy
                textAlign = Paint.Align.CENTER
                textSize = 7.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val stampCenter = stampBox.centerX()
            canvas.drawText(stampLines.getOrElse(0) { "★ COP RASMI TUAN RUMAH ★" }, stampCenter, bottomTop + 18f, stampTextPaint)
            canvas.drawText(stampLines.getOrElse(1) { "DISAHKAN LUNAS" }, stampCenter, bottomTop + 32f, stampTextPaint)
            canvas.drawText(stampLines.getOrElse(2) { "BAYARAN SEWA DITERIMA" }, stampCenter, bottomTop + 45f, stampTextPaint)
            canvas.drawText(stampLines.getOrElse(3) { "PENGURUSAN SEWAAN" }, stampCenter, bottomTop + 58f, stampTextPaint)

            // Tandatangan Tuan Rumah (Right)
            val sigLeft = 380f
            canvas.drawLine(sigLeft, bottomTop + 45f, 560f, bottomTop + 45f, Paint().apply { color = borderGray; strokeWidth = 1.2f })
            canvas.drawText("Tandatangan Tuan Rumah / Pengurus", sigLeft, bottomTop + 58f, tdBold)
            canvas.drawText(profile?.landlordName ?: "AL MUSLIM MUSTAPA", sigLeft, bottomTop + 40f, Paint().apply {
                color = textDark
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            })

            // Footer Notice
            val footerPaint = Paint().apply {
                color = textMuted
                textSize = 7f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Penyata kewangan ini dijana secara digital & disahkan berintegriti melalui Sistem UrusSewa Landlord Suite.", 297f, 825f, footerPaint)

            pdfDocument.finishPage(page)

            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(exportDir, "Penyata_Kewangan_PDF_$timeStamp.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()

            shareFile(context, file, "application/pdf", "Kongsi Penyata Kewangan PDF (.pdf)")
        } catch (e: Exception) {
            Toast.makeText(context, "Ralat menjana fail PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri: Uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, title))
    }
}
