package `in`.paperboxd.app.ui.screens.bookdetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import `in`.paperboxd.app.domain.model.AttachKind
import `in`.paperboxd.app.domain.model.AttachmentRef
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.ui.screens.messages.SendSheet
import androidx.compose.material.icons.automirrored.outlined.Send
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.components.cacheShareImage
import `in`.paperboxd.app.ui.components.instagramStoryIntent
import `in`.paperboxd.app.ui.components.rememberGallerySaver
import `in`.paperboxd.app.ui.screens.wrapped.PBW
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Default card canvas, in iOS points mapped 1:1 to dp — mirrors iOS
 * BookShareCardView.Format (540×960 story, 540×540 square). The thought card
 * still draws at this size; the book card supplies its own via `canvasOf`.
 */
enum class ShareFormat(val label: String, val baseW: Dp, val baseH: Dp) {
    Story("STORY", 540.dp, 960.dp),
    Square("SQUARE", 540.dp, 540.dp)
}

/** Reading status in the card's running head — iOS ShareStatus twin. */
enum class ShareStatus(val label: String) {
    Finished("Just finished"),
    Reading("Now reading"),
    Want("Want to read"),
    Favourite("Favourite of 2026")
}

/** The palette is a ground colour, not a filter — iOS BookShareCardView.Ground twin. */
enum class ShareGround(
    val label: String,
    val bg: Color, val fg: Color, val dim: Color, val line: Color, val accent: Color,
    val board: Color, val boardFg: Color, val boardDim: Color, val boardBar: Color
) {
    Ink(
        "INK", PBW.InkDeep, PBW.Cream, PBW.Cream.copy(alpha = 0.58f), PBW.Cream.copy(alpha = 0.2f), PBW.Terra,
        Color(0xFFEFE4D2), PBW.Ink, PBW.Muted, PBW.Terra
    ),
    Paper(
        "PAPER", PBW.Cream, PBW.Ink, PBW.Muted, PBW.Ink.copy(alpha = 0.18f), PBW.TerraDeep,
        PBW.Ink, PBW.Cream, PBW.Cream.copy(alpha = 0.6f), PBW.Terra
    ),
    Terra(
        "TERRA", PBW.TerraDeep, PBW.Cream, PBW.Cream.copy(alpha = 0.66f), PBW.Cream.copy(alpha = 0.28f), PBW.Amber,
        PBW.Cream, PBW.Ink, PBW.Muted, PBW.Amber
    )
}

/** Story emphases: the board as object, the reader's line set large, or stripped back. */
enum class ShareLayout(val label: String) { Cover("COVER"), Quote("QUOTE"), Minimal("MINIMAL") }

/** The prototype's artboard for a format (share2/cards.jsx): 450×800 story, 600×600 square. */
fun bookShareCanvas(format: ShareFormat): DpSize =
    if (format == ShareFormat.Story) DpSize(450.dp, 800.dp) else DpSize(600.dp, 600.dp)

/**
 * "Share book" sheet — iOS BookShareSheet twin. Renders the STORY/SQUARE card in
 * a chosen ground and layout through [ShareCardSheet].
 */
