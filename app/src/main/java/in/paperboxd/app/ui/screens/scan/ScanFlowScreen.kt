package `in`.paperboxd.app.ui.screens.scan

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Flow stages — iOS `ScanFlowView.Stage` twin. */
enum class ScanStage { Scan, Analyzing, Reveal, Breakdown }

data class ScanFlowUiState(
    val stage: ScanStage = ScanStage.Scan,
    val isbn: String = "",
    val pendingTitle: String? = null,
    val result: ScanResult? = null,
    val loadError: String? = null,
    /** The scan endpoint's only 403 is `scans_exhausted`; drives the exhausted layout. */
    val exhausted: Boolean = false,
    val tbrState: TbrState = TbrState.Idle,
    val toast: String? = null
)

@HiltViewModel
class ScanFlowViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanFlowUiState())
    val uiState: StateFlow<ScanFlowUiState> = _uiState.asStateFlow()

    fun go(stage: ScanStage) = _uiState.update { it.copy(stage = stage) }

    fun startScan(isbn: String, title: String?) {
        _uiState.update {
            it.copy(
                stage = ScanStage.Analyzing,
                isbn = isbn,
                pendingTitle = title,
                result = null,
                loadError = null,
                exhausted = false
            )
        }
        viewModelScope.launch {
            bookRepository.scanAnalyze(isbn)
                .onSuccess { response ->
                    // Persist the live free-scan count for Settings + result footers.
                    response.scansRemaining?.let { ScanPrefs.setScansRemaining(context, it) }
                    _uiState.update { it.copy(result = ScanResult.from(response)) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            loadError = friendlyMessage(e),
                            // The scan endpoint returns 403 only when free scans are used up.
                            exhausted = e is ApiError.Forbidden
                        )
                    }
                }
        }
    }

    fun retry() = startScan(_uiState.value.isbn, _uiState.value.pendingTitle)

    private fun friendlyMessage(e: Throwable): String = when (e) {
        is ApiError.DecodingError -> "Couldn't read the score. Try again."
        is ApiError.NetworkError -> "No internet connection"
        is ApiError -> e.message
        else -> "Something went wrong. Try again."
    }

    /** Title (or ISBN) search via `/books/search` — iOS `ScanService.search` twin. */
    suspend fun search(query: String): List<ScanSearchHit> =
        bookRepository.searchBooks(query, pageSize = 15).getOrNull()?.items.orEmpty().map {
            ScanSearchHit(
                id = it.id,
                title = it.title,
                author = it.authorLine,
                isbn = it.isbn,
                coverUrl = it.coverUrl
            )
        }

    /** Best-effort title for a scanned ISBN, to show on the confirmation card. */
    suspend fun lookupTitle(isbn: String): String? =
        runCatching { search(isbn) }.getOrNull()?.firstOrNull()?.title

    /**
     * Adds the scanned book to the reader's TBR. The backend resolves the book
     * by ISBN and auto-creates it if it isn't cached yet.
     */
    fun addToTbr(username: String?) {
        val state = _uiState.value
        if (state.tbrState == TbrState.Loading || state.tbrState == TbrState.Added) return
        if (username.isNullOrEmpty() || state.isbn.isEmpty()) {
            _uiState.update { it.copy(toast = "Sign in to save books") }
            return
        }
        _uiState.update { it.copy(tbrState = TbrState.Loading) }
        viewModelScope.launch {
            bookRepository.addToBookshelfByIsbn(username, state.isbn, "to-read")
                .onSuccess {
                    _uiState.update {
                        it.copy(tbrState = TbrState.Added, toast = "Added to your TBR")
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            tbrState = TbrState.Failed,
                            toast = (e as? ApiError)?.message ?: "Couldn't add — try again"
                        )
                    }
                }
        }
    }

    fun clearToast() = _uiState.update { it.copy(toast = null) }

    /**
     * Wipes the flow back to a fresh scanner. The VM is Activity-scoped (the flow
     * isn't a nav destination), so it survives close/reopen — without this, the
     * next open would re-render the previous book's breakdown instead of the camera.
     */
    fun reset() {
        _uiState.value = ScanFlowUiState()
    }
}

/**
 * Scan & Know flow coordinator — iOS `ScanFlowView` twin. Presented full-screen
 * from the dock's Pip scan button.
 *
 * Stages: Scan → Analyzing (the games fill the wait while the backend scores the
 * scanned ISBN) → Reveal (count-up) → Breakdown. The book + score + breakdown
 * are real — fetched from `POST /api/v1/scan/analyze`, not hardcoded.
 */
@Composable
fun ScanFlowScreen(
    user: User,
    onDismiss: () -> Unit,
    viewModel: ScanFlowViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Reset the retained VM as we leave, so the next open starts at the scanner.
    val dismiss = {
        viewModel.reset()
        onDismiss()
    }

    BackHandler {
        when (state.stage) {
            ScanStage.Scan -> dismiss()
            ScanStage.Analyzing -> viewModel.go(ScanStage.Scan)
            ScanStage.Reveal -> viewModel.go(ScanStage.Analyzing)
            ScanStage.Breakdown -> viewModel.go(ScanStage.Reveal)
        }
    }

    AnimatedContent(
        targetState = state.stage,
        transitionSpec = {
            if (targetState == ScanStage.Reveal || targetState == ScanStage.Breakdown) {
                (slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()) togetherWith fadeOut()
            } else {
                fadeIn() togetherWith fadeOut()
            }
        },
        label = "scan-stage",
        modifier = Modifier
            .fillMaxSize()
            .background(if (state.stage == ScanStage.Scan) Color.Black else SK.bg)
    ) { stage ->
        when (stage) {
            ScanStage.Scan -> ScanScreen(
                lookupTitle = { viewModel.lookupTitle(it) },
                search = { viewModel.search(it) },
                onScanIt = { isbn, title -> viewModel.startScan(isbn, title) },
                onClose = dismiss
            )
            ScanStage.Analyzing -> AnalyzingScreen(
                book = state.result,
                pendingTitle = state.pendingTitle,
                error = state.loadError,
                exhausted = state.exhausted,
                onReveal = { viewModel.go(ScanStage.Reveal) },
                onRetry = { viewModel.retry() },
                onBack = { viewModel.go(ScanStage.Scan) },
                onClose = dismiss
            )
            ScanStage.Reveal -> state.result?.let { result ->
                RevealScreen(
                    result = result,
                    onBreakdown = { viewModel.go(ScanStage.Breakdown) },
                    onClose = dismiss
                )
            } ?: Box(Modifier.fillMaxSize().background(SK.bg))
            ScanStage.Breakdown -> state.result?.let { result ->
                BreakdownScreen(
                    result = result,
                    tbrState = state.tbrState,
                    toast = state.toast,
                    onAddToTbr = { viewModel.addToTbr(user.username) },
                    onToastShown = { viewModel.clearToast() },
                    onClose = dismiss
                )
            } ?: Box(Modifier.fillMaxSize().background(SK.bg))
        }
    }
}
