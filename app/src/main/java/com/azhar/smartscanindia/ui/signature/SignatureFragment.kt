package com.azhar.smartscanindia.ui.signature

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.databinding.FragmentSignatureBinding
import com.azhar.smartscanindia.utils.FileUtils
import com.azhar.smartscanindia.utils.PdfToolkit

class SignatureFragment : Fragment(R.layout.fragment_signature) {
    private var _binding: FragmentSignatureBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignatureBinding.bind(view)

        binding.btnClear.setOnClickListener { binding.signaturePad.clear() }
        binding.btnSave.setOnClickListener { saveAndApplySignature() }
    }

    private fun saveAndApplySignature() {
        if (!binding.signaturePad.hasSignature()) {
            Toast.makeText(requireContext(), "Draw signature first", Toast.LENGTH_SHORT).show()
            return
        }
        val signature = binding.signaturePad.asBitmap()
        val signatureFile = FileUtils.newImageFile(requireContext(), "SIGN")
        signatureFile.outputStream().use { signature.compress(Bitmap.CompressFormat.PNG, 100, it) }

        val blankImage = FileUtils.newImageFile(requireContext(), "SIGN_DOC")
        val bitmap = Bitmap.createBitmap(1000, 1400, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.WHITE) }
        blankImage.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        val srcPdf = PdfToolkit.imageToPdf(requireContext(), listOf(blankImage), "signature_source.pdf")
        val outPdf = FileUtils.newPdfFile(requireContext(), "signature_applied")
        PdfToolkit.applySignature(srcPdf, signature, outPdf)
        Toast.makeText(requireContext(), "Saved ${signatureFile.name} and ${outPdf.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
