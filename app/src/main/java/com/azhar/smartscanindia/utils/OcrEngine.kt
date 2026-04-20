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
        val scaled = if (width < 1200) {
            val ratio = 1200f / width
            Bitmap.createScaledBitmap(input, 1200, (height * ratio).roundToInt(), true)
        } else {
            input.copy(Bitmap.Config.ARGB_8888, true)
        }

        val bmp = scaled.copy(Bitmap.Config.ARGB_8888, true)
        for (y in 0 until bmp.height) {
            for (x in 0 until bmp.width) {
                val pixel = bmp.getPixel(x, y)
                val gray = (Color.red(pixel) * 0.3 + Color.green(pixel) * 0.59 + Color.blue(pixel) * 0.11).toInt()
                val boosted = ((gray - 128) * 1.25 + 128).toInt().coerceIn(0, 255)
                bmp.setPixel(x, y, Color.rgb(boosted, boosted, boosted))
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
                val best = result.textBlocks.joinToString("\n") { it.text.trim() }.trim()
                onDone(best)
            }
            .addOnFailureListener(onError)
    }
}
