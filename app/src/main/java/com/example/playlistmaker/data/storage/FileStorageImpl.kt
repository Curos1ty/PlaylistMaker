package com.example.playlistmaker.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.domain.repository.FileStorage
import java.io.File
import java.io.FileOutputStream

class FileStorageImpl(private val context: Context) : FileStorage {
    override fun saveImage(uri: Uri): String? {
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLIST_COVER_ALBUM
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            ?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        inputStream?.close()
        return file.absolutePath
    }

    companion object {
        private const val PLAYLIST_COVER_ALBUM = "playlist_cover_album"
    }
}