package in.paperboxd.app.data.remote;

/**
 * App-wide session signals. MainActivity collects [expired] and routes to auth
 * — the Android twin of the iOS `.paperboxdSessionExpired` notification.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\n\u001a\u00020\u0005R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\u000b"}, d2 = {"Lin/paperboxd/app/data/remote/SessionEvents;", "", "()V", "_expired", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "", "expired", "Lkotlinx/coroutines/flow/SharedFlow;", "getExpired", "()Lkotlinx/coroutines/flow/SharedFlow;", "emitExpired", "app_debug"})
public final class SessionEvents {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<kotlin.Unit> _expired = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<kotlin.Unit> expired = null;
    
    @javax.inject.Inject()
    public SessionEvents() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<kotlin.Unit> getExpired() {
        return null;
    }
    
    public final void emitExpired() {
    }
}