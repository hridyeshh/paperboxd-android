package `in`.paperboxd.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Design tokens — mirror the iOS asset catalog's LIGHT appearance.
//
// The app is light by design on every page: the window theme is paper
// (themes.xml), the brutalist kit (HL) is paper-and-ink, and every iOS screen
// forces `.preferredColorScheme(.light)` so the catalog resolves these values
// and never its dark variants. These used to carry the dark half of that
// catalog, which left anything falling back to the theme — dialogs, sheet
// containers, switches, menus, ripples — rendering dark against paper. Same
// hexes as the HL kit, so the two palettes cannot drift apart.
val Background = Color(0xFFF2EDE1)    // HL.Paper
val Surface = Color(0xFFFDFBF6)       // HL.Card
val Border = Color(0xFFE6DFD0)
val TextPrimary = Color(0xFF151513)   // HL.Ink
val TextSecondary = Color(0xFF6A6456) // HL.Muted
val Accent = Color(0xFFD23B26)        // HL.Accent
val Error = Color(0xFFC0271C)         // HL.Crimson — reads on paper

// Inline accents from the iOS PB kit (avatar rings, liked heart).
val Terracotta = Color(0xFFD97757)
val TerracottaDeep = Color(0xFF6B3520)
val LikeRed = Color(0xFFD72830)

val AvatarGradient = Brush.linearGradient(colors = listOf(Terracotta, TerracottaDeep))
