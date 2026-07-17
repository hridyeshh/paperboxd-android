package `in`.paperboxd.app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.repository.AuthRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.screens.onboarding.readAndDownscale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private val GENDER_OPTIONS = listOf(
    "Female", "Male", "Non-binary", "Transgender", "Intersex", "Prefer not to say", "Custom"
)
private val PRONOUN_OPTIONS = listOf("He", "Him", "His", "She", "Her", "They", "Them", "Theirs")
private val USERNAME_RE = Regex("^[a-z0-9_]{3,30}$")

data class EditProfileState(
    val loaded: Boolean = false,
    val username: String = "",
    val originalUsername: String = "",
    val name: String = "",
    val bio: String = "",
    val pronouns: List<String> = emptyList(),
    val gender: String = "",
    val link: String = "",
    val birthday: String? = null, // yyyy-MM-dd
    val avatarUrl: String? = null,
    val pickedAvatar: Uri? = null,
    val usernameAvailable: Boolean? = null,
    val checkingUsername: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val api: ApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    private var usernameJob: Job? = null

    fun load(username: String) {
        if (_state.value.loaded || username.isBlank()) return
        viewModelScope.launch {
            userRepository.profile(username).onSuccess { p ->
                _state.update {
                    it.copy(
                        loaded = true,
                        username = p.username,
                        originalUsername = p.username,
                        name = p.name,
                        bio = p.bio.orEmpty(),
                        // Normalize stored pronouns to canonical chip labels so they highlight.
                        pronouns = p.pronouns.map { pr ->
                            PRONOUN_OPTIONS.firstOrNull { it.equals(pr, ignoreCase = true) } ?: pr
                        },
                        gender = p.gender.orEmpty(),
                        link = p.links.firstOrNull().orEmpty(),
                        birthday = p.birthday,
                        avatarUrl = p.avatarUrl,
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(loaded = true, error = e.message ?: "Couldn't load profile") }
            }
        }
    }

    fun onName(v: String) = _state.update { it.copy(name = v) }
    fun onBio(v: String) = _state.update { it.copy(bio = v) }
    fun onLink(v: String) = _state.update { it.copy(link = v) }
    fun onGender(v: String) = _state.update { it.copy(gender = v) }
    fun setBirthday(v: String?) = _state.update { it.copy(birthday = v) }

    fun togglePronoun(opt: String) = _state.update { s ->
        when {
            s.pronouns.contains(opt) -> s.copy(pronouns = s.pronouns - opt)
            s.pronouns.size < 2 -> s.copy(pronouns = s.pronouns + opt)
            else -> s
        }
    }

    fun onUsername(raw: String) {
        val candidate = raw.lowercase().trim()
        _state.update { it.copy(username = candidate) }
        usernameJob?.cancel()
        if (candidate == _state.value.originalUsername.lowercase()) {
            _state.update { it.copy(usernameAvailable = null, checkingUsername = false) }
            return
        }
        if (!USERNAME_RE.matches(candidate)) {
            _state.update { it.copy(usernameAvailable = false, checkingUsername = false) }
            return
        }
        usernameJob = viewModelScope.launch {
            _state.update { it.copy(checkingUsername = true) }
            delay(500)
            val available = runCatching { api.checkUsername(candidate).available }.getOrNull()
            _state.update { it.copy(usernameAvailable = available, checkingUsername = false) }
        }
    }

    fun uploadAvatar(bytes: ByteArray, preview: Uri) {
        _state.update { it.copy(pickedAvatar = preview) }
        viewModelScope.launch {
            authRepository.uploadAvatar(bytes).onSuccess { resp ->
                _state.update { it.copy(avatarUrl = resp.avatarUrl) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Couldn't upload photo") }
            }
        }
    }

    /** PUT /users/{originalUsername}; invokes [onDone] on success. Mirrors iOS save(). */
    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (s.isSaving) return
        val candidate = s.username.lowercase().trim()
        val usernameChanged = candidate != s.originalUsername.lowercase()
        if (usernameChanged && s.usernameAvailable == false) {
            _state.update { it.copy(error = "That username is taken or invalid.") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val link = s.link.trim()
            val gender = s.gender.trim()
            val fields = mapOf(
                "username" to if (usernameChanged) candidate else null,
                "name" to s.name.trim(),
                "bio" to s.bio.trim(),
                "pronouns" to s.pronouns,
                "birthday" to s.birthday,
                "gender" to gender.ifEmpty { null },
                "links" to if (link.isEmpty()) null else listOf(link),
            )
            userRepository.updateProfile(s.originalUsername, fields).onSuccess {
                _state.update { it.copy(isSaving = false) }
                onDone()
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message ?: "Couldn't save profile") }
            }
        }
    }
}

