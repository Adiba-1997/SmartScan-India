package com.azhar.smartscanindia.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.math.roundToInt

object OcrEngine {
    enum class Language(val displayName: String) {
        ENGLISH("English"),
        HINDI("Hindi"),
        TELUGU("Telugu"),
        URDU("Urdu"),
        TAMIL("Tamil")
    }

    fun preprocessForOcr(input: Bitmap): Bitmap {
        val width = input.width
        val height = input.height
        val scaled = if (width < 1400) {
            val ratio = 1400f / width
            Bitmap.createScaledBitmap(input, 1400, (height * ratio).roundToInt(), true)
        } else {
            input.copy(Bitmap.Config.ARGB_8888, true)
        }

        val bmp = scaled.copy(Bitmap.Config.ARGB_8888, true)
        var sum = 0L
        for (y in 0 until bmp.height step 2) {
            for (x in 0 until bmp.width step 2) {
                val p = bmp.getPixel(x, y)
                sum += (Color.red(p) * 30 + Color.green(p) * 59 + Color.blue(p) * 11) / 100
            }
        }
        val sampleCount = ((bmp.width / 2f) * (bmp.height / 2f)).coerceAtLeast(1f)
        val mean = (sum / sampleCount).toInt().coerceIn(80, 180)

        for (y in 0 until bmp.height) {
            for (x in 0 until bmp.width) {
                val p = bmp.getPixel(x, y)
                val gray = (Color.red(p) * 0.3 + Color.green(p) * 0.59 + Color.blue(p) * 0.11).toInt()
                val boosted = ((gray - mean) * 1.35 + 128).toInt().coerceIn(0, 255)
                val denoised = if (boosted > 245) 255 else boosted
                bmp.setPixel(x, y, Color.rgb(denoised, denoised, denoised))
            }
        }
        return bmp
    }

    fun extractText(bitmap: Bitmap, language: Language, onDone: (String) -> Unit, onError: (Exception) -> Unit) {
        val options = when (language) {
            Language.HINDI -> DevanagariTextRecognizerOptions.Builder().build()
            else -> TextRecognizerOptions.DEFAULT_OPTIONS
        }
        val recognizer = TextRecognition.getClient(options)
        val image = InputImage.fromBitmap(preprocessForOcr(bitmap), 0)
        recognizer.process(image)
            .addOnSuccessListener { result ->
                val best = result.textBlocks
                    .mapNotNull { it.text?.trim() }
                    .filter { it.isNotEmpty() }
                    .joinToString("\n")
                onDone(best)
            }
            .addOnFailureListener(onError)
    }
}
