package `in`.paperboxd.app.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Instagram's "share to story" intent with the card as the background image, or
 * null when Instagram isn't installed (needs the <queries> entry in the manifest).
 * Set the image with `setDataAndType(uri, "image/png")` before starting it.
 */
fun instagramStoryIntent(context: Context): Intent? {
    val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
        putExtra("source_application", context.packageName)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        type = "image/png"
    }
    return intent.takeIf { context.packageManager.resolveActivity(it, 0) != null }
}

/** Writes a share card to cache and returns a FileProvider content:// URI for it. */
fun cacheShareImage(context: Context, bitmap: Bitmap, name: String): Uri? = runCatching {
    val dir = File(context.cacheDir, "shares").apply { mkdirs() }
    val file = File(dir, "$name.png")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}.getOrNull()
