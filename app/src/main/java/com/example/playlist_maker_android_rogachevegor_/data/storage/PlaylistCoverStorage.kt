package com.example.playlist_maker_android_rogachevegor_.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.util.UUID

class PlaylistCoverStorage(context: Context) {
    private val applicationContext = context.applicationContext
    private val coverDirectory = File(applicationContext.filesDir, COVER_DIRECTORY_NAME)

    fun copyToLocalStorage(coverImageUri: String?): String? {
        if (coverImageUri == null) {
            return null
        }

        val sourceUri = Uri.parse(coverImageUri)
        if (isLocalCoverUri(sourceUri)) {
            return coverImageUri
        }

        val coverFile = File(
            coverDirectory.apply { mkdirs() },
            "${COVER_FILE_PREFIX}_${System.currentTimeMillis()}_${UUID.randomUUID()}.${
                resolveExtension(sourceUri)
            }"
        )

        return runCatching {
            applicationContext.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                coverFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return null

            Uri.fromFile(coverFile).toString()
        }.getOrElse {
            coverFile.delete()
            null
        }
    }

    fun deleteCover(coverImageUri: String?) {
        if (coverImageUri == null) {
            return
        }

        val coverUri = Uri.parse(coverImageUri)
        if (!isLocalCoverUri(coverUri)) {
            return
        }

        runCatching {
            File(requireNotNull(coverUri.path)).delete()
        }
    }

    private fun isLocalCoverUri(uri: Uri): Boolean {
        if (uri.scheme != ContentResolver.SCHEME_FILE) {
            return false
        }

        val path = uri.path ?: return false
        return runCatching {
            File(path).canonicalPath.startsWith(coverDirectory.canonicalPath)
        }.getOrDefault(false)
    }

    private fun resolveExtension(uri: Uri): String {
        val extensionFromMimeType = applicationContext.contentResolver.getType(uri)?.let { mimeType ->
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
        if (!extensionFromMimeType.isNullOrBlank()) {
            return extensionFromMimeType
        }

        val extensionFromPath = uri.lastPathSegment
            ?.substringAfterLast('.', missingDelimiterValue = "")
            ?.takeIf { it.isNotBlank() }

        return extensionFromPath ?: DEFAULT_COVER_EXTENSION
    }

    private companion object {
        const val COVER_DIRECTORY_NAME = "playlist_covers"
        const val COVER_FILE_PREFIX = "playlist_cover"
        const val DEFAULT_COVER_EXTENSION = "jpg"
    }
}