@Composable
fun BookShareSheet(
    book: Book,
    handle: String?,
    rating: Int?,
    note: String?,
    status: ShareStatus?,
    onDismiss: () -> Unit
) {
    var ground by remember { mutableStateOf(ShareGround.Ink) }
    var layout by remember { mutableStateOf(ShareLayout.Cover) }
    var showRating by remember { mutableStateOf(true) }
    var showNote by remember { mutableStateOf(true) }
    val hasRating = (rating ?: 0) > 0
    val hasNote = !note.isNullOrBlank()

    ShareCardSheet(
        link = "https://paperboxd.in/b/${book.slug ?: book.id}",
        imageName = "book-share",
        onDismiss = onDismiss,
        send = AttachmentRef(AttachKind.BOOK, book.id),
        canvasOf = ::bookShareCanvas,
        controls = { format ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShareGround.entries.forEach { g ->
                    ShareChip(g.label, ground == g, Modifier.weight(1f), swatch = g.bg) { ground = g }
                }
            }
            if (format == ShareFormat.Story) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShareLayout.entries.forEach { l ->
                        if (l == ShareLayout.Quote && !hasNote) return@forEach
                        ShareChip(l.label, layout == l, Modifier.weight(1f)) { layout = l }
                    }
                }
            }
            // Minimal never sets the note and quote is the note, so the toggle
            // only shows where it does something.
            val noteToggle = hasNote && (format == ShareFormat.Square || layout == ShareLayout.Cover)
            if (hasRating || noteToggle) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (hasRating) ShareChip("RATING", showRating, Modifier.weight(1f)) { showRating = !showRating }
                    if (noteToggle) ShareChip("NOTE", showNote, Modifier.weight(1f)) { showNote = !showNote }
                }
            }
        }
    ) { format, modifier ->
        ShareCard(
            title = book.title,
            author = book.authorLine,
            year = book.publishedYear,
            coverUrl = book.coverUrl,
            handle = handle,
            rating = rating,
            note = note,
            statusLabel = status?.label.orEmpty(),
            ground = ground,
            layout = layout,
            showRating = showRating,
            showNote = showNote,
            format = format,
            modifier = modifier
        )
    }
}

