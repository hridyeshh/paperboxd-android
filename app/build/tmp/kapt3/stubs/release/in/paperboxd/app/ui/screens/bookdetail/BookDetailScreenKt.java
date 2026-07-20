package in.paperboxd.app.ui.screens.bookdetail;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u00c0\u0001\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u000e\u001aF\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00040\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001aI\u0010\u000f\u001a\u00020\u00042\u0017\u0010\u0010\u001a\u0013\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00040\f\u00a2\u0006\u0002\b\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001a6\u0010\u0019\u001a\u00020\u00042\b\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0014\u0010\u001c\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u001b\u0012\u0004\u0012\u00020\u00040\f2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001a\u0016\u0010\u001e\u001a\u00020\u00042\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\b0 H\u0003\u001a\u00e6\u0001\u0010!\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u00132\b\u0010%\u001a\u0004\u0018\u00010\u00132\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010&\u001a\u0004\u0018\u00010\u001b2\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00040\f2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0014\u0010*\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u001b\u0012\u0004\u0012\u00020\u00040\f2,\u0010+\u001a(\u0012\u0004\u0012\u00020-\u0012\u0006\u0012\u0004\u0018\u00010\u0013\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00040\f\u0012\u0004\u0012\u00020\u00040,2\u001e\u0010.\u001a\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00040\f\u0012\u0004\u0012\u00020\u00040\f2\u0018\u0010/\u001a\u0014\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020\u000400H\u0003\u001aP\u00101\u001a\u00020\u00042\u0006\u00102\u001a\u0002032\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00040\f2\u0012\u00104\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00040\f2\b\b\u0002\u00105\u001a\u000206H\u0007\u001a@\u00107\u001a\u00020\u00042\b\u0010&\u001a\u0004\u0018\u00010\u001b2\u0006\u00108\u001a\u00020\u00152\f\u00109\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0003\u001a\u0018\u0010;\u001a\u00020\u00042\u0006\u0010<\u001a\u00020\u00132\u0006\u0010=\u001a\u00020\u0013H\u0003\u001a&\u0010>\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010?\u001a\b\u0012\u0004\u0012\u00020@0 2\u0006\u0010A\u001a\u00020-H\u0003\u001a\u0010\u0010B\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a\u00ce\u0001\u0010C\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020#2\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00040\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0014\u0010D\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\n\u0012\u0004\u0012\u00020\u00040\f2,\u0010+\u001a(\u0012\u0004\u0012\u00020-\u0012\u0006\u0012\u0004\u0018\u00010\u0013\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00040\f\u0012\u0004\u0012\u00020\u00040,2\u001e\u0010.\u001a\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00040\f\u0012\u0004\u0012\u00020\u00040\f2\u0018\u0010/\u001a\u0014\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020\u0004002\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0003\u001a\u0016\u0010E\u001a\u00020\u00042\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020F0 H\u0003\u001a\\\u0010G\u001a\u00020\u00042\b\u0010H\u001a\u0004\u0018\u00010\n2\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010I\u001a\u00020\u00152\u001a\u0010J\u001a\u0016\u0012\u0004\u0012\u00020-\u0012\u0006\u0012\u0004\u0018\u00010\u0013\u0012\u0004\u0012\u00020\u0004002\f\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\f\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001aH\u0010M\u001a\u00020\u00042\u0006\u0010N\u001a\u00020\u001b2\u0006\u0010\u0010\u001a\u00020O2\u0006\u0010=\u001a\u00020\u00132\u0006\u0010P\u001a\u00020\u00132\b\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0014\u0010\u001c\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u001b\u0012\u0004\u0012\u00020\u00040\fH\u0003\u001a \u0010Q\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001aC\u0010R\u001a\u00020\u00042\b\u0010S\u001a\u0004\u0018\u00010T2\b\u0010U\u001a\u0004\u0018\u00010-2\u0006\u0010V\u001a\u00020\u00152\u0018\u0010W\u001a\u0014\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020\u000400H\u0003\u00a2\u0006\u0002\u0010X\u001a0\u0010Y\u001a\u00020\u00042\b\b\u0002\u0010Z\u001a\u00020[2\u001c\u0010\\\u001a\u0018\u0012\u0004\u0012\u00020]\u0012\u0004\u0012\u00020\u00040\f\u00a2\u0006\u0002\b\u0011\u00a2\u0006\u0002\b^H\u0003\u001a<\u0010_\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010`\u001a\u00020\u00152\u0006\u0010I\u001a\u00020\u00152\f\u0010J\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\f\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001aP\u0010a\u001a\u00020\u00042\u0006\u0010b\u001a\u00020-2\u0006\u0010I\u001a\u00020\u00152\u0006\u0010c\u001a\u00020\u00152\u0012\u0010J\u001a\u000e\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020\u00040\f2\f\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\f\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001aP\u0010d\u001a\u00020\u00042\u0006\u0010b\u001a\u00020\u00132\u0006\u0010I\u001a\u00020\u00152\u0006\u0010c\u001a\u00020\u00152\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00040\f2\f\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\f\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001a*\u0010f\u001a\u00020\u00042\f\u0010g\u001a\b\u0012\u0004\u0012\u00020h0 2\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00040\fH\u0003\u001a)\u0010i\u001a\u00020\u00042\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0011\u0010\\\u001a\r\u0012\u0004\u0012\u00020\u00040\u000e\u00a2\u0006\u0002\b\u0011H\u0003\u001a\"\u0010j\u001a\u00020\u00042\u0006\u0010k\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0003\u001a\u0010\u0010l\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a\u001e\u0010m\u001a\u00020\u00042\u0006\u0010n\u001a\u00020\u00132\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00040\u000eH\u0003\u001a\u0010\u0010o\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a\u001a\u0010p\u001a\u00020\u00042\u0006\u0010q\u001a\u00020\u00132\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0003\u001a\u0012\u0010r\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\u001a\u0010\u0010s\u001a\u00020\u00132\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\u001a\u0010\u0010t\u001a\u00020\u00132\u0006\u0010u\u001a\u00020-H\u0002\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\u00a8\u0006v"}, d2 = {"Line", "Landroidx/compose/ui/graphics/Color;", "J", "ActionRow", "", "book", "Lin/paperboxd/app/domain/model/Book;", "myReview", "Lin/paperboxd/app/domain/model/BookReview;", "activePanel", "Lin/paperboxd/app/ui/screens/bookdetail/InlinePanel;", "onTogglePanel", "Lkotlin/Function1;", "onShare", "Lkotlin/Function0;", "ActionTile", "icon", "Landroidx/compose/runtime/Composable;", "label", "", "active", "", "modifier", "Landroidx/compose/ui/Modifier;", "onClick", "AddToLibrarySheet", "current", "Lin/paperboxd/app/ui/screens/bookdetail/BookDetailViewModel$LibraryShelf;", "onSelect", "onDismiss", "AllReviewsSection", "reviews", "", "BookDetailContent", "state", "Lin/paperboxd/app/ui/screens/bookdetail/BookDetailUiState;", "toast", "handle", "currentShelf", "onBack", "onOpenBook", "onToggleLike", "onSelectShelf", "onSubmitReview", "Lkotlin/Function3;", "", "onDeleteReview", "onUpdateProgress", "Lkotlin/Function2;", "BookDetailScreen", "user", "Lin/paperboxd/app/domain/model/User;", "onOpenProfile", "viewModel", "Lin/paperboxd/app/ui/screens/bookdetail/BookDetailViewModel;", "BottomDock", "isLiked", "onAdd", "onLike", "BrutalSectionHeader", "num", "title", "CoverBlock", "friends", "Lin/paperboxd/app/domain/model/FriendOnBook;", "readingCount", "DescriptionSection", "DetailBody", "onSetPanel", "FriendsSaySection", "Lin/paperboxd/app/domain/model/FriendBookReview;", "InlinePanelView", "panel", "isSubmitting", "onSubmit", "onDelete", "onClose", "LibraryOption", "shelf", "Landroidx/compose/ui/graphics/vector/ImageVector;", "sub", "MiniHeader", "PageProgressCard", "progress", "Lin/paperboxd/app/domain/model/ReadingProgress;", "bookPageCount", "isSaving", "onUpdate", "(Lin/paperboxd/app/domain/model/ReadingProgress;Ljava/lang/Integer;ZLkotlin/jvm/functions/Function2;)V", "PanelShell", "horizontalAlignment", "Landroidx/compose/ui/Alignment$Horizontal;", "content", "Landroidx/compose/foundation/layout/ColumnScope;", "Lkotlin/ExtensionFunctionType;", "PanelSubmitRow", "enabled", "RatePanel", "initial", "canDelete", "ReviewPanel", "onPost", "SimilarSection", "items", "Lin/paperboxd/app/domain/model/RecommendationItem;", "SquareIcon", "StatCell", "value", "StatStrip", "StepButton", "symbol", "TitleBlock", "ToastBar", "message", "bookTagline", "readingTime", "starsString", "rating", "app_release"})
public final class BookDetailScreenKt {
    private static final long Line = 0L;
    
    @androidx.compose.runtime.Composable()
    public static final void BookDetailScreen(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenProfile, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BookDetailContent(in.paperboxd.app.ui.screens.bookdetail.BookDetailUiState state, java.lang.String toast, java.lang.String handle, in.paperboxd.app.domain.model.BookReview myReview, in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf currentShelf, kotlin.jvm.functions.Function0<kotlin.Unit> onBack, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, kotlin.jvm.functions.Function0<kotlin.Unit> onToggleLike, kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf, kotlin.Unit> onSelectShelf, kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.lang.String, ? super kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit>, kotlin.Unit> onSubmitReview, kotlin.jvm.functions.Function1<? super kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit>, kotlin.Unit> onDeleteReview, kotlin.jvm.functions.Function2<? super java.lang.Integer, ? super java.lang.Integer, kotlin.Unit> onUpdateProgress) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MiniHeader(in.paperboxd.app.domain.model.Book book, kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SquareIcon(kotlin.jvm.functions.Function0<kotlin.Unit> onClick, kotlin.jvm.functions.Function0<kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DetailBody(in.paperboxd.app.ui.screens.bookdetail.BookDetailUiState state, in.paperboxd.app.domain.model.BookReview myReview, in.paperboxd.app.ui.screens.bookdetail.InlinePanel activePanel, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, kotlin.jvm.functions.Function0<kotlin.Unit> onShare, kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.bookdetail.InlinePanel, kotlin.Unit> onSetPanel, kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.lang.String, ? super kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit>, kotlin.Unit> onSubmitReview, kotlin.jvm.functions.Function1<? super kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit>, kotlin.Unit> onDeleteReview, kotlin.jvm.functions.Function2<? super java.lang.Integer, ? super java.lang.Integer, kotlin.Unit> onUpdateProgress, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CoverBlock(in.paperboxd.app.domain.model.Book book, java.util.List<in.paperboxd.app.domain.model.FriendOnBook> friends, int readingCount) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TitleBlock(in.paperboxd.app.domain.model.Book book) {
    }
    
    private static final java.lang.String bookTagline(in.paperboxd.app.domain.model.Book book) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StatStrip(in.paperboxd.app.domain.model.Book book) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StatCell(java.lang.String value, java.lang.String label, androidx.compose.ui.Modifier modifier) {
    }
    
    private static final java.lang.String readingTime(in.paperboxd.app.domain.model.Book book) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ActionRow(in.paperboxd.app.domain.model.Book book, in.paperboxd.app.domain.model.BookReview myReview, in.paperboxd.app.ui.screens.bookdetail.InlinePanel activePanel, kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.bookdetail.InlinePanel, kotlin.Unit> onTogglePanel, kotlin.jvm.functions.Function0<kotlin.Unit> onShare) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ActionTile(kotlin.jvm.functions.Function1<? super androidx.compose.ui.graphics.Color, kotlin.Unit> icon, java.lang.String label, boolean active, androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InlinePanelView(in.paperboxd.app.ui.screens.bookdetail.InlinePanel panel, in.paperboxd.app.domain.model.BookReview myReview, boolean isSubmitting, kotlin.jvm.functions.Function2<? super java.lang.Integer, ? super java.lang.String, kotlin.Unit> onSubmit, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, kotlin.jvm.functions.Function0<kotlin.Unit> onClose) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RatePanel(int initial, boolean isSubmitting, boolean canDelete, kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onSubmit, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, kotlin.jvm.functions.Function0<kotlin.Unit> onClose) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ReviewPanel(java.lang.String initial, boolean isSubmitting, boolean canDelete, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPost, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, kotlin.jvm.functions.Function0<kotlin.Unit> onClose) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PanelShell(androidx.compose.ui.Alignment.Horizontal horizontalAlignment, kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.ColumnScope, kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PanelSubmitRow(java.lang.String label, boolean enabled, boolean isSubmitting, kotlin.jvm.functions.Function0<kotlin.Unit> onSubmit, kotlin.jvm.functions.Function0<kotlin.Unit> onClose) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PageProgressCard(in.paperboxd.app.domain.model.ReadingProgress progress, java.lang.Integer bookPageCount, boolean isSaving, kotlin.jvm.functions.Function2<? super java.lang.Integer, ? super java.lang.Integer, kotlin.Unit> onUpdate) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StepButton(java.lang.String symbol, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BrutalSectionHeader(java.lang.String num, java.lang.String title) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DescriptionSection(in.paperboxd.app.domain.model.Book book) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SimilarSection(java.util.List<in.paperboxd.app.domain.model.RecommendationItem> items, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FriendsSaySection(java.util.List<in.paperboxd.app.domain.model.FriendBookReview> reviews) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AllReviewsSection(java.util.List<in.paperboxd.app.domain.model.BookReview> reviews) {
    }
    
    private static final java.lang.String starsString(int rating) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BottomDock(in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf currentShelf, boolean isLiked, kotlin.jvm.functions.Function0<kotlin.Unit> onAdd, kotlin.jvm.functions.Function0<kotlin.Unit> onLike, androidx.compose.ui.Modifier modifier) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    private static final void AddToLibrarySheet(in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf current, kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf, kotlin.Unit> onSelect, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LibraryOption(in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf shelf, androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title, java.lang.String sub, in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf current, kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf, kotlin.Unit> onSelect) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ToastBar(java.lang.String message, androidx.compose.ui.Modifier modifier) {
    }
}