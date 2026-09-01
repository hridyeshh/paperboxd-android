package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import `in`.paperboxd.app.R
import `in`.paperboxd.app.ui.theme.PBSans
import `in`.paperboxd.app.ui.theme.PBScript

// Auth surfaces use the shared light kit (HL) — same tokens as home and the
// book page, so the paper ground runs unbroken across the app.

/**
 * Layered dark wash — radial vignette + top-to-bottom linear darkening — that
 * sits over [BookCoverColumns] to keep foreground text legible. Non-interactive.
 */
@Composable
fun DarkWash(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(
                Brush.radialGradient(
                    colors = listOf(HL.Muted.copy(0.7f), HL.Muted, HL.Ink),
                )
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(HL.Muted.copy(0.7f), HL.Muted),
                )
            )
    )
}

/** The PaperBoxd script wordmark. */
@Composable
fun Wordmark(fontSize: Int, modifier: Modifier = Modifier, color: Color = HL.Ink) {
    Text(
        text = "PaperBoxd",
        fontFamily = PBScript,
        fontSize = fontSize.sp,
        color = color,
        maxLines = 1,
        modifier = modifier,
    )
}

/** Mono uppercase eyebrow / field label. */
@Composable
fun MonoLabel(text: String, modifier: Modifier = Modifier, color: Color = HL.Muted) {
    Text(
        text = text.uppercase(),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        color = color,
        modifier = modifier,
    )
}

/**
 * Brutalist card backing: opaque card-face panel, heavy ink 2px border,
 * hard 90° corners, and an offset "echo" frame standing in for a drop shadow.
 * Mirrors the iOS `BrutalCard`. [content] is laid out over the panel; give it
 * its own inner padding.
 */
@Composable
fun BrutalCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        // Offset echo — hard, blur-free brutalist shadow, sits down-right.
        Box(
            Modifier
                .matchParentSize()
                .padding(start = 6.dp, top = 6.dp)
                .border(2.dp, HL.Ink.copy(0.25f), RectangleShape)
        )
        // Opaque panel + structural border.
        Box(
            Modifier
                .matchParentSize()
                .padding(end = 6.dp, bottom = 6.dp)
                .background(HL.Card, RectangleShape)
                .border(2.dp, HL.Ink, RectangleShape)
        )
        Box(Modifier.padding(end = 6.dp, bottom = 6.dp)) { content() }
    }
}

/** Flat brutalist text field: paper2 fill, hard ink border, no rounded corners. */
@Composable
fun BrutalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = HL.Ink, fontFamily = PBSans, fontSize = 16.sp),
        cursorBrush = SolidColor(HL.Ink),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(if (focused) HL.Paper2 else HL.Paper2.copy(alpha = 0.55f), RectangleShape)
            .border(1.dp, if (focused) HL.Ink else HL.Ink.copy(alpha = 0.28f), RectangleShape)
            .onFocusChanged { focused = it.isFocused },
        decorationBox = { inner ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp),
            ) {
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = HL.Muted.copy(0.7f), fontFamily = PBSans, fontSize = 16.sp)
                    }
                    inner()
                }
                trailing?.invoke()
            }
        },
    )
}

/** Password variant of [BrutalTextField] with a show/hide eye toggle. */
@Composable
fun BrutalSecureField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var visible by remember { mutableStateOf(false) }
    BrutalTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailing = {
            Text(
                text = if (visible) "hide" else "show",
                color = HL.Muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickableNoRipple { visible = !visible },
            )
        },
    )
}

/**
 * Brutalist primary CTA: ink fill, ink 2px border, hard offset "shadow" behind,
 * paper label + arrow. Presses scale down slightly. Mirrors the iOS sign-in
 * button — inverted against the paper ground so it still reads as the one solid
 * mass on the card.
 */
@Composable
fun BrutalPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Box(modifier.fillMaxWidth().height(56.dp)) {
        // Hard white offset shadow.
        Box(
            Modifier
                .matchParentSize()
                .padding(start = 4.dp, top = 4.dp)
                .background(HL.Ink.copy(0.25f), RectangleShape)
        )
        Box(
            Modifier
                .matchParentSize()
                .padding(end = 4.dp, bottom = 4.dp)
                .scale(if (pressed) 0.98f else 1f)
                .alpha(if (enabled) 1f else 0.45f)
                .background(HL.Ink, RectangleShape)
                .border(2.dp, HL.Ink, RectangleShape)
                .clickableNoRipple(enabled = enabled && !loading, interactionSource = interaction, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (loading) {
                CircularProgressIndicator(color = HL.Paper, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text, color = HL.Paper, fontFamily = PBSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("→", color = HL.Paper, fontFamily = PBSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }
        }
    }
}

/** Outline "Continue with Google" button with the four-colour G. */
@Composable
fun GoogleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(HL.Paper2, RectangleShape)
            .border(1.5.dp, HL.Ink, RectangleShape)
            .clickableNoRipple(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.google_logo),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(text, color = HL.Ink, fontFamily = PBSans, fontWeight = FontWeight.SemiBold, fontSize = 14.5f.sp)
    }
}

/** "── OR ──" mono divider. */
@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(HL.Ink.copy(0.18f)))
        Text(
            "OR",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            letterSpacing = 2.2.sp,
            color = HL.Muted.copy(0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp),
        )
        Box(Modifier.weight(1f).height(1.dp).background(HL.Ink.copy(0.18f)))
    }
}

// --- small modifier helpers ---

private fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit,
): Modifier = this.then(
    Modifier.clickable(
        interactionSource = interactionSource ?: MutableInteractionSource(),
        indication = null,
        enabled = enabled,
        onClick = onClick,
    )
)
