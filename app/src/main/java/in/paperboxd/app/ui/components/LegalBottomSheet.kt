package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background

/** A bundled legal document, read from assets/legal at runtime. */
enum class LegalDoc(val asset: String, val title: String) {
    Privacy("legal/privacy-policy.md", "Privacy Policy"),
    Terms("legal/terms-of-service.md", "Terms of Service"),
}

private fun lw(a: Float) = Color.White.copy(alpha = a)

/**
 * Full legal document in a modal bottom sheet at signup. Content is bundled in
 * assets (works offline). Tables render as plain monospaced rows — the
 * bundled-native path has no table layout engine.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalBottomSheet(doc: LegalDoc, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val body = remember(doc) {
        val raw = context.assets.open(doc.asset).bufferedReader().use { it.readText() }
        sanitizeLegal(raw)
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Background) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
        ) {
            Text(doc.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.height(12.dp))
            // Mirrors the banner on paperboxd.in/privacy. No effective date is
            // set yet, so the text must not read as binding.
            Text(
                "Draft — not yet in effect. This document is under review and is not " +
                    "legally binding until an effective date is published.",
                color = Accent,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Accent.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
            )
            Spacer(Modifier.height(16.dp))
            body.split("\n").forEach { LegalLine(it) }
        }
    }
}

/** Drop the reviewer preamble (before the first `---`) and the internal
 *  "Outstanding placeholders" checklist (from that heading on). */
private fun sanitizeLegal(raw: String): String {
    var body = raw.substringBefore("\n## Outstanding placeholders")
    val marker = "\n---\n"
    val i = body.indexOf(marker)
    if (i != -1) body = body.substring(i + marker.length)
    return body.trimEnd().removeSuffix("---").trim()
}

@Composable
private fun LegalLine(raw: String) {
    val line = raw.trim()
    when {
        line.isEmpty() -> Spacer(Modifier.height(10.dp))
        line == "---" -> {
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(lw(0.12f)))
            Spacer(Modifier.height(10.dp))
        }
        line.startsWith("### ") -> Header(clean(line.removePrefix("### ")), 15.sp, top = 12)
        line.startsWith("## ") -> Header(clean(line.removePrefix("## ")), 17.sp, top = 18)
        line.startsWith("# ") -> Header(clean(line.removePrefix("# ")), 22.sp, top = 6)
        line.startsWith("|") ->
            if (line.all { it == '|' || it == '-' || it == ':' || it == ' ' }) Unit
            else Text(
                line,
                color = lw(0.7f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 1.dp),
            )
        line.startsWith("- ") || line.startsWith("* ") -> Row(Modifier.padding(vertical = 2.dp)) {
            Text("•  ", color = lw(0.5f), fontSize = 14.sp)
            Text(clean(line.substring(2)), color = lw(0.82f), fontSize = 14.sp)
        }
        else -> Text(clean(line), color = lw(0.82f), fontSize = 14.sp, modifier = Modifier.padding(vertical = 3.dp))
    }
}

@Composable
private fun Header(text: String, size: TextUnit, top: Int) {
    Text(
        text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = size,
        modifier = Modifier.padding(top = top.dp, bottom = 4.dp),
    )
}

/** Strip inline markdown the line renderer doesn't handle: `**bold**` and `[label](url)` → `label`. */
private fun clean(s: String): String {
    val noBold = s.replace("**", "")
    return Regex("""\[([^\]]+)\]\([^)]+\)""").replace(noBold) { it.groupValues[1] }
}
