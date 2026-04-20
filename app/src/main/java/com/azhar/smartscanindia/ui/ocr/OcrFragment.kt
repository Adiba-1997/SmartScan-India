package com.azhar.smartscanindia.ui.ocr

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.databinding.FragmentOcrBinding
import com.azhar.smartscanindia.utils.FileUtils
import com.azhar.smartscanindia.utils.OcrEngine
import java.io.File

class OcrFragment : Fragment(R.layout.fragment_ocr) {
    private var _binding: FragmentOcrBinding? = null
    private val binding get() = _binding!!
    private var imageFile: File? = null

    private val captureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) runOcr()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOcrBinding.bind(view)

        val languages = OcrEngine.Language.values().map { it.displayName }
        binding.spinnerLanguage.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)

        binding.btnCaptureForOcr.setOnClickListener {
            imageFile = FileUtils.newImageFile(requireContext(), "OCR")
            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", imageFile!!)
            captureLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri))
        }

        binding.btnRunOcr.setOnClickListener { runOcr() }
    }

    private fun runOcr() {
        val file = imageFile ?: run {
            Toast.makeText(requireContext(), "Capture image first", Toast.LENGTH_SHORT).show(); return
        }
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return
        val language = OcrEngine.Language.values()[binding.spinnerLanguage.selectedItemPosition]
        binding.tvOcrResult.text = "Recognizing..."
        OcrEngine.extractText(bitmap, language,
            onDone = { text -> binding.tvOcrResult.text = if (text.isBlank()) "No text detected" else text },
            onError = { e -> binding.tvOcrResult.text = "OCR failed: ${e.message}" })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
