package in.paperboxd.app.domain.model;

/**
 * POST /api/v1/scan/analyze response.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJ:\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u00c6\u0001\u00a2\u0006\u0002\u0010\u0019J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\tH\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001a\u0010\b\u001a\u0004\u0018\u00010\t8\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006 "}, d2 = {"Lin/paperboxd/app/domain/model/ScanAnalyzeResponse;", "", "book", "Lin/paperboxd/app/domain/model/ScanBook;", "score", "Lin/paperboxd/app/domain/model/ScanScore;", "sources", "Lin/paperboxd/app/domain/model/ScanSources;", "scansRemaining", "", "(Lin/paperboxd/app/domain/model/ScanBook;Lin/paperboxd/app/domain/model/ScanScore;Lin/paperboxd/app/domain/model/ScanSources;Ljava/lang/Integer;)V", "getBook", "()Lin/paperboxd/app/domain/model/ScanBook;", "getScansRemaining", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getScore", "()Lin/paperboxd/app/domain/model/ScanScore;", "getSources", "()Lin/paperboxd/app/domain/model/ScanSources;", "component1", "component2", "component3", "component4", "copy", "(Lin/paperboxd/app/domain/model/ScanBook;Lin/paperboxd/app/domain/model/ScanScore;Lin/paperboxd/app/domain/model/ScanSources;Ljava/lang/Integer;)Lin/paperboxd/app/domain/model/ScanAnalyzeResponse;", "equals", "", "other", "hashCode", "toString", "", "app_release"})
public final class ScanAnalyzeResponse {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.domain.model.ScanBook book = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.domain.model.ScanScore score = null;
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.ScanSources sources = null;
    @com.google.gson.annotations.SerializedName(value = "scans_remaining")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer scansRemaining = null;
    
    public ScanAnalyzeResponse(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ScanBook book, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ScanScore score, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.ScanSources sources, @org.jetbrains.annotations.Nullable()
    java.lang.Integer scansRemaining) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.ScanBook getBook() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.ScanScore getScore() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.ScanSources getSources() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getScansRemaining() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.ScanBook component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.ScanScore component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.ScanSources component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.ScanAnalyzeResponse copy(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ScanBook book, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ScanScore score, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.ScanSources sources, @org.jetbrains.annotations.Nullable()
    java.lang.Integer scansRemaining) {
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