package in.paperboxd.app.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b3\b\u0087\b\u0018\u00002\u00020\u0001B\u00e5\u0001\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u000b\u0012\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000b\u0012\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u000b\u0012\u000e\b\u0002\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u000b\u0012\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u000b\u0012\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u000b\u0012\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u001b\u0012\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u000e\u0012\b\b\u0002\u0010\u001d\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001f\u0012\b\b\u0002\u0010 \u001a\u00020\u000e\u00a2\u0006\u0002\u0010!J\u000b\u0010;\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000f\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00150\u000bH\u00c6\u0003J\u000f\u0010=\u001a\b\u0012\u0004\u0012\u00020\u00170\u000bH\u00c6\u0003J\u000f\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00190\u000bH\u00c6\u0003J\u000b\u0010?\u001a\u0004\u0018\u00010\u001bH\u00c6\u0003J\u0010\u0010@\u001a\u0004\u0018\u00010\u000eH\u00c6\u0003\u00a2\u0006\u0002\u00107J\t\u0010A\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010B\u001a\u0004\u0018\u00010\u001fH\u00c6\u0003J\t\u0010C\u001a\u00020\u000eH\u00c6\u0003J\t\u0010D\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010E\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010F\u001a\u00020\tH\u00c6\u0003J\u000f\u0010G\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u00c6\u0003J\u0010\u0010H\u001a\u0004\u0018\u00010\u000eH\u00c6\u0003\u00a2\u0006\u0002\u00107J\u000f\u0010I\u001a\b\u0012\u0004\u0012\u00020\u00100\u000bH\u00c6\u0003J\u000f\u0010J\u001a\b\u0012\u0004\u0012\u00020\u00120\u000bH\u00c6\u0003J\u000f\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00120\u000bH\u00c6\u0003J\u00ee\u0001\u0010L\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u000b2\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000b2\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u000b2\u000e\b\u0002\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u000b2\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u000b2\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u000b2\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u001d\u001a\u00020\u00052\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\b\u0002\u0010 \u001a\u00020\u000eH\u00c6\u0001\u00a2\u0006\u0002\u0010MJ\u0013\u0010N\u001a\u00020\u00052\b\u0010O\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010P\u001a\u00020\u000eH\u00d6\u0001J\t\u0010Q\u001a\u00020\u0007H\u00d6\u0001R\u0013\u0010\u001e\u001a\u0004\u0018\u00010\u001f\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010 \u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\'R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\'R\u0011\u0010\u001d\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010,R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010,R\u0013\u0010\u001a\u001a\u0004\u0018\u00010\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010\'R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u00101R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\'R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010\'R\u0015\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\n\n\u0002\u00108\u001a\u0004\b6\u00107R\u0015\u0010\u001c\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\n\n\u0002\u00108\u001a\u0004\b9\u00107R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010\'\u00a8\u0006R"}, d2 = {"Lin/paperboxd/app/ui/screens/profile/ProfileUiState;", "", "profile", "Lin/paperboxd/app/domain/model/UserProfile;", "isLoading", "", "errorMessage", "", "selectedTab", "Lin/paperboxd/app/ui/screens/profile/ProfileTab;", "shelfBooks", "", "Lin/paperboxd/app/domain/model/BookWithStatus;", "shelfTotal", "", "diaryEntries", "Lin/paperboxd/app/domain/model/DiaryEntry;", "ownLists", "Lin/paperboxd/app/domain/model/ReadingList;", "savedLists", "tbrItems", "Lin/paperboxd/app/domain/model/TbrItem;", "authors", "Lin/paperboxd/app/domain/model/AuthorSummary;", "favoriteBooks", "Lin/paperboxd/app/domain/model/FavoriteBook;", "lastLoggedBook", "Lin/paperboxd/app/domain/model/LastLoggedBook;", "streak", "isFollowLoading", "activity", "Lin/paperboxd/app/domain/model/ReadingActivity;", "activityYear", "(Lin/paperboxd/app/domain/model/UserProfile;ZLjava/lang/String;Lin/paperboxd/app/ui/screens/profile/ProfileTab;Ljava/util/List;Ljava/lang/Integer;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lin/paperboxd/app/domain/model/LastLoggedBook;Ljava/lang/Integer;ZLin/paperboxd/app/domain/model/ReadingActivity;I)V", "getActivity", "()Lin/paperboxd/app/domain/model/ReadingActivity;", "getActivityYear", "()I", "getAuthors", "()Ljava/util/List;", "getDiaryEntries", "getErrorMessage", "()Ljava/lang/String;", "getFavoriteBooks", "()Z", "getLastLoggedBook", "()Lin/paperboxd/app/domain/model/LastLoggedBook;", "getOwnLists", "getProfile", "()Lin/paperboxd/app/domain/model/UserProfile;", "getSavedLists", "getSelectedTab", "()Lin/paperboxd/app/ui/screens/profile/ProfileTab;", "getShelfBooks", "getShelfTotal", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getStreak", "getTbrItems", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Lin/paperboxd/app/domain/model/UserProfile;ZLjava/lang/String;Lin/paperboxd/app/ui/screens/profile/ProfileTab;Ljava/util/List;Ljava/lang/Integer;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lin/paperboxd/app/domain/model/LastLoggedBook;Ljava/lang/Integer;ZLin/paperboxd/app/domain/model/ReadingActivity;I)Lin/paperboxd/app/ui/screens/profile/ProfileUiState;", "equals", "other", "hashCode", "toString", "app_debug"})
public final class ProfileUiState {
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.UserProfile profile = null;
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.ui.screens.profile.ProfileTab selectedTab = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.BookWithStatus> shelfBooks = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer shelfTotal = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.DiaryEntry> diaryEntries = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.ReadingList> ownLists = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.ReadingList> savedLists = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.TbrItem> tbrItems = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.AuthorSummary> authors = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.FavoriteBook> favoriteBooks = null;
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.LastLoggedBook lastLoggedBook = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer streak = null;
    private final boolean isFollowLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.ReadingActivity activity = null;
    private final int activityYear = 0;
    
