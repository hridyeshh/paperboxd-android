package in.paperboxd.app.data.remote;

/**
 * Typed errors surfaced to repositories/UI. Maps the backend envelope
 * `{error, code, message?}` (plus the older nested `{error:{code,message}}`).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u0000 \b2\u00060\u0001j\u0002`\u0002:\u000b\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012B\u000f\b\u0004\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\t\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u00a8\u0006\u001c"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "Companion", "DecodingError", "Envelope", "Forbidden", "NetworkError", "NotFound", "RateLimited", "ServerError", "Unauthorized", "Unknown", "ValidationError", "Lin/paperboxd/app/data/remote/ApiError$DecodingError;", "Lin/paperboxd/app/data/remote/ApiError$Forbidden;", "Lin/paperboxd/app/data/remote/ApiError$NetworkError;", "Lin/paperboxd/app/data/remote/ApiError$NotFound;", "Lin/paperboxd/app/data/remote/ApiError$RateLimited;", "Lin/paperboxd/app/data/remote/ApiError$ServerError;", "Lin/paperboxd/app/data/remote/ApiError$Unauthorized;", "Lin/paperboxd/app/data/remote/ApiError$Unknown;", "Lin/paperboxd/app/data/remote/ApiError$ValidationError;", "app_release"})
public abstract class ApiError extends java.lang.Exception {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String message = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String FALLBACK = "Something went wrong \u2014 please try again";
    @org.jetbrains.annotations.NotNull()
    public static final in.paperboxd.app.data.remote.ApiError.Companion Companion = null;
    
    private ApiError(java.lang.String message) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getMessage() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\u0014\u0010\b\u001a\u0004\u0018\u00010\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0004H\u0002J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0004J\u0012\u0010\u000f\u001a\u00020\u00072\b\u0010\u0010\u001a\u0004\u0018\u00010\u0004H\u0002J\u0016\u0010\u0011\u001a\u0004\u0018\u00010\u0004*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$Companion;", "", "()V", "FALLBACK", "", "displayString", "e", "Lin/paperboxd/app/data/remote/ApiError$Envelope;", "friendlyForCode", "code", "fromResponse", "Lin/paperboxd/app/data/remote/ApiError;", "status", "", "errorBody", "parseEnvelope", "body", "stringOrNull", "Lcom/google/gson/JsonObject;", "key", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Builds a typed error from an HTTP status + raw error body.
         */
        @org.jetbrains.annotations.NotNull()
        public final in.paperboxd.app.data.remote.ApiError fromResponse(int status, @org.jetbrains.annotations.Nullable()
        java.lang.String errorBody) {
            return null;
        }
        
        private final java.lang.String displayString(in.paperboxd.app.data.remote.ApiError.Envelope e) {
            return null;
        }
        
        /**
         * Friendly copy for known machine codes (scan flow).
         */
        private final java.lang.String friendlyForCode(java.lang.String code) {
            return null;
        }
        
        /**
         * Tolerates both flat {error, code, message} and nested {error:{code,message}}.
         */
        private final in.paperboxd.app.data.remote.ApiError.Envelope parseEnvelope(java.lang.String body) {
            return null;
        }
        
        private final java.lang.String stringOrNull(com.google.gson.JsonObject $this$stringOrNull, java.lang.String key) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$DecodingError;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "(Ljava/lang/String;)V", "app_release"})
    public static final class DecodingError extends in.paperboxd.app.data.remote.ApiError {
        
        public DecodingError(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B)\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0006J\u000b\u0010\u000b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J-\u0010\u000e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0015"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$Envelope;", "", "error", "", "code", "message", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCode", "()Ljava/lang/String;", "getError", "getMessage", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_release"})
    static final class Envelope {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String error = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String code = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String message = null;
        
        public Envelope(@org.jetbrains.annotations.Nullable()
        java.lang.String error, @org.jetbrains.annotations.Nullable()
        java.lang.String code, @org.jetbrains.annotations.Nullable()
        java.lang.String message) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getError() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getCode() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getMessage() {
            return null;
        }
        
        public Envelope() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final in.paperboxd.app.data.remote.ApiError.Envelope copy(@org.jetbrains.annotations.Nullable()
        java.lang.String error, @org.jetbrains.annotations.Nullable()
        java.lang.String code, @org.jetbrains.annotations.Nullable()
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$Forbidden;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "(Ljava/lang/String;)V", "app_release"})
    public static final class Forbidden extends in.paperboxd.app.data.remote.ApiError {
        
        public Forbidden(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$NetworkError;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "(Ljava/lang/String;)V", "app_release"})
    public static final class NetworkError extends in.paperboxd.app.data.remote.ApiError {
        
        public NetworkError(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$NotFound;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "(Ljava/lang/String;)V", "app_release"})
    public static final class NotFound extends in.paperboxd.app.data.remote.ApiError {
        
        public NotFound(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$RateLimited;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "(Ljava/lang/String;)V", "app_release"})
    public static final class RateLimited extends in.paperboxd.app.data.remote.ApiError {
        
        public RateLimited(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$ServerError;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "code", "(Ljava/lang/String;Ljava/lang/String;)V", "getCode", "()Ljava/lang/String;", "app_release"})
    public static final class ServerError extends in.paperboxd.app.data.remote.ApiError {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String code = null;
        
        public ServerError(@org.jetbrains.annotations.NotNull()
        java.lang.String message, @org.jetbrains.annotations.NotNull()
        java.lang.String code) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCode() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$Unauthorized;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "(Ljava/lang/String;)V", "app_release"})
    public static final class Unauthorized extends in.paperboxd.app.data.remote.ApiError {
        
        public Unauthorized(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$Unknown;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "code", "(Ljava/lang/String;Ljava/lang/String;)V", "getCode", "()Ljava/lang/String;", "app_release"})
    public static final class Unknown extends in.paperboxd.app.data.remote.ApiError {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String code = null;
        
        public Unknown(@org.jetbrains.annotations.NotNull()
        java.lang.String message, @org.jetbrains.annotations.NotNull()
        java.lang.String code) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCode() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/data/remote/ApiError$ValidationError;", "Lin/paperboxd/app/data/remote/ApiError;", "message", "", "(Ljava/lang/String;)V", "app_release"})
    public static final class ValidationError extends in.paperboxd.app.data.remote.ApiError {
        
        public ValidationError(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
    }
}