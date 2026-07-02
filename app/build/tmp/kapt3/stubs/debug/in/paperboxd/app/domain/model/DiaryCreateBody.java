package in.paperboxd.app.domain.model;

/**
 * Body for POST /users/{username}/diary.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B-\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\bJ\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J:\u0010\u0014\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0015J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0006H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0003H\u00d6\u0001R\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\n\u00a8\u0006\u001b"}, d2 = {"Lin/paperboxd/app/domain/model/DiaryCreateBody;", "", "bookId", "", "content", "rating", "", "readingDate", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)V", "getBookId", "()Ljava/lang/String;", "getContent", "getRating", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getReadingDate", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)Lin/paperboxd/app/domain/model/DiaryCreateBody;", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class DiaryCreateBody {
    @com.google.gson.annotations.SerializedName(value = "book_id")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String bookId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String content = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer rating = null;
    @com.google.gson.annotations.SerializedName(value = "reading_date")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String readingDate = null;
    
    public DiaryCreateBody(@org.jetbrains.annotations.Nullable()
    java.lang.String bookId, @org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.Nullable()
    java.lang.Integer rating, @org.jetbrains.annotations.NotNull()
    java.lang.String readingDate) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBookId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getContent() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getRating() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getReadingDate() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.DiaryCreateBody copy(@org.jetbrains.annotations.Nullable()
    java.lang.String bookId, @org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.Nullable()
    java.lang.Integer rating, @org.jetbrains.annotations.NotNull()
    java.lang.String readingDate) {
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