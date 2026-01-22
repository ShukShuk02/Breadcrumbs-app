package com.example.breadcrumbs.ui;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0016J8\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\f2\u0006\u0010\u001a\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u001f\u001a\u00020\fH\u0002J\b\u0010 \u001a\u00020\u0013H\u0016R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u00058BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000f\u001a\u0010\u0012\f\u0012\n \u0011*\u0004\u0018\u00010\f0\f0\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/example/breadcrumbs/ui/AddPoiFragment;", "Landroidx/fragment/app/Fragment;", "<init>", "()V", "_binding", "Lcom/example/breadcrumbs/databinding/FragmentAddPoiBinding;", "binding", "getBinding", "()Lcom/example/breadcrumbs/databinding/FragmentAddPoiBinding;", "firebaseManager", "Lcom/example/breadcrumbs/data/remote/FirebaseManager;", "tripId", "", "imageUri", "Landroid/net/Uri;", "getContent", "Landroidx/activity/result/ActivityResultLauncher;", "kotlin.jvm.PlatformType", "onViewCreated", "", "view", "Landroid/view/View;", "savedInstanceState", "Landroid/os/Bundle;", "savePoi", "id", "desc", "loc", "lat", "", "lng", "imgUrl", "onDestroyView", "app_debug"})
public final class AddPoiFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.example.breadcrumbs.databinding.FragmentAddPoiBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final com.example.breadcrumbs.data.remote.FirebaseManager firebaseManager = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String tripId;
    @org.jetbrains.annotations.Nullable()
    private android.net.Uri imageUri;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> getContent = null;
    
    public AddPoiFragment() {
        super();
    }
    
    private final com.example.breadcrumbs.databinding.FragmentAddPoiBinding getBinding() {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void savePoi(java.lang.String id, java.lang.String desc, java.lang.String loc, double lat, double lng, java.lang.String imgUrl) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}