package `in`.paperboxd.app.ui.components

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// Toast copy, shared by every "save the card" tile.
private const val SAVED = "Saved to Photos"
private const val FAILED = "Couldn’t save image"
private const val DENIED = "Storage access needed to save"

/**
 * Writing to `MediaStore.Images` only became permission-free in API 29 (scoped storage).
 * Below that the insert throws `SecurityException` without WRITE_EXTERNAL_STORAGE, which
 * is declared `maxSdkVersion="28"` so API 29+ never sees it in the app's permission set.
 */
private val needsStoragePermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

/**
 * Saves a rendered card into Pictures/PaperBoxd, handling the pre-Q permission grant, and
 * hands back the toast to show.
 *
 * ```kotlin
 * val saveCard = rememberGallerySaver { toast = it }
 * // …
 * scope.launch { saveCard(render()) }
 * ```
 *
 * The bitmap is held in [pending] across the permission dialog because the grant resolves
 * asynchronously, long after the tile's click handler has returned.
 *
 * ponytail: a permanent denial just re-reports [DENIED] — no "Open Settings" escape hatch
 * like ScanScreen has, since a save tile is retryable and not a dead end. Add one if
 * support tickets say otherwise.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberGallerySaver(onResult: (String) -> Unit): (Bitmap) -> Unit {
    val context = LocalContext.current
    var pending by remember { mutableStateOf<Bitmap?>(null) }

    val permission = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
        val bitmap = pending
        pending = null
        onResult(
            when {
                !granted -> DENIED
                bitmap == null -> FAILED
                else -> if (saveToGallery(context, bitmap)) SAVED else FAILED
            }
        )
    }

    return { bitmap ->
        if (!needsStoragePermission || permission.status.isGranted) {
            onResult(if (saveToGallery(context, bitmap)) SAVED else FAILED)
        } else {
            pending = bitmap
            permission.launchPermissionRequest()
        }
    }
}

/**
 * Saves the card into the device gallery (Pictures/PaperBoxd) via MediaStore.
 *
 * Private on purpose: routing every caller through [rememberGallerySaver] is what stops a
 * new save button from silently failing on API 26–28 the way this one used to.
 */
private fun saveToGallery(context: Context, bitmap: Bitmap): Boolean = runCatching {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "paperboxd-${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/PaperBoxd")
        }
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
    resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    true
}.getOrDefault(false)
