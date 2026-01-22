package com.example.breadcrumbs.data.remote;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u000f2\u0006\u0010\u0014\u001a\u00020\u0015J\u001a\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u00180\u00172\u0006\u0010\u0019\u001a\u00020\u000bJ\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00130\u000f2\u0006\u0010\u001b\u001a\u00020\u001cJ\u001a\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0\u00180\u00172\u0006\u0010\u001e\u001a\u00020\u000bJ\u001c\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u000f2\u0006\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b8F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\r\u00a8\u0006#"}, d2 = {"Lcom/example/breadcrumbs/data/remote/FirebaseManager;", "", "<init>", "()V", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "db", "Lcom/google/firebase/firestore/FirebaseFirestore;", "storage", "Lcom/google/firebase/storage/FirebaseStorage;", "currentUserId", "", "getCurrentUserId", "()Ljava/lang/String;", "signInAnonymously", "Lcom/google/android/gms/tasks/Task;", "Lcom/google/firebase/auth/AuthResult;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveTrip", "Ljava/lang/Void;", "trip", "Lcom/example/breadcrumbs/model/Trip;", "getUserTrips_Flow", "Lkotlinx/coroutines/flow/Flow;", "", "userId", "savePoi", "poi", "Lcom/example/breadcrumbs/model/Poi;", "getPoisForTrip_Flow", "tripId", "uploadImage", "Landroid/net/Uri;", "uri", "path", "app_release"})
public final class FirebaseManager {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.storage.FirebaseStorage storage = null;
    
    public FirebaseManager() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUserId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signInAnonymously(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<java.lang.Void> saveTrip(@org.jetbrains.annotations.NotNull()
    com.example.breadcrumbs.model.Trip trip) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.breadcrumbs.model.Trip>> getUserTrips_Flow(@org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<java.lang.Void> savePoi(@org.jetbrains.annotations.NotNull()
    com.example.breadcrumbs.model.Poi poi) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.breadcrumbs.model.Poi>> getPoisForTrip_Flow(@org.jetbrains.annotations.NotNull()
    java.lang.String tripId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<android.net.Uri> uploadImage(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String path) {
        return null;
    }
}