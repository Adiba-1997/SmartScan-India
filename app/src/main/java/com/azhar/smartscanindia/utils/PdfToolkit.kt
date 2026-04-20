package com.azhar.smartscanindia.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import java.io.File
import java.io.FileOutputStream

object PdfToolkit {
    fun imageToPdf(context: Context, images: List<File>, outputName: String): File {
        val output = File(FileUtils.ensureDir(context, "pdfs"), outputName)
        val document = PDDocument()
        images.forEach { imageFile ->
            val bmp = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return@forEach
            val page = PDPage(PDRectangle.A4)
            document.addPage(page)
            val pdImage = LosslessFactory.createFromImage(document, bmp)
            val stream = PDPageContentStream(document, page)
            val pageWidth = page.mediaBox.width
            val pageHeight = page.mediaBox.height
            val ratio = minOf(pageWidth / bmp.width, pageHeight / bmp.height)
            val drawW = bmp.width * ratio
            val drawH = bmp.height * ratio
            val x = (pageWidth - drawW) / 2f
            val y = (pageHeight - drawH) / 2f
            stream.drawImage(pdImage, x, y, drawW, drawH)
            stream.close()
        }
        document.save(output)
        document.close()
        return output
    }

    fun mergePdfs(context: Context, inputs: List<File>, outputName: String): File {
        val output = File(FileUtils.ensureDir(context, "pdfs"), outputName)
        val merger = PDFMergerUtility()
        inputs.forEach { merger.addSource(it) }
        merger.destinationFileName = output.absolutePath
        merger.mergeDocuments(null)
        return output
    }

    fun splitPdf(context: Context, input: File): List<File> {
        val source = PDDocument.load(input)
        val out = mutableListOf<File>()
        source.pages.forEachIndexed { index, page ->
            val doc = PDDocument()
            doc.addPage(page)
            val outFile = File(FileUtils.ensureDir(context, "pdfs"), "split_${index + 1}_${input.name}")
            doc.save(outFile)
            doc.close()
            out += outFile
        }
        source.close()
        return out
    }

    fun compressPdf(context: Context, input: File): File {
        val src = PDDocument.load(input)
        val outFile = File(FileUtils.ensureDir(context, "pdfs"), "compressed_${input.name}")
        src.save(outFile)
        src.close()
        return outFile
    }

    fun lockPdf(context: Context, input: File, password: String): File {
        val src = PDDocument.load(input)
        val permission = AccessPermission()
        val policy = StandardProtectionPolicy(password, password, permission).apply {
            encryptionKeyLength = 128
            permissions = permission
        }
        src.protect(policy)
        val outFile = File(FileUtils.ensureDir(context, "pdfs"), "locked_${input.name}")
        src.save(outFile)
        src.close()
        return outFile
    }

    fun applySignature(inputPdf: File, signature: Bitmap, outputPdf: File) {
        val document = PDDocument.load(inputPdf)
        val page = document.getPage(0)
        val image = LosslessFactory.createFromImage(document, signature)
        val stream = PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)
        stream.drawImage(image, 48f, 48f, 180f, 80f)
        stream.close()
        document.save(outputPdf)
        document.close()
    }

    fun pdfFirstPageToBitmap(file: File): Bitmap? {
        val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(pfd)
        val page = renderer.openPage(0)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        renderer.close()
        pfd.close()
        return bitmap
    }
}
