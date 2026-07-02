package in.paperboxd.app.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J,\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0015\u0010\u0016J,\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001a\u0010\u001bJ6\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\u001f2\b\b\u0002\u0010 \u001a\u00020\u001fH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b!\u0010\"J,\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b$\u0010\u001bJ,\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00180\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b&\u0010\u001bJ,\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00180\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b(\u0010\u001bR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\r\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006)"}, d2 = {"Lin/paperboxd/app/data/repository/DiaryRepository;", "", "api", "Lin/paperboxd/app/data/remote/ApiService;", "(Lin/paperboxd/app/data/remote/ApiService;)V", "_entryCreated", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lin/paperboxd/app/domain/model/DiaryEntry;", "_entryDeleted", "", "entryCreated", "Lkotlinx/coroutines/flow/SharedFlow;", "getEntryCreated", "()Lkotlinx/coroutines/flow/SharedFlow;", "entryDeleted", "getEntryDeleted", "create", "Lkotlin/Result;", "username", "body", "Lin/paperboxd/app/domain/model/DiaryCreateBody;", "create-0E7RQCE", "(Ljava/lang/String;Lin/paperboxd/app/domain/model/DiaryCreateBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "", "entryId", "delete-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "entries", "Lin/paperboxd/app/domain/model/DiaryEntriesResponse;", "page", "", "pageSize", "entries-BWLJW6A", "(Ljava/lang/String;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "entry", "entry-0E7RQCE", "like", "like-0E7RQCE", "unlike", "unlike-0E7RQCE", "app_debug"})
public final class DiaryRepository {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.remote.ApiService api = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<in.paperboxd.app.domain.model.DiaryEntry> _entryCreated = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<in.paperboxd.app.domain.model.DiaryEntry> entryCreated = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<java.lang.String> _entryDeleted = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<java.lang.String> entryDeleted = null;
    
    @javax.inject.Inject()
    public DiaryRepository(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.remote.ApiService api) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<in.paperboxd.app.domain.model.DiaryEntry> getEntryCreated() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<java.lang.String> getEntryDeleted() {
        return null;
    }
}