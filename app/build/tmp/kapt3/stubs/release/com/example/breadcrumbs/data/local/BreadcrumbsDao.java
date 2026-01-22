package com.example.breadcrumbs.data.local;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u000f\u001a\u00020\u00032\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u0014\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00110\u0014H\'J\u0018\u0010\u0015\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0016\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0018\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u001c\u0010\u001b\u001a\u00020\u00032\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u001c\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u00110\u00142\u0006\u0010\u0016\u001a\u00020\tH\'J\u0016\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\n\u00a8\u0006\u001f\u00c0\u0006\u0003"}, d2 = {"Lcom/example/breadcrumbs/data/local/BreadcrumbsDao;", "", "insertUser", "", "user", "Lcom/example/breadcrumbs/data/local/LocalUser;", "(Lcom/example/breadcrumbs/data/local/LocalUser;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUser", "userId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertTrip", "trip", "Lcom/example/breadcrumbs/data/local/LocalTrip;", "(Lcom/example/breadcrumbs/data/local/LocalTrip;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertTrips", "trips", "", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllTrips", "Lkotlinx/coroutines/flow/Flow;", "getTrip", "tripId", "insertPoi", "poi", "Lcom/example/breadcrumbs/data/local/LocalPoi;", "(Lcom/example/breadcrumbs/data/local/LocalPoi;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertPois", "pois", "getPoisForTrip", "deletePoisForTrip", "app_release"})
@androidx.room.Dao()
public abstract interface BreadcrumbsDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertUser(@org.jetbrains.annotations.NotNull()
    com.example.breadcrumbs.data.local.LocalUser user, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM users WHERE id = :userId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUser(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.breadcrumbs.data.local.LocalUser> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTrip(@org.jetbrains.annotations.NotNull()
    com.example.breadcrumbs.data.local.LocalTrip trip, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTrips(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.breadcrumbs.data.local.LocalTrip> trips, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM trips ORDER BY startDate DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.breadcrumbs.data.local.LocalTrip>> getAllTrips();
    
    @androidx.room.Query(value = "SELECT * FROM trips WHERE id = :tripId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTrip(@org.jetbrains.annotations.NotNull()
    java.lang.String tripId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.breadcrumbs.data.local.LocalTrip> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPoi(@org.jetbrains.annotations.NotNull()
    com.example.breadcrumbs.data.local.LocalPoi poi, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPois(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.breadcrumbs.data.local.LocalPoi> pois, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM pois WHERE tripId = :tripId ORDER BY timestamp ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.breadcrumbs.data.local.LocalPoi>> getPoisForTrip(@org.jetbrains.annotations.NotNull()
    java.lang.String tripId);
    
    @androidx.room.Query(value = "DELETE FROM pois WHERE tripId = :tripId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deletePoisForTrip(@org.jetbrains.annotations.NotNull()
    java.lang.String tripId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}