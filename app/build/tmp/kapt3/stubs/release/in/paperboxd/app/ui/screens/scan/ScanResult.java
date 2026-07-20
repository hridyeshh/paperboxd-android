package in.paperboxd.app.ui.screens.scan;

/**
 * Everything the reveal + breakdown screens render — iOS `ScanResult` twin.
 * Built from the backend response; no hardcoded book or score.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u001b\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b!\b\u0087\b\u0018\u0000 O2\u00020\u0001:\u0002OPB\u00b3\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\t\u0012\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u0012\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u0012\b\u0010\u0014\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0015\u001a\u00020\u0007\u0012\u0006\u0010\u0016\u001a\u00020\u0007\u0012\u0006\u0010\u0017\u001a\u00020\u0007\u0012\u0006\u0010\u0018\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\u0019J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\u000f\u00108\u001a\b\u0012\u0004\u0012\u00020\u000f0\tH\u00c6\u0003J\u000f\u00109\u001a\b\u0012\u0004\u0012\u00020\u00030\tH\u00c6\u0003J\u000f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00030\tH\u00c6\u0003J\u0010\u0010;\u001a\u0004\u0018\u00010\u0013H\u00c6\u0003\u00a2\u0006\u0002\u0010&J\u000b\u0010<\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010=\u001a\u00020\u0007H\u00c6\u0003J\t\u0010>\u001a\u00020\u0007H\u00c6\u0003J\t\u0010?\u001a\u00020\u0007H\u00c6\u0003J\t\u0010@\u001a\u00020\u0007H\u00c6\u0003J\t\u0010A\u001a\u00020\u0003H\u00c6\u0003J\t\u0010B\u001a\u00020\u0003H\u00c6\u0003J\t\u0010C\u001a\u00020\u0007H\u00c6\u0003J\u000f\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00030\tH\u00c6\u0003J\u000b\u0010E\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010F\u001a\u00020\u0007H\u00c6\u0003J\t\u0010G\u001a\u00020\u0003H\u00c6\u0003J\t\u0010H\u001a\u00020\u0003H\u00c6\u0003J\u00e0\u0001\u0010I\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\t2\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00030\t2\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00030\t2\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00132\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0015\u001a\u00020\u00072\b\b\u0002\u0010\u0016\u001a\u00020\u00072\b\b\u0002\u0010\u0017\u001a\u00020\u00072\b\b\u0002\u0010\u0018\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010JJ\u0013\u0010K\u001a\u0002002\b\u0010L\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010M\u001a\u00020\u0007H\u00d6\u0001J\t\u0010N\u001a\u00020\u0003H\u00d6\u0001R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0016\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001dR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001bR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001bR\u0011\u0010\u0018\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001fR\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001bR\u0015\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b%\u0010&R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001dR\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001fR\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001dR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001fR\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u001dR\u0011\u0010\u0015\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u001fR#\u0010.\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u000200\u0012\u0004\u0012\u00020\u00030/0\t8F\u00a2\u0006\u0006\u001a\u0004\b1\u0010\u001bR\u0011\u0010\u0017\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u001fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010\u001dR\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u0010\u001dR\u0011\u00105\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b6\u0010\u001d\u00a8\u0006Q"}, d2 = {"Lin/paperboxd/app/ui/screens/scan/ScanResult;", "", "isbn", "", "title", "author", "pages", "", "genres", "", "coverUrl", "matchScore", "verdict", "oneLine", "dimensions", "Lin/paperboxd/app/ui/screens/scan/ScanResult$Dimension;", "forYou", "againstYou", "internetRating", "", "ratingsCount", "readersCount", "communityRatings", "shelfCount", "friendsCount", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/util/List;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/Double;Ljava/lang/String;IIII)V", "getAgainstYou", "()Ljava/util/List;", "getAuthor", "()Ljava/lang/String;", "getCommunityRatings", "()I", "getCoverUrl", "getDimensions", "getForYou", "getFriendsCount", "getGenres", "getInternetRating", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getIsbn", "getMatchScore", "getOneLine", "getPages", "getRatingsCount", "getReadersCount", "reasons", "Lkotlin/Pair;", "", "getReasons", "getShelfCount", "getTitle", "getVerdict", "verdictSub", "getVerdictSub", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/util/List;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/Double;Ljava/lang/String;IIII)Lin/paperboxd/app/ui/screens/scan/ScanResult;", "equals", "other", "hashCode", "toString", "Companion", "Dimension", "app_release"})
public final class ScanResult {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String isbn = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String author = null;
    private final int pages = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> genres = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String coverUrl = null;
    private final int matchScore = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String verdict = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String oneLine = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.ui.screens.scan.ScanResult.Dimension> dimensions = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> forYou = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> againstYou = null;
    
    /**
     * "The internet" rating (e.g. Goodreads avg). Not always supplied.
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double internetRating = null;
    
    /**
     * Human ratings count, e.g. "23.4k".
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String ratingsCount = null;
    
    /**
     * Real source counts shown on the analyzing screen (from the backend).
     */
    private final int readersCount = 0;
    private final int communityRatings = 0;
    private final int shelfCount = 0;
    private final int friendsCount = 0;
    @org.jetbrains.annotations.NotNull()
    public static final in.paperboxd.app.ui.screens.scan.ScanResult.Companion Companion = null;
    
    public ScanResult(@org.jetbrains.annotations.NotNull()
    java.lang.String isbn, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String author, int pages, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> genres, @org.jetbrains.annotations.Nullable()
    java.lang.String coverUrl, int matchScore, @org.jetbrains.annotations.NotNull()
    java.lang.String verdict, @org.jetbrains.annotations.NotNull()
    java.lang.String oneLine, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.ui.screens.scan.ScanResult.Dimension> dimensions, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> forYou, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> againstYou, @org.jetbrains.annotations.Nullable()
    java.lang.Double internetRating, @org.jetbrains.annotations.Nullable()
    java.lang.String ratingsCount, int readersCount, int communityRatings, int shelfCount, int friendsCount) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getIsbn() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAuthor() {
        return null;
    }
    
    public final int getPages() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getGenres() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCoverUrl() {
        return null;
    }
    
    public final int getMatchScore() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVerdict() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOneLine() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.ui.screens.scan.ScanResult.Dimension> getDimensions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getForYou() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getAgainstYou() {
        return null;
    }
    
    /**
     * "The internet" rating (e.g. Goodreads avg). Not always supplied.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getInternetRating() {
        return null;
    }
    
    /**
     * Human ratings count, e.g. "23.4k".
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRatingsCount() {
        return null;
    }
    
    /**
     * Real source counts shown on the analyzing screen (from the backend).
     */
    public final int getReadersCount() {
        return 0;
    }
    
    public final int getCommunityRatings() {
        return 0;
    }
    
    public final int getShelfCount() {
        return 0;
    }
    
    public final int getFriendsCount() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVerdictSub() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<kotlin.Pair<java.lang.Boolean, java.lang.String>> getReasons() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.ui.screens.scan.ScanResult.Dimension> component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    public final int component15() {
        return 0;
    }
    
    public final int component16() {
        return 0;
    }
    
    public final int component17() {
        return 0;
    }
    
    public final int component18() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final int component4() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    public final int component7() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.scan.ScanResult copy(@org.jetbrains.annotations.NotNull()
    java.lang.String isbn, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String author, int pages, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> genres, @org.jetbrains.annotations.Nullable()
    java.lang.String coverUrl, int matchScore, @org.jetbrains.annotations.NotNull()
    java.lang.String verdict, @org.jetbrains.annotations.NotNull()
    java.lang.String oneLine, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.ui.screens.scan.ScanResult.Dimension> dimensions, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> forYou, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> againstYou, @org.jetbrains.annotations.Nullable()
    java.lang.Double internetRating, @org.jetbrains.annotations.Nullable()
    java.lang.String ratingsCount, int readersCount, int communityRatings, int shelfCount, int friendsCount) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n\u00a8\u0006\u000b"}, d2 = {"Lin/paperboxd/app/ui/screens/scan/ScanResult$Companion;", "", "()V", "compactCount", "", "n", "", "from", "Lin/paperboxd/app/ui/screens/scan/ScanResult;", "response", "Lin/paperboxd/app/domain/model/ScanAnalyzeResponse;", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final in.paperboxd.app.ui.screens.scan.ScanResult from(@org.jetbrains.annotations.NotNull()
        in.paperboxd.app.domain.model.ScanAnalyzeResponse response) {
            return null;
        }
        
        /**
         * Compact human count, e.g. 1896 → "1.9k", 662 → "662". Null for 0.
         */
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String compactCount(int n) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0014"}, d2 = {"Lin/paperboxd/app/ui/screens/scan/ScanResult$Dimension;", "", "name", "", "value", "", "(Ljava/lang/String;D)V", "getName", "()Ljava/lang/String;", "getValue", "()D", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "app_release"})
    public static final class Dimension {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String name = null;
        private final double value = 0.0;
        
        public Dimension(@org.jetbrains.annotations.NotNull()
        java.lang.String name, double value) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getName() {
            return null;
        }
        
        public final double getValue() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final double component2() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final in.paperboxd.app.ui.screens.scan.ScanResult.Dimension copy(@org.jetbrains.annotations.NotNull()
        java.lang.String name, double value) {
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
}