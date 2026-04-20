package com.azhar.smartscanindia.utils

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

object ImageProcessingUtils {
    fun autoCropDocument(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        var left = w
        var right = 0
        var top = h
        var bottom = 0
        for (y in 0 until h step 4) {
            for (x in 0 until w step 4) {
                val p = bitmap.getPixel(x, y)
                val luminance = (Color.red(p) * 0.299 + Color.green(p) * 0.587 + Color.blue(p) * 0.114).toInt()
                if (luminance < 245) {
                    left = min(left, x)
                    right = max(right, x)
                    top = min(top, y)
                    bottom = max(bottom, y)
                }
            }
        }
        if (right <= left || bottom <= top) return bitmap
        val pad = 20
        val cl = max(0, left - pad)
        val ct = max(0, top - pad)
        val cr = min(w - 1, right + pad)
        val cb = min(h - 1, bottom + pad)
        return Bitmap.createBitmap(bitmap, cl, ct, cr - cl, cb - ct)
    }
}
