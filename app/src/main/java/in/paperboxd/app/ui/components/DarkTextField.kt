package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Border
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary

/** Dark-styled text field matching the iOS DarkFields look. */
@Composable
fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        singleLine = singleLine,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = darkFieldColors(),
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

/** Password field with show/hide toggle. */
@Composable
fun DarkPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = darkFieldColors(),
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
private fun darkFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Surface,
    unfocusedContainerColor = Surface,
    focusedBorderColor = Accent.copy(alpha = 0.6f),
    unfocusedBorderColor = Border,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = Accent,
    focusedLabelColor = TextSecondary,
    unfocusedLabelColor = TextSecondary
)
