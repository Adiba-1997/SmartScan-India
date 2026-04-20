package com.azhar.smartscanindia.ui.document

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.databinding.FragmentDocumentScannerBinding
import com.azhar.smartscanindia.utils.ImageProcessingUtils
import com.azhar.smartscanindia.utils.PdfToolkit

class DocumentScannerFragment : Fragment(R.layout.fragment_document_scanner) {
    private var _binding: FragmentDocumentScannerBinding? = null
    private val binding get() = _binding!!
    private val capturedPages = mutableListOf<java.io.File>()

    private val scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            val file = uri.toFile()
            capturedPages += file
            toast("Page captured (${capturedPages.size})")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDocumentScannerBinding.bind(view)

        binding.btnLaunchCameraX.setOnClickListener {
            scanLauncher.launch(Intent(requireContext(), CameraXScannerActivity::class.java))
        }
        binding.btnAutoDetect.setOnClickListener { autoCropLastPage() }
        binding.btnMultiPage.setOnClickListener { saveAsPdf() }
    }

    private fun autoCropLastPage() {
        val last = capturedPages.lastOrNull() ?: run {
            toast("Capture at least one page first")
            return
        }
        val bitmap = BitmapFactory.decodeFile(last.absolutePath) ?: return
        val cropped = ImageProcessingUtils.autoCropDocument(bitmap)
        java.io.FileOutputStream(last).use { cropped.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, it) }
        toast("Auto crop applied to latest page")
    }

    private fun saveAsPdf() {
        if (capturedPages.isEmpty()) {
            toast("No pages to save")
            return
        }
        val file = PdfToolkit.imageToPdf(requireContext(), capturedPages, "document_scan.pdf")
        toast("Saved ${file.name}")
    }

    private fun toast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