@Composable
fun EditProfileScreen(
    username: String,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(username) { viewModel.load(username) }

    val pickAvatar = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) scope.launch {
            val bytes = readAndDownscale(context, uri)
            if (bytes != null) viewModel.uploadAvatar(bytes, uri)
        }
    }

    Column(
        Modifier.fillMaxSize().background(HL.Paper).statusBarsPadding()
    ) {
        // Top bar: Cancel · title · Save
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onClose) { Text("Cancel", color = HL.Muted, fontSize = 15.sp) }
            Text(
                "Edit profile",
                color = HL.Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
            )
            if (state.isSaving) {
                Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(18.dp), color = HL.Accent, strokeWidth = 2.dp)
                }
            } else {
                TextButton(onClick = { viewModel.save(onSaved) }) {
                    Text("Save", color = HL.Accent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            AvatarHeader(
                model = state.pickedAvatar ?: state.avatarUrl,
                name = state.name.trim().ifEmpty { state.username },
                username = state.username,
                onPick = {
                    pickAvatar.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Section("Identity") {
                StackedRow("Username") { UsernameControl(state, viewModel::onUsername) }
                RowDivider()
                StackedRow("Name") { PlainField(state.name, "Your name", viewModel::onName) }
                RowDivider()
                StackedRow("Bio") { BioControl(state.bio, viewModel::onBio) }
            }

            Section("Personal") {
                BirthdayRow(state.birthday, viewModel::setBirthday)
                RowDivider()
                StackedRow("Pronouns") { PronounChips(state.pronouns, viewModel::togglePronoun) }
                RowDivider()
                StackedRow("Gender") { GenderControl(state.gender, viewModel::onGender) }
            }

            Section("Online") {
                StackedRow("Link") { PlainField(state.link, "https://…", viewModel::onLink) }
            }

            state.error?.let {
                Text(it, color = HL.Accent, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 4.dp))
            }
        }
    }
}

// ── Avatar header ────────────────────────────────────────────────────────────

private val avatarGradient = Brush.linearGradient(listOf(Color(0xFFF2D19E), Color(0xFF7A52B8)))

@Composable
private fun AvatarHeader(model: Any?, name: String, username: String, onPick: () -> Unit) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                Modifier.size(104.dp).clip(CircleShape).background(avatarGradient)
                    .border(1.dp, HL.Ink.copy(alpha = 0.15f), CircleShape)
                    .clickable { onPick() },
                contentAlignment = Alignment.Center
            ) {
                if (model != null) {
                    AsyncImage(
                        model = model, contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                    )
                }
            }
            Box(
                Modifier.size(30.dp).clip(CircleShape).background(HL.Ink)
                    .border(2.5.dp, HL.Paper, CircleShape).clickable { onPick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.PhotoCamera, "Change photo", tint = HL.Paper, modifier = Modifier.size(14.dp))
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(name, color = HL.Ink, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("@$username", color = HL.Muted, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        }
    }
}

// ── Section scaffolding ──────────────────────────────────────────────────────

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            title.uppercase(), color = HL.Muted.copy(alpha = 0.75f),
            fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, letterSpacing = 2.sp,
            modifier = Modifier.padding(start = 6.dp)
        )
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(HL.Card)
                .border(1.dp, HL.Ink.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
        ) { content() }
    }
}

@Composable
private fun RowDivider() {
    Box(Modifier.fillMaxWidth().padding(start = 16.dp).height(1.dp).background(HL.Ink.copy(alpha = 0.08f)))
}

@Composable
private fun StackedRow(label: String, control: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(
            label.uppercase(), color = HL.Muted,
            fontFamily = FontFamily.Monospace, fontSize = 9.5.sp, letterSpacing = 1.5.sp
        )
        control()
    }
}

// ── Controls ─────────────────────────────────────────────────────────────────

@Composable
private fun PlainField(value: String, placeholder: String, onChange: (String) -> Unit) {
    BasicTextField(
        value = value, onValueChange = onChange, singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp, color = HL.Ink),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) Text(placeholder, color = HL.Muted, fontSize = 16.sp)
                inner()
            }
        }
    )
}