    public ProfileUiState(@org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.UserProfile profile, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.profile.ProfileTab selectedTab, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.BookWithStatus> shelfBooks, @org.jetbrains.annotations.Nullable()
    java.lang.Integer shelfTotal, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.DiaryEntry> diaryEntries, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.ReadingList> ownLists, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.ReadingList> savedLists, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.TbrItem> tbrItems, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.AuthorSummary> authors, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.FavoriteBook> favoriteBooks, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.LastLoggedBook lastLoggedBook, @org.jetbrains.annotations.Nullable()
    java.lang.Integer streak, boolean isFollowLoading, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.ReadingActivity activity, int activityYear) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.UserProfile getProfile() {
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
    public final in.paperboxd.app.ui.screens.profile.ProfileTab getSelectedTab() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.BookWithStatus> getShelfBooks() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getShelfTotal() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.DiaryEntry> getDiaryEntries() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.ReadingList> getOwnLists() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.ReadingList> getSavedLists() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.TbrItem> getTbrItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.AuthorSummary> getAuthors() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.FavoriteBook> getFavoriteBooks() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.LastLoggedBook getLastLoggedBook() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getStreak() {
        return null;
    }
    
    public final boolean isFollowLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.ReadingActivity getActivity() {
        return null;
    }
    
    public final int getActivityYear() {
        return 0;
    }
    
    public ProfileUiState() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.UserProfile component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.TbrItem> component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.AuthorSummary> component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.FavoriteBook> component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.LastLoggedBook component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component14() {
        return null;
    }
    
    public final boolean component15() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.ReadingActivity component16() {
        return null;
    }
    
    public final int component17() {
        return 0;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.profile.ProfileTab component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.BookWithStatus> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.DiaryEntry> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.ReadingList> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.ReadingList> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.profile.ProfileUiState copy(@org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.UserProfile profile, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.profile.ProfileTab selectedTab, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.BookWithStatus> shelfBooks, @org.jetbrains.annotations.Nullable()
    java.lang.Integer shelfTotal, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.DiaryEntry> diaryEntries, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.ReadingList> ownLists, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.ReadingList> savedLists, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.TbrItem> tbrItems, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.AuthorSummary> authors, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.FavoriteBook> favoriteBooks, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.LastLoggedBook lastLoggedBook, @org.jetbrains.annotations.Nullable()
    java.lang.Integer streak, boolean isFollowLoading, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.ReadingActivity activity, int activityYear) {
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