package `in`.paperboxd.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.AuthRepository
import `in`.paperboxd.app.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Imports a Goodreads library export (CSV) into the reader's bookshelf.
 *
 * Mirrors the iOS GoodreadsImportView flow: parse the CSV → for each row find a
 * catalogue match (ISBN first via the auto-creating shelf endpoint, else a
 * title+author search) → add it to the shelf with the mapped status. Star
 * ratings are not imported; the mobile progress model tracks pages, not ratings,
 * so shelf placement is the honest working subset.
 */
@HiltViewModel
class GoodreadsImportViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed interface Phase {
        data object Idle : Phase
        data class Importing(val done: Int, val total: Int) : Phase
        data class Finished(val imported: Int, val skipped: Int, val total: Int) : Phase
        data class Error(val message: String) : Phase
    }

    private val _phase = MutableStateFlow<Phase>(Phase.Idle)
    val phase: StateFlow<Phase> = _phase.asStateFlow()

    fun reset() { _phase.value = Phase.Idle }

    /** [csv] is the full decoded text of the Goodreads export file. */
    fun runImport(csv: String) {
        val username = authRepository.cachedUser()?.username
        if (username.isNullOrEmpty()) {
            _phase.value = Phase.Error("You need to be signed in to import.")
            return
        }

        val rows = parseCSV(csv)
        if (rows.isEmpty()) {
            _phase.value = Phase.Error(
                "No books found in that CSV. Export your library from Goodreads and try again."
            )
            return
        }

        viewModelScope.launch {
            _phase.value = Phase.Importing(done = 0, total = rows.size)
            var imported = 0
            var skipped = 0
            // Sequential keeps the backend un-hammered and drives live progress.
            rows.forEachIndexed { index, row ->
                if (importRow(row, username)) imported++ else skipped++
                _phase.value = Phase.Importing(done = index + 1, total = rows.size)
            }
            _phase.value = Phase.Finished(imported = imported, skipped = skipped, total = rows.size)
        }
    }

    /** Find one row's book and add it to the shelf. Returns true on success. */
    private suspend fun importRow(row: Map<String, String>, username: String): Boolean {
        val title = row["Title"].orEmpty()
        if (title.isBlank()) return false
        val author = row["Author"] ?: row["Author l-f"] ?: ""
        val isbn13 = cleanISBN(row["ISBN13"] ?: "")
        val isbn = cleanISBN(row["ISBN"] ?: "")
        val status = mapStatus(row["Exclusive Shelf"] ?: "to-read")

        // ISBN path: the shelf endpoint auto-creates the book if uncached, so it
        // succeeds far more often than a catalogue search.
        val bestIsbn = isbn13.ifEmpty { isbn }
        if (bestIsbn.isNotEmpty()) {
            if (bookRepository.addToBookshelfByIsbn(username, bestIsbn, status).isSuccess) return true
            // fall through to a title search if the ISBN wasn't resolvable
        }

        val query = "$title $author".trim()
        val bookId = bookRepository.searchBooks(query, pageSize = 1)
            .getOrNull()?.items?.firstOrNull()?.id ?: return false
        return bookRepository.addToBookshelf(username, bookId, status).isSuccess
    }

    companion object {
        /** Goodreads maps its exclusive shelves onto our three shelf statuses. */
        fun mapStatus(exclusive: String): String = when (exclusive) {
            "read" -> "read"
            "currently-reading" -> "reading"
            else -> "to-read"
        }

        /** Goodreads writes ISBNs as `="0451524935"` — strip the `="` wrapper. */
        fun cleanISBN(raw: String): String =
            raw.replace("=", "").replace("\"", "").trim()

        fun parseCSV(text: String): List<Map<String, String>> {
            val lines = text.split("\r\n", "\n", "\r")
                .filter { it.isNotBlank() }
            if (lines.size < 2) return emptyList()

            val headers = parseCSVLine(lines[0]).map { it.trim() }
            return lines.drop(1).mapNotNull { line ->
                val values = parseCSVLine(line)
                val obj = headers.mapIndexed { i, h ->
                    h to (values.getOrNull(i)?.trim() ?: "")
                }.toMap()
                if (obj["Title"].isNullOrBlank()) null else obj
            }
        }

        /** Split one CSV line, respecting quoted fields (which may contain commas). */
        fun parseCSVLine(line: String): List<String> {
            val result = mutableListOf<String>()
            val current = StringBuilder()
            var inQuotes = false
            for (ch in line) {
                when {
                    ch == '"' -> inQuotes = !inQuotes
                    ch == ',' && !inQuotes -> { result.add(current.toString()); current.clear() }
                    else -> current.append(ch)
                }
            }
            result.add(current.toString())
            return result
        }
    }
}
