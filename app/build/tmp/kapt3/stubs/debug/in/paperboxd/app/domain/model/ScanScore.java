package in.paperboxd.app.domain.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001BI\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0007H\u00c6\u0003J\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00070\tH\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00070\tH\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0007H\u00c6\u0003JQ\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\t2\b\b\u0002\u0010\u000b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\"\u001a\u00020\u0007H\u00d6\u0001R\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\t8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001c\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u0016\u0010\u000b\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0013\u00a8\u0006#"}, d2 = {"Lin/paperboxd/app/domain/model/ScanScore;", "", "overallScore", "", "dimensions", "Lin/paperboxd/app/domain/model/ScanDimensions;", "verdict", "", "forYou", "", "againstYou", "oneLine", "(ILin/paperboxd/app/domain/model/ScanDimensions;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;)V", "getAgainstYou", "()Ljava/util/List;", "getDimensions", "()Lin/paperboxd/app/domain/model/ScanDimensions;", "getForYou", "getOneLine", "()Ljava/lang/String;", "getOverallScore", "()I", "getVerdict", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class ScanScore {
    @com.google.gson.annotations.SerializedName(value = "overall_score")
    private final int overallScore = 0;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.domain.model.ScanDimensions dimensions = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String verdict = null;
    @com.google.gson.annotations.SerializedName(value = "for_you")
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> forYou = null;
    @com.google.gson.annotations.SerializedName(value = "against_you")
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> againstYou = null;
    @com.google.gson.annotations.SerializedName(value = "one_line")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String oneLine = null;
    
    public ScanScore(int overallScore, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ScanDimensions dimensions, @org.jetbrains.annotations.NotNull()
    java.lang.String verdict, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> forYou, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> againstYou, @org.jetbrains.annotations.NotNull()
    java.lang.String oneLine) {
        super();
    }
    
    public final int getOverallScore() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.ScanDimensions getDimensions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVerdict() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOneLine() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.ScanDimensions component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.domain.model.ScanScore copy(int overallScore, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ScanDimensions dimensions, @org.jetbrains.annotations.NotNull()
    java.lang.String verdict, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> forYou, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> againstYou, @org.jetbrains.annotations.NotNull()
    java.lang.String oneLine) {
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