package com.azhar.smartscanindia.ui.pan

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
import com.azhar.smartscanindia.databinding.FragmentPanBinding
import com.azhar.smartscanindia.utils.FileUtils
import com.azhar.smartscanindia.utils.ImageProcessingUtils
import com.azhar.smartscanindia.utils.PdfToolkit
import java.io.File

class PanFragment : Fragment(R.layout.fragment_pan) {
    private var _binding: FragmentPanBinding? = null
    private val binding get() = _binding!!
    private var panImageFile: File? = null

    private val captureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            panImageFile?.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return@let
                val cropped = ImageProcessingUtils.autoCropDocument(bitmap)
                file.outputStream().use { cropped.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, it) }
                val pdf = PdfToolkit.imageToPdf(requireContext(), listOf(file), "pan_card.pdf")
                Toast.makeText(requireContext(), "Saved ${pdf.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPanBinding.bind(view)

        binding.btnScanPan.setOnClickListener {
            panImageFile = FileUtils.newImageFile(requireContext(), "PAN")
            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", panImageFile!!)
            captureLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
