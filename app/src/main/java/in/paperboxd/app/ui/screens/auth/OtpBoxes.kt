package `in`.paperboxd.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Border
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary

/**
 * Six visual character boxes over one invisible BasicTextField, so focus,
 * paste, and auto-advance come for free. Auto-submit fires in the ViewModel
 * when the sixth digit lands.
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(6) { index ->
                    val char = code.getOrNull(index)?.toString() ?: ""
                    val isActive = index == code.length
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface)
                            .border(
                                width = if (isActive) 1.5.dp else 1.dp,
                                color = if (isActive) Accent else Border,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    )
}
