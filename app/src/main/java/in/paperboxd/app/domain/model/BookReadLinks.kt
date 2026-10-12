package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Where a reader can get the text of a book. Maps `read_links` on the
 * single-book detail response (backend docs/API.md); never on list items.
 * A store that doesn't carry the book is null. Amazon and WorldCat are always set.
 *
 * iOS twin: `BookReadLinks.swift`.
 */
data class BookReadLinks(
    @SerializedName("google_play_buy_link") val googlePlayBuyLink: String? = null,
    @SerializedName("apple_books_url") val appleBooksUrl: String? = null,
    @SerializedName("amazon_search_url") val amazonSearchUrl: String,
    @SerializedName("worldcat_url") val worldcatUrl: String,
    @SerializedName("is_public_domain") val isPublicDomain: Boolean = false,
    @SerializedName("gutenberg_id") val gutenbergId: Int? = null,
    @SerializedName("gutenberg_html_url") val gutenbergHtmlUrl: String? = null,
    @SerializedName("gutenberg_epub_url") val gutenbergEpubUrl: String? = null
) {
    /** Store buttons in display order, null links dropped. */
    val stores: List<Pair<String, String>>
        get() = listOf(
            "Amazon Kindle" to amazonSearchUrl,
            "Google Play Books" to googlePlayBuyLink,
            "Apple Books" to appleBooksUrl,
            "Find at your library" to worldcatUrl
        ).mapNotNull { (label, url) -> url?.let { label to it } }
}
