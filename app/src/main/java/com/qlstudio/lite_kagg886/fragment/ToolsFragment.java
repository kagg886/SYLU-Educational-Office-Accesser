package com.qlstudio.lite_kagg886.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.adapter.ToolsAdapter;
import com.qlstudio.lite_kagg886.widget.GridItemDecoration;
import org.jetbrains.annotations.NotNull;

public class ToolsFragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        RecyclerView root = new RecyclerView(getContext());
        root.setLayoutManager(new GridLayoutManager(getContext(), 3));
        root.setAdapter(new ToolsAdapter());

        GridItemDecoration gridItemDecoration = new GridItemDecoration(GridLayoutManager.VERTICAL);
        gridItemDecoration.setColor(Color.parseColor("#E0E0E0"));
        root.addItemDecoration(gridItemDecoration);

        return root;
    }
}