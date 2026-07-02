package `in`.paperboxd.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Design tokens — mirror iOS asset catalog exactly.
val Background = Color(0xFF0A0A0A)
val Surface = Color(0xFF141414)
val Border = Color(0xFF2A2A2A)
val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFA0A0A0)
val Accent = Color(0xFFE8D5B7)
val Error = Color(0xFFE05252)

// Inline accents from the iOS PB kit (avatar rings, liked heart).
val Terracotta = Color(0xFFD97757)
val TerracottaDeep = Color(0xFF6B3520)
val LikeRed = Color(0xFFD72830)

val AvatarGradient = Brush.linearGradient(colors = listOf(Terracotta, TerracottaDeep))
