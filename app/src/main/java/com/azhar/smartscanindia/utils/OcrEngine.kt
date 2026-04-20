package com.azhar.smartscanindia.utils

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object OcrEngine {
    enum class Language(val displayName: String) {
        ENGLISH("English"),
        HINDI("Hindi"),
        TELUGU("Telugu"),
        URDU("Urdu"),
        TAMIL("Tamil")
    }

    fun extractText(bitmap: Bitmap, language: Language, onDone: (String) -> Unit, onError: (Exception) -> Unit) {
        val options = when (language) {
            Language.HINDI -> DevanagariTextRecognizerOptions.Builder().build()
            else -> TextRecognizerOptions.DEFAULT_OPTIONS
        }
        val recognizer = TextRecognition.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { result -> onDone(result.text) }
            .addOnFailureListener(onError)
    }
}
