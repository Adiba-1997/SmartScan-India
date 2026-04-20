package com.azhar.smartscanindia.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.multipdf.Splitter
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.io.File
import java.io.FileOutputStream

object PdfToolkit {
    fun imageToPdf(context: Context, images: List<File>, outputName: String, jpegQuality: Float = 0.85f): File {
        val output = File(FileUtils.ensureDir(context, "pdfs"), outputName)
        val document = PDDocument()
        images.forEach { imageFile ->
            val bmp = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return@forEach
            val page = PDPage(PDRectangle.A4)
            document.addPage(page)
            val pdImage = JPEGFactory.createFromImage(document, bmp, jpegQuality)
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
        val splitDocuments = Splitter().split(source)
        val out = mutableListOf<File>()
        splitDocuments.forEachIndexed { index, doc ->
            val outFile = File(FileUtils.ensureDir(context, "pdfs"), "split_${index + 1}_${input.name}")
            doc.save(outFile)
            doc.close()
            out += outFile
        }
        source.close()
        return out
    }

    fun compressPdf(context: Context, input: File): File {
        val pagesAsImages = mutableListOf<File>()
        val pfd = ParcelFileDescriptor.open(input, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(pfd)
        try {
            for (i in 0 until renderer.pageCount) {
                val page = renderer.openPage(i)
                val targetW = (page.width * 0.6f).toInt().coerceAtLeast(720)
                val targetH = (page.height * 0.6f).toInt().coerceAtLeast(960)
                val bitmap = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                val imageFile = FileUtils.newImageFile(context, "COMP_PAGE_${i + 1}")
                FileOutputStream(imageFile).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 55, it) }
                bitmap.recycle()
                pagesAsImages += imageFile
                page.close()
            }
        } finally {
            renderer.close()
            pfd.close()
        }
        val out = imageToPdf(context, pagesAsImages, "compressed_${input.name}", jpegQuality = 0.55f)
        pagesAsImages.forEach { it.delete() }
        return out
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
}
