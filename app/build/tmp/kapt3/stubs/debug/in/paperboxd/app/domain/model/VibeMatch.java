package in.paperboxd.app.domain.model;

/**
 * One vibe-search match: the book plus why Jazy picked it.
 * Maps `types.VibeBookResult` — the score rides on the same object as the book
 * fields, so this is flat, not nested. iOS twin: `VibeMatch`.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u00a2\u0006\u0002\u0010\u0010J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010*\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010,\u001a\u00020\u000bH\u00c6\u0003J\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010/\u001a\u0004\u0018\u00010\u000fH\u00c6\u0003\u00a2\u0006\u0002\u0010 Jp\u00100\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u00c6\u0001\u00a2\u0006\u0002\u00101J\u0013\u00102\u001a\u0002032\b\u00104\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00105\u001a\u00020\u000fH\u00d6\u0001J\t\u00106\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\u00148F\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012R\u0011\u0010\u0019\u001a\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u001a\u0010\u000e\u001a\u0004\u0018\u00010\u000f8\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010!\u001a\u0004\b\u001f\u0010 R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&\u00a8\u00067"}, d2 = {"Lin/paperboxd/app/domain/model/VibeMatch;", "", "id", "", "slug", "volumeInfo", "Lin/paperboxd/app/domain/model/VolumeInfo;", "paperboxdStats", "Lin/paperboxd/app/domain/model/PaperboxdStats;", "apiSource", "similarityScore", "", "matchReason", "matchCaveat", "serverMatchPercent", "", "(Ljava/lang/String;Ljava/lang/String;Lin/paperboxd/app/domain/model/VolumeInfo;Lin/paperboxd/app/domain/model/PaperboxdStats;Ljava/lang/String;DLjava/lang/String;Ljava/lang/String;Ljava/lang/Integer;)V", "getApiSource", "()Ljava/lang/String;", "book", "Lin/paperboxd/app/domain/model/Book;", "getBook", "()Lin/paperboxd/app/domain/model/Book;", "getId", "getMatchCaveat", "matchPercent", "getMatchPercent", "()I", "getMatchReason", "getPaperboxdStats", "()Lin/paperboxd/app/domain/model/PaperboxdStats;", "getServerMatchPercent", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getSimilarityScore", "()D", "getSlug", "getVolumeInfo", "()Lin/paperboxd/app/domain/model/VolumeInfo;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Lin/paperboxd/app/domain/model/VolumeInfo;Lin/paperboxd/app/domain/model/PaperboxdStats;Ljava/lang/String;DLjava/lang/String;Ljava/lang/String;Ljava/lang/Integer;)Lin/paperboxd/app/domain/model/VibeMatch;", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class VibeMatch {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String slug = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.domain.model.VolumeInfo volumeInfo = null;
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.PaperboxdStats paperboxdStats = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String apiSource = null;
    private final double similarityScore = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String matchReason = null;
    
    /**
     * One honest note on what might not land. Empty when Claude is unavailable.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String matchCaveat = null;
    
    /**
     * Claude's own match number, so the pill and [matchReason] make the same
     * claim. Null against a backend running without an Anthropic key — Gson
     * leaves absent fields null regardless of the Kotlin default.
     */
    @com.google.gson.annotations.SerializedName(value = "matchPercent")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer serverMatchPercent = null;
    
    public VibeMatch(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.Nullable()
    java.lang.String slug, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.VolumeInfo volumeInfo, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.PaperboxdStats paperboxdStats, @org.jetbrains.annotations.Nullable()
    java.lang.String apiSource, double similarityScore, @org.jetbrains.annotations.NotNull()
    java.lang.String matchReason, @org.jetbrains.annotations.NotNull()
    java.lang.String matchCaveat, @org.jetbrains.annotations.Nullable()
    java.lang.Integer serverMatchPercent) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSlug() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.VolumeInfo getVolumeInfo() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.PaperboxdStats getPaperboxdStats() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApiSource() {
        return null;
    }
    
    public final double getSimilarityScore() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMatchReason() {
        return null;
    }
    
    /**
     * One honest note on what might not land. Empty when Claude is unavailable.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMatchCaveat() {
        return null;
    }
    
    /**
     * Claude's own match number, so the pill and [matchReason] make the same
     * claim. Null against a backend running without an Anthropic key — Gson
     * leaves absent fields null regardless of the Kotlin default.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getServerMatchPercent() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.Book getBook() {
        return null;
    }
    
    public final int getMatchPercent() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.VolumeInfo component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.PaperboxdStats component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.VibeMatch copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.Nullable()
    java.lang.String slug, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.VolumeInfo volumeInfo, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.PaperboxdStats paperboxdStats, @org.jetbrains.annotations.Nullable()
    java.lang.String apiSource, double similarityScore, @org.jetbrains.annotations.NotNull()
    java.lang.String matchReason, @org.jetbrains.annotations.NotNull()
    java.lang.String matchCaveat, @org.jetbrains.annotations.Nullable()
    java.lang.Integer serverMatchPercent) {
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