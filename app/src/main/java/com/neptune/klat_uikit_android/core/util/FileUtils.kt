package com.neptune.klat_uikit_android.core.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        val contentResolver: ContentResolver = context.contentResolver
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                copyStream(inputStream, outputStream)
            }
        }
        return file
    }

    fun resizeImage(file: File, targetWidth: Int = 1050, targetHeight: Int = 1400): File {
        val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)

        val exif = ExifInterface(file.absolutePath)
        val rotationDegrees = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        val correctedBitmap = if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
        } else {
            originalBitmap
        }

        val resizedBitmap = Bitmap.createScaledBitmap(correctedBitmap, targetWidth, targetHeight, true)
        val resizedFile = File(file.parent, "resized_${file.name}")

        FileOutputStream(resizedFile).use { outputStream ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        }

        originalBitmap.recycle()
        correctedBitmap.recycle()
        resizedBitmap.recycle()

        return resizedFile
    }

    private fun copyStream(input: InputStream, output: FileOutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }
}