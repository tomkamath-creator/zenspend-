package com.vk.zenspend.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageStorageHelper {

    fun saveImageLocally(context: Context, sourceUri: Uri): String? {
        return try {
            val receiptsDir = File(context.filesDir, "receipts").apply {
                if (!exists()) mkdirs()
            }
            val fileName = "bill_${System.currentTimeMillis()}.jpg"
            val destFile = File(receiptsDir, fileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            inputStream?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun loadBitmap(pathOrUri: String, context: Context): Bitmap? {
        return try {
            val localFile = File(pathOrUri)
            if (localFile.exists() && localFile.isFile) {
                decodeSampledBitmap(localFile.absolutePath, 1400, 1400)
            } else {
                val uri = Uri.parse(pathOrUri)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun decodeSampledBitmap(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(filePath, options)

        var inSampleSize = 1
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            val halfHeight: Int = options.outHeight / 2
            val halfWidth: Int = options.outWidth / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        val decodeOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return BitmapFactory.decodeFile(filePath, decodeOptions)
    }
}
