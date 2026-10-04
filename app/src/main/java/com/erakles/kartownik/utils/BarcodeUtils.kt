package com.erakles.kartownik.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.util.EnumMap

object BarcodeUtils {

    fun generateBarcodeBitmap(
        content: String,
        formatName: String,
        width: Int = 800,
        height: Int = 300
    ): Bitmap? {
        if (content.isBlank()) return null

        val format = mapStringToZxingFormat(formatName)
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.MARGIN, 2)
        }

        return try {
            val actualHeight = if (format == BarcodeFormat.QR_CODE || format == BarcodeFormat.AZTEC) width else height
            val bitMatrix = MultiFormatWriter().encode(content, format, width, actualHeight, hints)
            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height
            val pixels = IntArray(matrixWidth * matrixHeight)

            for (y in 0 until matrixHeight) {
                val offset = y * matrixWidth
                for (x in 0 until matrixWidth) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
            }
        } catch (e: Exception) {
            // Fallback na CODE_128 jeśli dany format (np. EAN-13) miał niepoprawną długość lub znaki
            try {
                val fallbackMatrix = MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, width, height, hints)
                val mWidth = fallbackMatrix.width
                val mHeight = fallbackMatrix.height
                val pixels = IntArray(mWidth * mHeight)
                for (y in 0 until mHeight) {
                    val offset = y * mWidth
                    for (x in 0 until mWidth) {
                        pixels[offset + x] = if (fallbackMatrix.get(x, y)) Color.BLACK else Color.WHITE
                    }
                }
                Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888).apply {
                    setPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight)
                }
            } catch (fallbackException: Exception) {
                null
            }
        }
    }

    private fun mapStringToZxingFormat(formatName: String): BarcodeFormat {
        return when (formatName.uppercase()) {
            "QR_CODE" -> BarcodeFormat.QR_CODE
            "EAN_13" -> BarcodeFormat.EAN_13
            "EAN_8" -> BarcodeFormat.EAN_8
            "CODE_39" -> BarcodeFormat.CODE_39
            "CODE_93" -> BarcodeFormat.CODE_93
            "UPC_A" -> BarcodeFormat.UPC_A
            "UPC_E" -> BarcodeFormat.UPC_E
            "PDF_417" -> BarcodeFormat.PDF_417
            "AZTEC" -> BarcodeFormat.AZTEC
            "ITF" -> BarcodeFormat.ITF
            else -> BarcodeFormat.CODE_128
        }
    }

    fun mapMlKitFormatToString(mlKitFormat: Int): String {
        return when (mlKitFormat) {
            Barcode.FORMAT_QR_CODE -> "QR_CODE"
            Barcode.FORMAT_EAN_13 -> "EAN_13"
            Barcode.FORMAT_EAN_8 -> "EAN_8"
            Barcode.FORMAT_CODE_128 -> "CODE_128"
            Barcode.FORMAT_CODE_39 -> "CODE_39"
            Barcode.FORMAT_CODE_93 -> "CODE_93"
            Barcode.FORMAT_UPC_A -> "UPC_A"
            Barcode.FORMAT_UPC_E -> "UPC_E"
            Barcode.FORMAT_PDF417 -> "PDF_417"
            Barcode.FORMAT_AZTEC -> "AZTEC"
            Barcode.FORMAT_ITF -> "ITF"
            else -> "CODE_128"
        }
    }
}