@Composable
private fun UsernameControl(state: EditProfileState, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f)) { PlainField(state.username, "Unique handle", onChange) }
        when {
            state.checkingUsername -> CircularProgressIndicator(Modifier.size(16.dp), color = HL.Muted, strokeWidth = 2.dp)
            state.usernameAvailable == true -> Icon(Icons.Filled.Check, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
            state.usernameAvailable == false -> Icon(Icons.Filled.Close, null, tint = HL.Accent, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun BioControl(value: String, onChange: (String) -> Unit) {
    val over = value.length > 160
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        BasicTextField(
            value = value, onValueChange = onChange,
            textStyle = TextStyle(fontSize = 16.sp, color = HL.Ink, fontFamily = FontFamily.Serif),
            modifier = Modifier.fillMaxWidth().heightIn(min = 78.dp),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text("Say something about yourself", color = HL.Muted, fontSize = 16.sp, fontFamily = FontFamily.Serif)
                    inner()
                }
            }
        )
        Text(
            "${value.length}/160",
            color = if (over) HL.Accent else HL.Muted.copy(alpha = 0.6f),
            fontFamily = FontFamily.Monospace, fontSize = 9.5.sp
        )
    }
}

@Composable
private fun BirthdayRow(birthday: String?, onSet: (String?) -> Unit) {
    val context = LocalContext.current
    fun openPicker() {
        val cal = Calendar.getInstance()
        birthday?.split("-")?.takeIf { it.size == 3 }?.let { (y, m, d) ->
            runCatching { cal.set(y.toInt(), m.toInt() - 1, d.toInt()) }
        }
        android.app.DatePickerDialog(
            context,
            { _, y, m, d -> onSet("%04d-%02d-%02d".format(y, m + 1, d)) },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
    }
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("BIRTHDAY", color = HL.Muted, fontFamily = FontFamily.Monospace, fontSize = 9.5.sp, letterSpacing = 1.5.sp)
        Spacer(Modifier.weight(1f))
        if (birthday != null) {
            Text(birthday, color = HL.Ink, fontSize = 15.sp, modifier = Modifier.clickable { openPicker() })
            Icon(
                Icons.Filled.Close, "Clear birthday", tint = HL.Muted.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 8.dp).size(16.dp).clickable { onSet(null) }
            )
        } else {
            Text("Add", color = HL.Accent, fontWeight = FontWeight.Medium, fontSize = 15.sp,
                modifier = Modifier.clickable { openPicker() })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PronounChips(selected: List<String>, onToggle: (String) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PRONOUN_OPTIONS.forEach { opt ->
            val on = selected.contains(opt)
            val atMax = selected.size >= 2 && !on
            Box(
                Modifier.clip(CircleShape)
                    .background(if (on) HL.Ink else HL.Paper)
                    .border(1.dp, if (on) HL.Ink else HL.Ink.copy(alpha = 0.2f), CircleShape)
                    .clickable(enabled = !atMax) { onToggle(opt) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .then(if (atMax) Modifier.alpha(0.35f) else Modifier)
            ) {
                Text(
                    opt, color = if (on) HL.Paper else HL.Ink, fontSize = 13.sp, fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GenderControl(gender: String, onChange: (String) -> Unit) {
    val isPreset = GENDER_OPTIONS.dropLast(1).contains(gender)
    var custom by remember { mutableStateOf(gender.isNotEmpty() && !isPreset) }
    var expanded by remember { mutableStateOf(false) }
    val label = when {
        custom -> gender.ifEmpty { "Custom" }
        gender.isEmpty() -> "Select gender"
        else -> gender
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box {
            Row(
                Modifier.fillMaxWidth().clickable { expanded = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = if (gender.isEmpty() && !custom) HL.Muted else HL.Ink, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text("⌄", color = HL.Muted, fontSize = 16.sp)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                GENDER_OPTIONS.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            expanded = false
                            if (opt == "Custom") {
                                custom = true
                                if (GENDER_OPTIONS.contains(gender)) onChange("")
                            } else {
                                custom = false
                                onChange(opt)
                            }
                        }
                    )
                }
            }
        }
        if (custom) {
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(HL.Paper)
                    .border(1.dp, HL.Ink.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                PlainField(gender, "Enter gender", onChange)
            }
        }
    }
}
