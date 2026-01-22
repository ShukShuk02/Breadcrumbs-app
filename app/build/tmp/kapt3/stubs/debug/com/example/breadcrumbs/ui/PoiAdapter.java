package com.example.breadcrumbs.ui;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001:\u0002\u000f\u0010B\u0007\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\nH\u0016\u00a8\u0006\u0011"}, d2 = {"Lcom/example/breadcrumbs/ui/PoiAdapter;", "Landroidx/recyclerview/widget/ListAdapter;", "Lcom/example/breadcrumbs/model/Poi;", "Lcom/example/breadcrumbs/ui/PoiAdapter$PoiViewHolder;", "<init>", "()V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "", "holder", "position", "PoiViewHolder", "PoiDiffCallback", "app_debug"})
public final class PoiAdapter extends androidx.recyclerview.widget.ListAdapter<com.example.breadcrumbs.model.Poi, com.example.breadcrumbs.ui.PoiAdapter.PoiViewHolder> {
    
    public PoiAdapter() {
        super(null);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.example.breadcrumbs.ui.PoiAdapter.PoiViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.example.breadcrumbs.ui.PoiAdapter.PoiViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0016J\u0018\u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0016\u00a8\u0006\n"}, d2 = {"Lcom/example/breadcrumbs/ui/PoiAdapter$PoiDiffCallback;", "Landroidx/recyclerview/widget/DiffUtil$ItemCallback;", "Lcom/example/breadcrumbs/model/Poi;", "<init>", "()V", "areItemsTheSame", "", "oldItem", "newItem", "areContentsTheSame", "app_debug"})
    public static final class PoiDiffCallback extends androidx.recyclerview.widget.DiffUtil.ItemCallback<com.example.breadcrumbs.model.Poi> {
        
        public PoiDiffCallback() {
            super();
        }
        
        @java.lang.Override()
        public boolean areItemsTheSame(@org.jetbrains.annotations.NotNull()
        com.example.breadcrumbs.model.Poi oldItem, @org.jetbrains.annotations.NotNull()
        com.example.breadcrumbs.model.Poi newItem) {
            return false;
        }
        
        @java.lang.Override()
        public boolean areContentsTheSame(@org.jetbrains.annotations.NotNull()
        com.example.breadcrumbs.model.Poi oldItem, @org.jetbrains.annotations.NotNull()
        com.example.breadcrumbs.model.Poi newItem) {
            return false;
        }
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/example/breadcrumbs/ui/PoiAdapter$PoiViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/example/breadcrumbs/databinding/ItemPoiBinding;", "<init>", "(Lcom/example/breadcrumbs/databinding/ItemPoiBinding;)V", "bind", "", "poi", "Lcom/example/breadcrumbs/model/Poi;", "app_debug"})
    public static final class PoiViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.example.breadcrumbs.databinding.ItemPoiBinding binding = null;
        
        public PoiViewHolder(@org.jetbrains.annotations.NotNull()
        com.example.breadcrumbs.databinding.ItemPoiBinding binding) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        com.example.breadcrumbs.model.Poi poi) {
        }
    }
}