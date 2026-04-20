package com.azhar.smartscanindia.ui.aadhaar

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.databinding.FragmentAadhaarBinding
import com.azhar.smartscanindia.utils.FileUtils
import com.azhar.smartscanindia.utils.ImageProcessingUtils
import com.azhar.smartscanindia.utils.PdfToolkit
import java.io.File

class AadhaarFragment : Fragment(R.layout.fragment_aadhaar) {
    private var _binding: FragmentAadhaarBinding? = null
    private val binding get() = _binding!!

    private var frontFile: File? = null
    private var backFile: File? = null
    private var pendingCaptureType: String? = null

    private val captureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            when (pendingCaptureType) {
                "front" -> frontFile?.let { processCapture(it, true) }
                "back" -> backFile?.let { processCapture(it, false) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAadhaarBinding.bind(view)

        binding.btnScanFront.setOnClickListener { dispatchCapture(true) }
        binding.btnScanBack.setOnClickListener { dispatchCapture(false) }
        binding.btnCombine.setOnClickListener { combineFrontBack() }
    }

    private fun dispatchCapture(front: Boolean) {
        val file = FileUtils.newImageFile(requireContext(), if (front) "AADHAAR_FRONT" else "AADHAAR_BACK")
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        if (front) {
            frontFile = file
            pendingCaptureType = "front"
        } else {
            backFile = file
            pendingCaptureType = "back"
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
        captureLauncher.launch(intent)
    }

    private fun processCapture(file: File, front: Boolean) {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return
        val cropped = ImageProcessingUtils.autoCropDocument(bitmap)
        file.outputStream().use { cropped.compress(android.graphics.Bitmap.CompressFormat.JPEG, 94, it) }
        showMessage(if (front) "Front scanned" else "Back scanned")
    }

    private fun combineFrontBack() {
        val front = frontFile
        val back = backFile
        if (front == null || back == null) {
            showMessage("Capture both front and back first")
            return
        }
        val output = PdfToolkit.imageToPdf(requireContext(), listOf(front, back), "aadhaar_combined.pdf")
        showMessage("Saved ${output.name}")
    }

    private fun showMessage(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
