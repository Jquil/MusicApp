package com.jqwong.music.view.control;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: Jq
 * @date: 8/5/2023
 */
public class RecyclerViewX extends RecyclerView {
    public RecyclerViewX(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewX(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewX(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return 1f;
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 1f;
    }


}
