package in.paperboxd.app.domain.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B?\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\fJ\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\bH\u00c6\u0003J\t\u0010\u0019\u001a\u00020\bH\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003JC\u0010\u001b\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020\bH\u00d6\u0001J\t\u0010 \u001a\u00020!H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\t\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006\""}, d2 = {"Lin/paperboxd/app/domain/model/LikesResponse;", "", "books", "", "Lin/paperboxd/app/domain/model/BookWithLikedAt;", "totalCount", "", "page", "", "pageSize", "pagination", "Lin/paperboxd/app/domain/model/PaginationMeta;", "(Ljava/util/List;JIILin/paperboxd/app/domain/model/PaginationMeta;)V", "getBooks", "()Ljava/util/List;", "getPage", "()I", "getPageSize", "getPagination", "()Lin/paperboxd/app/domain/model/PaginationMeta;", "getTotalCount", "()J", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"})
public final class LikesResponse {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.BookWithLikedAt> books = null;
    @com.google.gson.annotations.SerializedName(value = "total_count")
    private final long totalCount = 0L;
    private final int page = 0;
    @com.google.gson.annotations.SerializedName(value = "page_size")
    private final int pageSize = 0;
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.PaginationMeta pagination = null;
    
    public LikesResponse(@org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.BookWithLikedAt> books, long totalCount, int page, int pageSize, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.PaginationMeta pagination) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.BookWithLikedAt> getBooks() {
        return null;
    }
    
    public final long getTotalCount() {
        return 0L;
    }
    
    public final int getPage() {
        return 0;
    }
    
    public final int getPageSize() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.PaginationMeta getPagination() {
        return null;
    }
    
    public LikesResponse() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.BookWithLikedAt> component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int component4() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.PaginationMeta component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.LikesResponse copy(@org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.BookWithLikedAt> books, long totalCount, int page, int pageSize, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.PaginationMeta pagination) {
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