package `in`.paperboxd.app.ui.screens.onboarding

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.Genre
import `in`.paperboxd.app.domain.model.ReadingTempo
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.DarkTextField
import `in`.paperboxd.app.ui.components.GhostButton
import `in`.paperboxd.app.ui.components.PrimaryButton
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Border
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/** Multi-step onboarding: username → genres → tempo → aha loading → aha reveal. */
@Composable
fun OnboardingScreen(
    user: User,
    onUsernameSet: (String) -> Unit,
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.initialUser = user
        viewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.UsernameSet -> onUsernameSet(event.username)
                is OnboardingEvent.Finished -> onFinished()
            }
        }
    }

    val pickAvatar = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val bytes = readAndDownscale(context, uri)
                if (bytes != null) viewModel.uploadAvatar(bytes)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp)
    ) {
        AnimatedContent(targetState = state.step, label = "onboardingStep") { step ->
            Column {
                when (step) {
                    OnboardingStep.Username -> UsernameStep(
                        state = state,
                        viewModel = viewModel,
                        onPickAvatar = {
                            pickAvatar.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                    OnboardingStep.Genres -> GenresStep(state, viewModel)
                    OnboardingStep.Tempo -> TempoStep(state, viewModel)
                    OnboardingStep.AhaLoading -> AhaLoadingStep()
                    OnboardingStep.AhaReveal -> AhaRevealStep(state, viewModel)
                }
            }
        }

        state.errorMessage?.let {
            Spacer(Modifier.height(14.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = ErrorColor)
        }
    }
}

/** Reads + downscales the picked image off the main thread (max 1024px, JPEG 85). */
private suspend fun readAndDownscale(context: Context, uri: Uri): ByteArray? =
    withContext(Dispatchers.IO) {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return@withContext null
        val source = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext bytes
        val scale = maxOf(source.width, source.height) / 1024f
        val bitmap = if (scale > 1f) {
            Bitmap.createScaledBitmap(
                source,
                (source.width / scale).toInt(),
                (source.height / scale).toInt(),
                true
            )
        } else source
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        out.toByteArray()
    }

@Composable
private fun UsernameStep(
    state: OnboardingUiState,
    viewModel: OnboardingViewModel,
    onPickAvatar: () -> Unit
) {
    Text(
        text = stringResource(R.string.onboarding_pick_username),
        style = MaterialTheme.typography.headlineMedium,
        color = TextPrimary
    )
    Spacer(Modifier.height(24.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.clickable(onClick = onPickAvatar)) {
            AvatarImage(url = state.avatarUrl, name = state.username.ifEmpty { "?" }, size = 72.dp)
            if (state.isUploadingAvatar) {
                CircularProgressIndicator(
                    color = Accent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.align(Alignment.Center).size(24.dp)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = stringResource(R.string.onboarding_add_photo),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.clickable(onClick = onPickAvatar)
        )
    }

    Spacer(Modifier.height(20.dp))
    DarkTextField(
        value = state.username,
        onValueChange = viewModel::onUsernameChange,
        label = stringResource(R.string.onboarding_username),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(6.dp))
    when (val a = state.availability) {
        is UsernameAvailability.Checking -> Text(
            stringResource(R.string.onboarding_checking),
            style = MaterialTheme.typography.bodySmall, color = TextSecondary
        )
        is UsernameAvailability.Available -> Text(
            stringResource(R.string.onboarding_available),
            style = MaterialTheme.typography.bodySmall, color = Accent
        )
        is UsernameAvailability.Taken -> Text(
            a.reason ?: stringResource(R.string.onboarding_taken),
            style = MaterialTheme.typography.bodySmall, color = ErrorColor
        )
        is UsernameAvailability.CheckFailed -> Text(
            a.message, style = MaterialTheme.typography.bodySmall, color = ErrorColor
        )
        UsernameAvailability.Idle -> {}
    }

    Spacer(Modifier.height(12.dp))
    DarkTextField(
        value = state.displayName,
        onValueChange = viewModel::onDisplayNameChange,
        label = stringResource(R.string.onboarding_display_name),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(24.dp))
    PrimaryButton(
        text = stringResource(R.string.onboarding_continue),
        onClick = viewModel::submitUsername,
        enabled = state.availability == UsernameAvailability.Available,
        loading = state.isSubmitting
    )
    TextButton(onClick = viewModel::skip, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.onboarding_skip), color = TextSecondary)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenresStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text(
        text = stringResource(R.string.onboarding_pick_genres),
        style = MaterialTheme.typography.headlineMedium,
        color = TextPrimary
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.onboarding_pick_genres_sub),
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
    )
    Spacer(Modifier.height(20.dp))
    FlowRow {
        Genre.all.forEach { genre ->
            val selected = genre.id in state.selectedGenres
            FilterChip(
                selected = selected,
                onClick = { viewModel.toggleGenre(genre.id) },
                label = { Text(genre.label) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Surface,
                    labelColor = TextSecondary,
                    selectedContainerColor = Accent,
                    selectedLabelColor = Background
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = Border,
                    selectedBorderColor = Accent
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
    Spacer(Modifier.height(24.dp))
    PrimaryButton(
        text = stringResource(R.string.onboarding_continue),
        onClick = viewModel::continueFromGenres,
        enabled = state.selectedGenres.size >= 3
    )
}

@Composable
private fun TempoStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text(
        text = stringResource(R.string.onboarding_pick_tempo),
        style = MaterialTheme.typography.headlineMedium,
        color = TextPrimary
    )
    Spacer(Modifier.height(20.dp))
    ReadingTempo.all.forEach { tempo ->
        val selected = state.tempo == tempo.id
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) Accent.copy(alpha = 0.14f) else Surface)
                .clickable { viewModel.selectTempo(tempo.id) }
                .padding(16.dp)
        ) {
            Text(
                tempo.label, style = MaterialTheme.typography.titleMedium,
                color = if (selected) Accent else TextPrimary
            )
            Text(tempo.sub, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
    Spacer(Modifier.height(24.dp))
    PrimaryButton(
        text = stringResource(R.string.onboarding_continue),
        onClick = viewModel::continueFromTempo
    )
}

@Composable
private fun AhaLoadingStep() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Accent)
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.onboarding_aha_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun AhaRevealStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val book = state.currentAhaBook ?: return
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.onboarding_aha_title),
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        Spacer(Modifier.height(20.dp))
        BookCoverImage(
            url = book.coverUrl,
            title = book.title,
            modifier = Modifier.width(160.dp).aspectRatio(2f / 3f),
            cornerRadius = 10.dp
        )
        Spacer(Modifier.height(14.dp))
        Text(book.title, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Text(book.authorLine, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        book.reason?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = Accent)
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton(
            text = stringResource(R.string.onboarding_save_to_shelf),
            onClick = viewModel::saveToShelf,
            loading = state.isAddingBook
        )
        Spacer(Modifier.height(8.dp))
        GhostButton(
            text = stringResource(R.string.onboarding_show_another),
            onClick = viewModel::showAnother
        )
        TextButton(onClick = viewModel::finish) {
            Text(stringResource(R.string.onboarding_skip), color = TextSecondary)
        }
    }
}
