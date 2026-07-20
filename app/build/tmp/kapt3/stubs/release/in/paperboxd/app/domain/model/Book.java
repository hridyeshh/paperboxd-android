package in.paperboxd.app.domain.model;

/**
 * Full book detail. Maps types.BookResponse from the Go backend; metadata is
 * nested in `volumeInfo` mirroring the Google Books API shape.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010/\u001a\u00020\u0003H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u00101\u001a\u00020\u0006H\u00c6\u0003J\u000b\u00102\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\u000b\u00103\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u00104\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003JM\u00105\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u00106\u001a\u0002072\b\u00108\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00109\u001a\u00020#H\u00d6\u0001J\t\u0010:\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\rR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00030\u00118F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u00158F\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00030\u00118F\u00a2\u0006\u0006\u001a\u0004\b\u0019\u0010\u0013R\u0013\u0010\u001a\u001a\u0004\u0018\u00010\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u001b\u0010\rR\u0013\u0010\u001c\u001a\u0004\u0018\u00010\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u001d\u0010\rR\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\rR\u0013\u0010 \u001a\u0004\u0018\u00010\u00038F\u00a2\u0006\u0006\u001a\u0004\b!\u0010\rR\u0013\u0010\"\u001a\u0004\u0018\u00010#8F\u00a2\u0006\u0006\u001a\u0004\b$\u0010%R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R\u0013\u0010(\u001a\u0004\u0018\u00010\u00038F\u00a2\u0006\u0006\u001a\u0004\b)\u0010\rR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\rR\u0011\u0010+\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b,\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.\u00a8\u0006;"}, d2 = {"Lin/paperboxd/app/domain/model/Book;", "", "id", "", "slug", "volumeInfo", "Lin/paperboxd/app/domain/model/VolumeInfo;", "paperboxdStats", "Lin/paperboxd/app/domain/model/PaperboxdStats;", "googleBooksId", "apiSource", "(Ljava/lang/String;Ljava/lang/String;Lin/paperboxd/app/domain/model/VolumeInfo;Lin/paperboxd/app/domain/model/PaperboxdStats;Ljava/lang/String;Ljava/lang/String;)V", "getApiSource", "()Ljava/lang/String;", "authorLine", "getAuthorLine", "authors", "", "getAuthors", "()Ljava/util/List;", "averageRating", "", "getAverageRating", "()Ljava/lang/Double;", "categories", "getCategories", "coverUrl", "getCoverUrl", "description", "getDescription", "getGoogleBooksId", "getId", "isbn", "getIsbn", "pageCount", "", "getPageCount", "()Ljava/lang/Integer;", "getPaperboxdStats", "()Lin/paperboxd/app/domain/model/PaperboxdStats;", "publishedYear", "getPublishedYear", "getSlug", "title", "getTitle", "getVolumeInfo", "()Lin/paperboxd/app/domain/model/VolumeInfo;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "app_release"})
public final class Book {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String slug = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.domain.model.VolumeInfo volumeInfo = null;
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.PaperboxdStats paperboxdStats = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String googleBooksId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String apiSource = null;
    
    public Book(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.Nullable()
    java.lang.String slug, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.VolumeInfo volumeInfo, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.PaperboxdStats paperboxdStats, @org.jetbrains.annotations.Nullable()
    java.lang.String googleBooksId, @org.jetbrains.annotations.Nullable()
    java.lang.String apiSource) {
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
    public final java.lang.String getGoogleBooksId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApiSource() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getAuthors() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAuthorLine() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDescription() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getPageCount() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPublishedYear() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getAverageRating() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getIsbn() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCoverUrl() {
        return null;
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.Book copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.Nullable()
    java.lang.String slug, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.VolumeInfo volumeInfo, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.PaperboxdStats paperboxdStats, @org.jetbrains.annotations.Nullable()
    java.lang.String googleBooksId, @org.jetbrains.annotations.Nullable()
    java.lang.String apiSource) {
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