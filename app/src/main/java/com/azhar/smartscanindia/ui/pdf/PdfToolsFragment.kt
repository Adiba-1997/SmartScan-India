package com.azhar.smartscanindia.ui.pdf

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.databinding.FragmentPdfToolsBinding
import com.azhar.smartscanindia.utils.FileUtils
import com.azhar.smartscanindia.utils.PdfToolkit
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class PdfToolsFragment : Fragment(R.layout.fragment_pdf_tools) {
    private var _binding: FragmentPdfToolsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPdfToolsBinding.bind(view)
        PDFBoxResourceLoader.init(requireContext())

        binding.btnMerge.setOnClickListener { mergeDemo() }
        binding.btnSplit.setOnClickListener { splitDemo() }
        binding.btnCompress.setOnClickListener { compressDemo() }
        binding.btnLock.setOnClickListener { lockDemo() }
        binding.btnImageToPdf.setOnClickListener { imageToPdfDemo() }
    }

    private fun createColorImage(prefix: String, color: Int): java.io.File {
        val file = FileUtils.newImageFile(requireContext(), prefix)
        val bitmap = Bitmap.createBitmap(1000, 1400, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(color)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return file
    }

    private fun imageToPdfDemo() {
        val image = createColorImage("IMG2PDF", Color.WHITE)
        val pdf = PdfToolkit.imageToPdf(requireContext(), listOf(image), "image_to_pdf_demo.pdf")
        notify("Created ${pdf.name}")
    }

    private fun mergeDemo() {
        val a = PdfToolkit.imageToPdf(requireContext(), listOf(createColorImage("MERGE_A", Color.WHITE)), "merge_a.pdf")
        val b = PdfToolkit.imageToPdf(requireContext(), listOf(createColorImage("MERGE_B", Color.LTGRAY)), "merge_b.pdf")
        val merged = PdfToolkit.mergePdfs(requireContext(), listOf(a, b), "merged_demo.pdf")
        notify("Merged -> ${merged.name}")
    }

    private fun splitDemo() {
        val src = PdfToolkit.imageToPdf(requireContext(), listOf(createColorImage("SPLIT_1", Color.WHITE), createColorImage("SPLIT_2", Color.GRAY)), "split_source.pdf")
        val files = PdfToolkit.splitPdf(requireContext(), src)
        notify("Split into ${files.size} PDFs")
    }

    private fun compressDemo() {
        val src = PdfToolkit.imageToPdf(requireContext(), listOf(createColorImage("COMPRESS", Color.WHITE)), "compress_source.pdf")
        val out = PdfToolkit.compressPdf(requireContext(), src)
        notify("Compressed -> ${out.name}")
    }

    private fun lockDemo() {
        val src = PdfToolkit.imageToPdf(requireContext(), listOf(createColorImage("LOCK", Color.WHITE)), "lock_source.pdf")
        val out = PdfToolkit.lockPdf(requireContext(), src, "1234")
        notify("Locked -> ${out.name} (pwd:1234)")
    }

    private fun notify(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