/**
 * Share chrome every card uses — iOS ShareCardSheet twin: live preview, STORY /
 * SQUARE toggle, card-specific [controls], and Instagram Story / More / Save /
 * Copy. The visible card is rasterized on demand via a GraphicsLayer, so what
 * the user sees is exactly what ships.
 *
 * [card] must compose at [canvasOf] for the format (requiredSize) and apply the
 * modifier it is given — that is where the preview scale and capture live.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareCardSheet(
    link: String,
    imageName: String,
    onDismiss: () -> Unit,
    canvasOf: (ShareFormat) -> DpSize = { DpSize(it.baseW, it.baseH) },
    /** When set, "Send on Paperboxd" leads the options and DMs this object. */
    send: AttachmentRef? = null,
    controls: @Composable ColumnScope.(format: ShareFormat) -> Unit = {},
    card: @Composable (format: ShareFormat, modifier: Modifier) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    var format by remember { mutableStateOf(ShareFormat.Story) }
    var toast by remember { mutableStateOf<String?>(null) }
    var showSend by remember { mutableStateOf(false) }
    val saveCard = rememberGallerySaver { toast = it }
    val instagram = remember { instagramStoryIntent(context) }

    LaunchedEffect(toast) {
        if (toast != null) { delay(2000); toast = null }
    }

    suspend fun render(): Bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = HL.Paper,
        dragHandle = null
    ) {
        if (showSend && send != null) {
            SendSheet(attachment = send, onDismiss = { showSend = false }, onSent = { toast = "Sent" })
        }
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(top = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SHARE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    letterSpacing = 2.sp,
                    color = HL.Ink
                )
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .size(34.dp)
                        .brutalPlate(fill = HL.Card, borderWidth = 1.5.dp, offset = 2.dp, shadow = Color.Transparent)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Close, "Close", tint = HL.Ink, modifier = Modifier.size(15.dp))
                }
            }

            // Preview — the card is always laid out at its full canvas and only
            // *drawn* scaled, so `graphicsLayer.record` captures the full-resolution
            // card rather than the shrunken preview. The outer graphicsLayer sits
            // before drawWithContent in the chain, so the recorded layer is the
            // unscaled content.
            val canvas = canvasOf(format)
            BoxWithConstraints(
                Modifier.fillMaxWidth().height(360.dp).padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                val fit = minOf(maxWidth / canvas.width, maxHeight / canvas.height)
                Box(Modifier.fillMaxSize().clipToBounds(), contentAlignment = Alignment.Center) {
                    card(
                        format,
                        Modifier
                            .graphicsLayer { scaleX = fit; scaleY = fit }
                            .drawWithContent {
                                graphicsLayer.record { this@drawWithContent.drawContent() }
                                drawLayer(graphicsLayer)
                            }
                    )
                }
            }

            // Format tabs + card options
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShareFormat.entries.forEach { f ->
                        ShareChip(f.label, format == f, Modifier.weight(1f)) { format = f }
                    }
                }
                controls(format)
            }

            // Options
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (send != null) {
                    OptionTile(Icons.AutoMirrored.Outlined.Send, "SEND", Modifier.weight(1f)) { showSend = true }
                }
                if (instagram != null) {
                    OptionTile(Icons.Outlined.CameraAlt, "STORY", Modifier.weight(1f)) {
                        scope.launch {
                            val uri = cacheShareImage(context, render(), imageName)
                            if (uri != null) {
                                instagram.setDataAndType(uri, "image/png")
                                context.startActivity(instagram)
                                onDismiss()
                            } else toast = "Couldn’t prepare image"
                        }
                    }
                }
                OptionTile(Icons.Outlined.IosShare, "MORE", Modifier.weight(1f)) {
                    scope.launch {
                        val uri = cacheShareImage(context, render(), imageName)
                        if (uri != null) {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(Intent.EXTRA_TEXT, link)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(send, "Share"))
                        } else toast = "Couldn’t prepare image"
                    }
                }
                OptionTile(Icons.Outlined.Download, "SAVE", Modifier.weight(1f)) {
                    scope.launch { saveCard(render()) }
                }
                OptionTile(Icons.Outlined.Link, "COPY", Modifier.weight(1f)) {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("PaperBoxd", link))
                    toast = "Link copied"
                }
            }

            toast?.let {
                Text(
                    it,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = HL.Paper,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(HL.Ink.copy(alpha = 0.92f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

/**
 * Two-state option button in the sheet's brutalist chrome — separate rectangles
 * (not a fused segmented block), each with its own ink border so the selected
 * fill never swallows the outline.
 */
@Composable
fun ShareChip(
    label: String,
    on: Boolean,
    modifier: Modifier = Modifier,
    swatch: Color? = null,
    onClick: () -> Unit
) {
    Row(
        modifier
            .brutalPlate(fill = if (on) HL.Ink else HL.Card, borderWidth = 1.5.dp, offset = 2.dp, shadow = Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (swatch != null) {
            Box(Modifier.size(10.dp).background(swatch).border(1.dp, if (on) HL.Paper else HL.Ink))
        }
        Text(
            label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp,
            maxLines = 1,
            color = if (on) HL.Paper else HL.Ink
        )
    }
}

// ── The card ──────────────────────────────────────────────────────────────────

/**
 * The shareable book card — iOS BookShareCardView twin, port of the design
 * prototype's share2/cards.jsx set in the Monthly Wrapped print kit: one ground
 * colour, a running head, hairline rules, the cover as a hard-edged printed
 * board (typographic when there is no image), a rating as a tabular number,
 * and a footer of wordmark + handle + scan code.
 *
 * Composed at [bookShareCanvas] via requiredSize, so every number is the
 * prototype's own; the caller scales it for display.
 */
@Composable
fun ShareCard(
    title: String,
    author: String,
    year: String?,
    coverUrl: String?,
    handle: String?,
    rating: Int?,
    note: String?,
    statusLabel: String,
    ground: ShareGround,
    layout: ShareLayout,
    showRating: Boolean,
    showNote: Boolean,
    format: ShareFormat,
    modifier: Modifier = Modifier
) {
    val canvas = bookShareCanvas(format)
    val noteText = note?.trim()?.takeIf { it.isNotEmpty() }?.let { if (it.length > 600) it.take(599) + "…" else it }
    val ratingValue = rating?.takeIf { showRating && it > 0 }?.let { min(it, 5) }
    // Quote needs a line to set; without one the card falls back to cover.
    val activeLayout = if (layout == ShareLayout.Quote && noteText == null) ShareLayout.Cover else layout
    val yearText = year?.takeIf { it.isNotEmpty() }
    val meta = when {
        yearText == null -> author
        author.isEmpty() -> yearText
        else -> "$author · $yearText"
    }
    val k = CardKit(ground, title, author, coverUrl?.replace("http://", "https://")?.takeIf { it.isNotEmpty() })

    Box(
        modifier
            .requiredSize(canvas.width, canvas.height)
            .background(ground.bg)
            .padding(if (format == ShareFormat.Story) 34.dp else 40.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            with(k) {
                when {
                    format == ShareFormat.Square && activeLayout == ShareLayout.Quote ->
                        SquareQuote(meta, ratingValue, noteText.orEmpty(), handle, statusLabel)
                    format == ShareFormat.Square -> Square(meta, ratingValue, noteText.takeIf { showNote }, handle, statusLabel)
                    activeLayout == ShareLayout.Quote -> StoryQuote(meta, ratingValue, noteText.orEmpty(), handle, statusLabel)
                    activeLayout == ShareLayout.Minimal -> StoryMinimal(meta, ratingValue, handle, statusLabel)
                    else -> StoryCover(yearText, ratingValue, noteText.takeIf { showNote }, handle, statusLabel)
                }
            }
        }
    }
}

/** Card pieces bound to one ground + title. */
private class CardKit(val g: ShareGround, val title: String, val author: String, val coverUrl: String?) {

    /** A thought shared on its own has no book; the board row then stays out. */
    val hasBook = title.isNotEmpty()

    /** Long titles and long words step the display type down so nothing clips. */
    val titleScale: Float = run {
        val n = title.length
        val longest = title.split(' ').maxOfOrNull { it.length } ?: n
        val byLength = when {
            n > 40 -> 0.62f
            n > 26 -> 0.76f
            n > 16 -> 0.88f
            else -> 1f
        }
        max(0.5f, min(byLength, 11f / max(longest, 1)))
    }

    // ── Story · cover
    @Composable
    fun ColumnScope.StoryCover(year: String?, rating: Int?, note: String?, handle: String?, status: String) {
        Head(30f, 9.5f, status)
        Rule(Modifier.padding(top = 18.dp))
        Box(Modifier.weight(1f).fillMaxWidth().padding(start = 10.dp), contentAlignment = Alignment.CenterStart) {
            Board(200.dp)
        }
        Rule()
        Row(Modifier.padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            Column(Modifier.weight(1f)) {
                Title(42f, 3)
                Mono(author, 10f, Modifier.padding(top = 12.dp))
                if (year != null) Mono("First published $year", 10f, Modifier.padding(top = 6.dp))
            }
            if (rating != null) Rate(rating, 38f)
        }
        if (note != null) BodyNote(note, 3, Modifier.padding(top = 20.dp))
        Foot(22f, 9.5f, 26.dp, handle, Modifier.padding(top = 26.dp))
    }

    // ── Story · quote
    @Composable
    fun ColumnScope.StoryQuote(meta: String, rating: Int?, note: String, handle: String?, status: String) {
        val quoteSize = quoteSize(note)
        Head(30f, 9.5f, status)
        Rule(Modifier.padding(top = 18.dp))
        Column(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            Box(Modifier.padding(bottom = 26.dp).size(46.dp, 4.dp).background(g.accent))
            Text(
                note,
                fontFamily = PBW.Display, fontWeight = FontWeight.Normal,
                fontSize = quoteSize.sp, lineHeight = (quoteSize * 1.34f).sp,
                color = g.fg, maxLines = 9, overflow = TextOverflow.Ellipsis
            )
            if (rating != null) Rate(rating, 40f, Modifier.padding(top = 30.dp))
        }
        Rule()
        if (hasBook) {
            Row(
                Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Board(78.dp)
                Column(Modifier.weight(1f).padding(bottom = 2.dp)) {
                    Title(26f, 2)
                    Mono(meta, 9.5f, Modifier.padding(top = 10.dp))
                }
            }
        }
        Foot(22f, 9.5f, 26.dp, handle, Modifier.padding(top = 26.dp))
    }

    // ── Square · quote
    @Composable
    fun ColumnScope.SquareQuote(meta: String, rating: Int?, note: String, handle: String?, status: String) {
        val quoteSize = quoteSize(note) * 0.86f
        Head(28f, 10f, status)
        Rule(Modifier.padding(top = 16.dp))
        Column(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            Box(Modifier.padding(bottom = 20.dp).size(46.dp, 4.dp).background(g.accent))
            Text(
                note,
                fontFamily = PBW.Display, fontWeight = FontWeight.Normal,
                fontSize = quoteSize.sp, lineHeight = (quoteSize * 1.34f).sp,
                color = g.fg, maxLines = 9, overflow = TextOverflow.Ellipsis
            )
            if (rating != null) Rate(rating, 34f, Modifier.padding(top = 22.dp))
        }
        Rule()
        if (hasBook) {
            Row(
                Modifier.padding(top = 18.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Board(60.dp)
                Column(Modifier.weight(1f).padding(bottom = 2.dp)) {
                    Title(22f, 2)
                    Mono(meta, 9.5f, Modifier.padding(top = 8.dp))
                }
            }
        }
        Foot(24f, 10f, 24.dp, handle, Modifier.padding(top = 22.dp))
    }

    /** Long lines step the display type down so the card never clips. */
    private fun quoteSize(note: String): Float = when {
        note.length > 420 -> 15f
        note.length > 300 -> 17f
        note.length > 220 -> 19f
        note.length > 140 -> 22f
        note.length > 80 -> 26f
        else -> 31f
    }

    // ── Story · minimal
    @Composable
    fun ColumnScope.StoryMinimal(meta: String, rating: Int?, handle: String?, status: String) {
        Head(30f, 9.5f, status)
        Rule(Modifier.padding(top = 18.dp))
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Board(222.dp) }
        Rule()
        Row(
            Modifier.padding(top = 20.dp, bottom = 26.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Title(40f, 3)
                Mono(meta, 10f, Modifier.padding(top = 12.dp))
            }
            if (rating != null) Rate(rating, 36f)
        }
        Foot(22f, 9.5f, 26.dp, handle)
    }

    // ── Square
    @Composable
    fun ColumnScope.Square(meta: String, rating: Int?, note: String?, handle: String?, status: String) {
        Head(28f, 10f, status)
        Rule(Modifier.padding(top = 16.dp))
        Row(
            Modifier.weight(1f).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(34.dp)
        ) {
            Board(168.dp)
            Column(Modifier.weight(1f)) {
                Title(36f, 3)
                Mono(meta, 10f, Modifier.padding(top = 12.dp))
                if (rating != null) Rate(rating, 34f, Modifier.padding(top = 20.dp))
                if (note != null) BodyNote(note, 4, Modifier.padding(top = 20.dp))
            }
        }
        Foot(24f, 10f, 24.dp, handle)
    }

    // ── Pieces

    @Composable
    fun Rule(modifier: Modifier = Modifier) {
        Box(modifier.fillMaxWidth().height(1.dp).background(g.line))
    }

    @Composable
    fun Head(brand: Float, mono: Float, status: String) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Brand(brand)
            Spacer(Modifier.weight(1f))
            if (status.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(7.dp).background(g.accent))
                    Text(
                        status.uppercase(),
                        fontFamily = PBW.Mono, fontSize = mono.sp, letterSpacing = 0.16.em,
                        color = g.fg, maxLines = 1
                    )
                }
            }
        }
    }

    @Composable
    fun Foot(brand: Float, mono: Float, scan: Dp, handle: String?, modifier: Modifier = Modifier) {
        Column(modifier.fillMaxWidth()) {
            Rule()
            Row(
                Modifier.padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Brand(brand)
                if (!handle.isNullOrEmpty()) Mono(handle, mono, maxLines = 1)
                Spacer(Modifier.weight(1f))
                ScanCode(scan)
            }
        }
    }

    @Composable
    fun Brand(size: Float) {
        Text("PaperBoxd", fontFamily = PBW.Script, fontSize = size.sp, lineHeight = (size * 0.88f).sp, color = g.fg)
    }

    @Composable
    fun Title(size: Float, lines: Int) {
        val s = size * titleScale
        Text(
            title,
            fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
            fontSize = s.sp, lineHeight = (s * 1.02f).sp, letterSpacing = (-0.025).em,
            color = g.fg, maxLines = lines, overflow = TextOverflow.Ellipsis
        )
    }

    @Composable
    fun Mono(text: String, size: Float, modifier: Modifier = Modifier, maxLines: Int = 2) {
        Text(
            text.uppercase(),
            fontFamily = PBW.Mono, fontSize = size.sp, letterSpacing = 0.14.em,
            color = g.dim, maxLines = maxLines, overflow = TextOverflow.Ellipsis, modifier = modifier
        )
    }

    @Composable
    fun BodyNote(text: String, lines: Int, modifier: Modifier = Modifier) {
        Column(modifier.fillMaxWidth()) {
            Rule()
            Text(
                "“$text”",
                fontFamily = PBW.Sans, fontSize = 15.5.sp, lineHeight = (15.5f * 1.5f).sp,
                color = g.fg, maxLines = lines, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }

    /** A rating is a tabular number, never five shapes. */
    @Composable
    fun Rate(r: Int, size: Float, modifier: Modifier = Modifier) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                "$r.0",
                fontFamily = PBW.Sans, fontWeight = FontWeight.Bold,
                fontSize = size.sp, lineHeight = (size * 0.9f).sp, letterSpacing = (-0.03).em,
                color = g.accent, modifier = Modifier.alignByBaseline()
            )
            Text(
                "/ 5",
                fontFamily = PBW.Mono, fontSize = (size * 0.3f).sp, letterSpacing = 0.1.em,
                color = g.dim, modifier = Modifier.alignByBaseline()
            )
        }
    }

    /**
     * The cover as a printed board: the real cover, hard edges, an offset solid
     * shadow. Without an image (or while it loads) it is a uniform typographic
     * board — accent rule on the spine, display title, mono author.
     */
    @Composable
    fun Board(w: Dp) {
        val shadow = (w.value * 0.045f).roundToInt().dp
        val shadowColor = Color(0xFF120E0B).copy(alpha = 0.32f)
        Box(
            Modifier
                .size(w, w * 1.5f)
                .drawBehind {
                    val s = shadow.toPx()
                    drawRect(shadowColor, topLeft = Offset(s, s), size = size)
                }
                .clipToBounds()
        ) {
            if (coverUrl != null) {
                SubcomposeAsyncImage(
                    model = coverUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    loading = { TypographicBoard(w) },
                    error = { TypographicBoard(w) },
                    modifier = Modifier.fillMaxSize()
                )
            } else TypographicBoard(w)
        }
    }

    @Composable
    fun TypographicBoard(w: Dp) {
        val t = w.value * 0.132f * titleScale
        val a = max(6.5f, w.value * 0.052f)
        Box(Modifier.fillMaxSize().background(g.board)) {
            Box(Modifier.fillMaxSize().padding(w * 0.085f).padding(start = w * 0.05f), contentAlignment = Alignment.BottomStart) {
                Column {
                    Text(
                        title,
                        fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                        fontSize = t.sp, lineHeight = (t * 1.04f).sp, letterSpacing = (-0.02).em,
                        color = g.boardFg, maxLines = 4, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        author.uppercase(),
                        fontFamily = PBW.Mono, fontSize = a.sp, letterSpacing = 0.12.em,
                        color = g.boardDim, maxLines = 2, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = w * 0.055f)
                    )
                }
            }
            Box(Modifier.fillMaxHeight().width(max(3f, w.value * 0.05f).dp).background(g.boardBar))
        }
    }

    /** Deterministic scan-code bar heights (0…1), set in solid bars. */
    @Composable
    fun ScanCode(h: Dp) {
        Row(
            Modifier.height(h),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.5.dp)
        ) {
            SCAN_SEQ.forEach { v ->
                Box(Modifier.width(3.dp).height(max(4f, (v * h.value).roundToInt().toFloat()).dp).background(g.fg))
            }
        }
    }

    companion object {
        private val SCAN_SEQ = floatArrayOf(
            0.5f, 0.95f, 0.35f, 0.7f, 1f, 0.45f, 0.8f, 0.6f, 0.3f, 0.9f, 0.5f, 0.75f, 0.4f,
            1f, 0.55f, 0.85f, 0.35f, 0.65f, 0.95f, 0.45f, 0.7f, 0.55f, 0.9f, 0.4f, 0.8f, 0.6f
        )
    }
}

@Composable
private fun OptionTile(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .brutalPlate(fill = HL.Card, borderWidth = 1.5.dp, offset = 3.dp, shadow = Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = HL.Ink, modifier = Modifier.size(20.dp))
        Text(
            label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            color = HL.Muted
        )
    }
}
