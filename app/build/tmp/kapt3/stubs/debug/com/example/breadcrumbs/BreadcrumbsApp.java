package com.example.breadcrumbs;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\n\u001a\u00020\u000bH\u0016R\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\f"}, d2 = {"Lcom/example/breadcrumbs/BreadcrumbsApp;", "Landroid/app/Application;", "<init>", "()V", "database", "Lcom/example/breadcrumbs/data/local/AppDatabase;", "getDatabase", "()Lcom/example/breadcrumbs/data/local/AppDatabase;", "database$delegate", "Lkotlin/Lazy;", "onCreate", "", "app_debug"})
public final class BreadcrumbsApp extends android.app.Application {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy database$delegate = null;
    
    public BreadcrumbsApp() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.breadcrumbs.data.local.AppDatabase getDatabase() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
}