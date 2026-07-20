package in.paperboxd.app.ui.screens.write;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000V\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\bH\u0003\u001aA\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00040\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00040\bH\u0003\u00a2\u0006\u0002\u0010\u0011\u001aN\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00162\u0006\u0010\u0017\u001a\u00020\u00062\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00040\u000f2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00040\u000fH\u0003\u001aC\u0010\u001a\u001a\u00020\u00042\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u00062\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00040\b2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00040\bH\u0003\u00a2\u0006\u0002\u0010\u001f\u001a.\u0010 \u001a\u00020\u00042\u0006\u0010!\u001a\u00020\u00142\u0012\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00040\u000f2\b\b\u0002\u0010#\u001a\u00020$H\u0003\u001a4\u0010%\u001a\u00020\u00042\u0006\u0010&\u001a\u00020\u00062\u0006\u0010\'\u001a\u00020\u00062\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00040\b2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00040\bH\u0003\u001a(\u0010*\u001a\u00020\u00042\u0006\u0010+\u001a\u00020,2\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00040\b2\b\b\u0002\u0010.\u001a\u00020/H\u0007\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\u00a8\u00060"}, d2 = {"Line", "Landroidx/compose/ui/graphics/Color;", "J", "AttachButton", "", "open", "", "onClick", "Lkotlin/Function0;", "AttachedBookCard", "book", "Lin/paperboxd/app/domain/model/Book;", "rating", "", "onRate", "Lkotlin/Function1;", "onRemove", "(Lin/paperboxd/app/domain/model/Book;Ljava/lang/Integer;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)V", "BookSearchPanel", "query", "", "results", "", "searching", "onQueryChange", "onPick", "BottomToolbar", "charCount", "datePickerOpen", "onCalendar", "onStar", "(Ljava/lang/Integer;IZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V", "Editor", "content", "onChange", "modifier", "Landroidx/compose/ui/Modifier;", "NavBar", "canSubmit", "isLoading", "onCancel", "onPost", "WriteScreen", "user", "Lin/paperboxd/app/domain/model/User;", "onDismiss", "viewModel", "Lin/paperboxd/app/ui/screens/write/WriteViewModel;", "app_release"})
public final class WriteScreenKt {
    private static final long Line = 0L;
    
    /**
     * Compose sheet — iOS WriteView twin on the light paper palette (all PaperBoxd
     * screens are light). Quiet editor; the brutalist signatures are the attached
     * book card, the attach button, and the accent shadow under an active Post.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void WriteScreen(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.write.WriteViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void NavBar(boolean canSubmit, boolean isLoading, kotlin.jvm.functions.Function0<kotlin.Unit> onCancel, kotlin.jvm.functions.Function0<kotlin.Unit> onPost) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AttachedBookCard(in.paperboxd.app.domain.model.Book book, java.lang.Integer rating, kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onRate, kotlin.jvm.functions.Function0<kotlin.Unit> onRemove) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AttachButton(boolean open, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BookSearchPanel(java.lang.String query, java.util.List<in.paperboxd.app.domain.model.Book> results, boolean searching, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onQueryChange, kotlin.jvm.functions.Function1<? super in.paperboxd.app.domain.model.Book, kotlin.Unit> onPick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Editor(java.lang.String content, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onChange, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BottomToolbar(java.lang.Integer rating, int charCount, boolean datePickerOpen, kotlin.jvm.functions.Function0<kotlin.Unit> onCalendar, kotlin.jvm.functions.Function0<kotlin.Unit> onStar) {
    }
}