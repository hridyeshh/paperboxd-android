package `in`.paperboxd.app.ui.screens.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.ui.components.HL

/**
 * Six visual character boxes over one invisible BasicTextField, so focus,
 * paste, and auto-advance come for free. Auto-submit fires in the ViewModel
 * when the sixth digit lands. Paper field wells with ink borders, with a
 * spring pop per box when its digit lands (mirrors iOS's
 * `.contentTransition(.numericText())`).
 */
@Composable
fun OtpBoxes(code: String, onCodeChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    BasicTextField(
        value = code,
        onValueChange = onCodeChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.focusRequester(focusRequester),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(6) { index ->
                    val char = code.getOrNull(index)?.toString() ?: ""
                    val isActive = index == code.length
                    OtpDigitBox(char = char, isActive = isActive)
                }
            }
        }
    )
}

@Composable
private fun OtpDigitBox(char: String, isActive: Boolean) {
    val borderColor by animateColorAsState(
        targetValue = if (isActive) HL.Ink else HL.Ink.copy(alpha = 0.30f),
        animationSpec = tween(150),
        label = "otpBorder",
    )
    val pop = remember { Animatable(1f) }
    LaunchedEffect(char) {
        if (char.isNotEmpty()) {
            pop.snapTo(1.3f)
            pop.animateTo(1f, animationSpec = spring(dampingRatio = 0.5f, stiffness = 380f))
        }
    }
    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 56.dp)
            .background(HL.Field)
            .border(width = 2.dp, color = borderColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            color = HL.Ink,
            modifier = Modifier.scale(pop.value),
        )
    }
}
