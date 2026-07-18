package in.paperboxd.app.ui.screens.settings;

/**
 * Imports a Goodreads library export (CSV) into the reader's bookshelf.
 *
 * Mirrors the iOS GoodreadsImportView flow: parse the CSV → for each row find a
 * catalogue match (ISBN first via the auto-creating shelf endpoint, else a
 * title+author search) → add it to the shelf with the mapped status. Star
 * ratings are not imported; the mobile progress model tracks pages, not ratings,
 * so shelf placement is the honest working subset.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0007\u0018\u0000 \u00192\u00020\u0001:\u0002\u0019\u001aB\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J*\u0010\u000e\u001a\u00020\u000f2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0013\u001a\u00020\u0012H\u0082@\u00a2\u0006\u0002\u0010\u0014J\u0006\u0010\u0015\u001a\u00020\u0016J\u000e\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0012R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u001b"}, d2 = {"Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel;", "Landroidx/lifecycle/ViewModel;", "bookRepository", "Lin/paperboxd/app/data/repository/BookRepository;", "authRepository", "Lin/paperboxd/app/data/repository/AuthRepository;", "(Lin/paperboxd/app/data/repository/BookRepository;Lin/paperboxd/app/data/repository/AuthRepository;)V", "_phase", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase;", "phase", "Lkotlinx/coroutines/flow/StateFlow;", "getPhase", "()Lkotlinx/coroutines/flow/StateFlow;", "importRow", "", "row", "", "", "username", "(Ljava/util/Map;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "reset", "", "runImport", "csv", "Companion", "Phase", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class GoodreadsImportViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase> _phase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase> phase = null;
    @org.jetbrains.annotations.NotNull()
    public static final in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Companion Companion = null;
    
    @javax.inject.Inject()
    public GoodreadsImportViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.BookRepository bookRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.AuthRepository authRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase> getPhase() {
        return null;
    }
    
    public final void reset() {
    }
    
    /**
     * [csv] is the full decoded text of the Goodreads export file.
     */
    public final void runImport(@org.jetbrains.annotations.NotNull()
    java.lang.String csv) {
    }
    
    /**
     * Find one row's book and add it to the shelf. Returns true on success.
     */
    private final java.lang.Object importRow(java.util.Map<java.lang.String, java.lang.String> row, java.lang.String username, kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0010$\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004J\u000e\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0004J \u0010\b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\n0\t2\u0006\u0010\u000b\u001a\u00020\u0004J\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\t2\u0006\u0010\r\u001a\u00020\u0004\u00a8\u0006\u000e"}, d2 = {"Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Companion;", "", "()V", "cleanISBN", "", "raw", "mapStatus", "exclusive", "parseCSV", "", "", "text", "parseCSVLine", "line", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Goodreads maps its exclusive shelves onto our three shelf statuses.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String mapStatus(@org.jetbrains.annotations.NotNull()
        java.lang.String exclusive) {
            return null;
        }
        
        /**
         * Goodreads writes ISBNs as `="0451524935"` — strip the `="` wrapper.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String cleanISBN(@org.jetbrains.annotations.NotNull()
        java.lang.String raw) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.util.Map<java.lang.String, java.lang.String>> parseCSV(@org.jetbrains.annotations.NotNull()
        java.lang.String text) {
            return null;
        }
        
        /**
         * Split one CSV line, respecting quoted fields (which may contain commas).
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> parseCSVLine(@org.jetbrains.annotations.NotNull()
        java.lang.String line) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bv\u0018\u00002\u00020\u0001:\u0004\u0002\u0003\u0004\u0005\u0082\u0001\u0004\u0006\u0007\b\t\u00a8\u0006\n"}, d2 = {"Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase;", "", "Error", "Finished", "Idle", "Importing", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase$Error;", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase$Finished;", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase$Idle;", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase$Importing;", "app_debug"})
    public static abstract interface Phase {
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase$Error;", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Error implements in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            public Error(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                super();
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase.Error copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
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
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2 = {"Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase$Finished;", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase;", "imported", "", "skipped", "total", "(III)V", "getImported", "()I", "getSkipped", "getTotal", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "toString", "", "app_debug"})
        public static final class Finished implements in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase {
            private final int imported = 0;
            private final int skipped = 0;
            private final int total = 0;
            
            public Finished(int imported, int skipped, int total) {
                super();
            }
            
            public final int getImported() {
                return 0;
            }
            
            public final int getSkipped() {
                return 0;
            }
            
            public final int getTotal() {
                return 0;
            }
            
            public final int component1() {
                return 0;
            }
            
            public final int component2() {
                return 0;
            }
            
            public final int component3() {
                return 0;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase.Finished copy(int imported, int skipped, int total) {
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
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase$Idle;", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Idle implements in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase {
            @org.jetbrains.annotations.NotNull()
            public static final in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase.Idle INSTANCE = null;
            
            private Idle() {
                super();
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
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0013"}, d2 = {"Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase$Importing;", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel$Phase;", "done", "", "total", "(II)V", "getDone", "()I", "getTotal", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "toString", "", "app_debug"})
        public static final class Importing implements in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase {
            private final int done = 0;
            private final int total = 0;
            
            public Importing(int done, int total) {
                super();
            }
            
            public final int getDone() {
                return 0;
            }
            
            public final int getTotal() {
                return 0;
            }
            
            public final int component1() {
                return 0;
            }
            
            public final int component2() {
                return 0;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel.Phase.Importing copy(int done, int total) {
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
}