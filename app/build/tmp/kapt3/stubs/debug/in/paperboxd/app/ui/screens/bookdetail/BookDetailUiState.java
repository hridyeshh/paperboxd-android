package in.paperboxd.app.ui.screens.bookdetail;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b$\b\u0087\b\u0018\u00002\u00020\u0001B\u009b\u0001\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\t\u0012\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\t\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0019J\u000b\u0010*\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010,\u001a\u0004\u0018\u00010\u0017H\u00c6\u0003J\t\u0010-\u001a\u00020\u0005H\u00c6\u0003J\t\u0010.\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000f\u00100\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u00c6\u0003J\t\u00101\u001a\u00020\fH\u00c6\u0003J\u000f\u00102\u001a\b\u0012\u0004\u0012\u00020\u000e0\tH\u00c6\u0003J\t\u00103\u001a\u00020\u0010H\u00c6\u0003J\u000f\u00104\u001a\b\u0012\u0004\u0012\u00020\u00120\tH\u00c6\u0003J\u000f\u00105\u001a\b\u0012\u0004\u0012\u00020\u00140\tH\u00c6\u0003J\u009f\u0001\u00106\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\t2\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\t2\b\b\u0002\u0010\u0015\u001a\u00020\u00052\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0005H\u00c6\u0001J\u0013\u00107\u001a\u00020\u00052\b\u00108\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00109\u001a\u00020\u0010H\u00d6\u0001J\t\u0010:\u001a\u00020\u0007H\u00d6\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\t\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010!R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010%R\u0011\u0010\u0018\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010%R\u0011\u0010\u0015\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010%R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\t\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010!R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010!\u00a8\u0006;"}, d2 = {"Lin/paperboxd/app/ui/screens/bookdetail/BookDetailUiState;", "", "book", "Lin/paperboxd/app/domain/model/Book;", "isLoading", "", "errorMessage", "", "similarBooks", "", "Lin/paperboxd/app/domain/model/RecommendationItem;", "bookState", "Lin/paperboxd/app/ui/screens/bookdetail/BookUserState;", "friendsOnBook", "Lin/paperboxd/app/domain/model/FriendOnBook;", "friendsReadingCount", "", "friendReviews", "Lin/paperboxd/app/domain/model/FriendBookReview;", "reviews", "Lin/paperboxd/app/domain/model/BookReview;", "isSubmittingReview", "progress", "Lin/paperboxd/app/domain/model/ReadingProgress;", "isSavingProgress", "(Lin/paperboxd/app/domain/model/Book;ZLjava/lang/String;Ljava/util/List;Lin/paperboxd/app/ui/screens/bookdetail/BookUserState;Ljava/util/List;ILjava/util/List;Ljava/util/List;ZLin/paperboxd/app/domain/model/ReadingProgress;Z)V", "getBook", "()Lin/paperboxd/app/domain/model/Book;", "getBookState", "()Lin/paperboxd/app/ui/screens/bookdetail/BookUserState;", "getErrorMessage", "()Ljava/lang/String;", "getFriendReviews", "()Ljava/util/List;", "getFriendsOnBook", "getFriendsReadingCount", "()I", "()Z", "getProgress", "()Lin/paperboxd/app/domain/model/ReadingProgress;", "getReviews", "getSimilarBooks", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class BookDetailUiState {
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.Book book = null;
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.RecommendationItem> similarBooks = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.ui.screens.bookdetail.BookUserState bookState = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.FriendOnBook> friendsOnBook = null;
    private final int friendsReadingCount = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.FriendBookReview> friendReviews = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.BookReview> reviews = null;
    private final boolean isSubmittingReview = false;
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.ReadingProgress progress = null;
    private final boolean isSavingProgress = false;
    
    public BookDetailUiState(@org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.Book book, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.RecommendationItem> similarBooks, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.bookdetail.BookUserState bookState, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.FriendOnBook> friendsOnBook, int friendsReadingCount, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.FriendBookReview> friendReviews, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.BookReview> reviews, boolean isSubmittingReview, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.ReadingProgress progress, boolean isSavingProgress) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.Book getBook() {
        return null;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getErrorMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.RecommendationItem> getSimilarBooks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.bookdetail.BookUserState getBookState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.FriendOnBook> getFriendsOnBook() {
        return null;
    }
    
    public final int getFriendsReadingCount() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.FriendBookReview> getFriendReviews() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.BookReview> getReviews() {
        return null;
    }
    
    public final boolean isSubmittingReview() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.ReadingProgress getProgress() {
        return null;
    }
    
    public final boolean isSavingProgress() {
        return false;
    }
    
    public BookDetailUiState() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.Book component1() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.ReadingProgress component11() {
        return null;
    }
    
    public final boolean component12() {
        return false;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.RecommendationItem> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.bookdetail.BookUserState component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.FriendOnBook> component6() {
        return null;
    }
    
    public final int component7() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.FriendBookReview> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.BookReview> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.bookdetail.BookDetailUiState copy(@org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.Book book, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.RecommendationItem> similarBooks, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.bookdetail.BookUserState bookState, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.FriendOnBook> friendsOnBook, int friendsReadingCount, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.FriendBookReview> friendReviews, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.BookReview> reviews, boolean isSubmittingReview, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.ReadingProgress progress, boolean isSavingProgress) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}