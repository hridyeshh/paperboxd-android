package in.paperboxd.app.ui.screens.settings;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u001a)\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u00062\u0011\u0010\u0007\u001a\r\u0012\u0004\u0012\u00020\u00040\u0006\u00a2\u0006\u0002\b\bH\u0003\u001a&\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u00012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006H\u0003\u001a \u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u0001H\u0003\u001a#\u0010\u0012\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u00012\u0011\u0010\u0007\u001a\r\u0012\u0004\u0012\u00020\u00040\u0006\u00a2\u0006\u0002\b\bH\u0003\u001aN\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00012\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u00062\u0006\u0010\u0016\u001a\u00020\u00172\u001c\u0010\u0018\u001a\u0018\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00040\u0019\u00a2\u0006\u0002\b\b\u00a2\u0006\u0002\b\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u001dH\u0003\u001a&\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006H\u0003\u001a6\u0010\u001f\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00012\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00040\u00062\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u00062\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0007\u001a6\u0010!\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u00062\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u00062\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0007\u001a\u001e\u0010\"\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u00012\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006H\u0003\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"PRIVACY_TEXT", "", "TERMS_TEXT", "CircleChip", "", "onClick", "Lkotlin/Function0;", "content", "Landroidx/compose/runtime/Composable;", "InfoDialog", "title", "body", "onDismiss", "InfoRow", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "label", "value", "Section", "SettingsBody", "email", "onSignOut", "viewModel", "Lin/paperboxd/app/ui/screens/settings/SettingsViewModel;", "header", "Lkotlin/Function1;", "Landroidx/compose/foundation/layout/RowScope;", "Lkotlin/ExtensionFunctionType;", "modifier", "Landroidx/compose/ui/Modifier;", "SettingsRow", "SettingsScreen", "onBack", "SettingsSheet", "ToastChip", "msg", "onDone", "app_debug"})
public final class SettingsScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PRIVACY_TEXT = "PaperBoxd stores the reading data you give us \u2014 the books you log, reviews, shelves, and profile details \u2014 to run the app and show your activity to people you choose to share it with.\n\nWe don\'t sell your data. Book metadata and ratings come from third-party sources (Google Books, Open Library, Hardcover).\n\nYou can request deletion of your account and data at any time by emailing hello@paperboxd.in.\n\nFor the full, current policy see paperboxd.in/privacy.";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TERMS_TEXT = "By using PaperBoxd you agree to use it for personal, non-commercial book tracking and to respect other readers in the community.\n\nYou own the content you post. You grant us a licence to display it within the app so your friends and followers can see your activity.\n\nThe Scan & Know score is a recommendation aid, not a guarantee \u2014 it\'s generated from community data and your reading history.\n\nWe may update these terms as the app evolves. Continued use means you accept the current terms. Full terms at paperboxd.in/terms.";
    
    /**
     * iOS SettingsView twin — same sections/rows, restyled to the light brutalist
     * paper aesthetic used across the ported app. Opened from the profile hamburger.
     */
    @androidx.compose.runtime.Composable()
    public static final void SettingsScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSignOut, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.settings.SettingsViewModel viewModel) {
    }
    
    /**
     * Slide-up sheet twin of iOS `.sheet(isPresented: $showSettings)` — same body,
     * presented as a ModalBottomSheet over the profile instead of a full screen.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SettingsSheet(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSignOut, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.settings.SettingsViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SettingsBody(java.lang.String email, kotlin.jvm.functions.Function0<kotlin.Unit> onSignOut, in.paperboxd.app.ui.screens.settings.SettingsViewModel viewModel, kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.RowScope, kotlin.Unit> header, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Section(java.lang.String title, kotlin.jvm.functions.Function0<kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SettingsRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String label, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InfoRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String label, java.lang.String value) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CircleChip(kotlin.jvm.functions.Function0<kotlin.Unit> onClick, kotlin.jvm.functions.Function0<kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InfoDialog(java.lang.String title, java.lang.String body, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ToastChip(java.lang.String msg, kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
}