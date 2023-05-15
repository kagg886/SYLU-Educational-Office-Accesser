package com.qlstudio.lite_kagg886.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private int mDividerHeight = 2;
    private Paint mPaint;
    private int mOrientation;

    public GridItemDecoration(@RecyclerView.Orientation int orientation) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xFFFF0000);
        mOrientation = orientation;
    }

    private void setColor(Color color) {
        mPaint.setColor(color.toArgb());
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int itemCount = parent.getAdapter().getItemCount();
        int viewLayoutPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

        int left = 0;
        int top = 0;
        int right = mDividerHeight;
        int bottom = mDividerHeight;
//        if (isLastRow(layoutManager, itemCount, viewLayoutPosition)) {
//            // 如果是最后一行，则不需要绘制底部
//            bottom = 0;
//        }
//        if (isLastCol(layoutManager, itemCount, viewLayoutPosition)) {
//            // 如果是最后一列，则不需要绘制右边
//            right = 0;
//        }

        outRect.set(left, top, right, bottom);
    }

    /**
     * 判断是否最后一列
     */
    private boolean isLastCol(GridLayoutManager layoutManager, int childCount, int itemPosition) {
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int spanIndex = spanSizeLookup.getSpanIndex(itemPosition, spanCount);
        int spanSize = spanSizeLookup.getSpanSize(itemPosition);

        if (mOrientation == GridLayoutManager.VERTICAL) {
            return spanIndex + spanSize == spanCount;
        } else {
            return (childCount - itemPosition) / (spanCount * 1.0f) <= 1;
        }
    }

    /**
     * 判断是否最后一行
     */
    private boolean isLastRow(GridLayoutManager layoutManager, int childCount, int itemPosition) {
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int spanIndex = spanSizeLookup.getSpanIndex(itemPosition, spanCount);
        int spanSize = spanSizeLookup.getSpanSize(itemPosition);

        if (mOrientation == GridLayoutManager.VERTICAL) {
            return (childCount - itemPosition) / (spanCount * 1.0f) <= 1;
        } else {
            return spanIndex + spanSize == spanCount;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        c.save();
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            if (i <= ((GridLayoutManager) Objects.requireNonNull(parent.getLayoutManager())).getSpanCount()) {
                c.drawLine(childAt.getX(), 0, childAt.getX() + childAt.getWidth(), 0, mPaint);
                c.drawLine(childAt.getX(), 1, childAt.getX() + childAt.getWidth(), 1, mPaint);
            }
            drawHorizontal(c, childAt);
            drawVertical(c, childAt);
        }
        c.restore();
    }

    public void drawHorizontal(Canvas c, View childAt) {
        int left = childAt.getLeft();
        int right = childAt.getRight();
        int top = childAt.getBottom();
        int bottom = childAt.getBottom() + mDividerHeight;
        c.drawRect(left, top, right, bottom, mPaint);
    }

    public void drawVertical(Canvas c, View childAt) {
        int left = childAt.getRight();
        int right = left + mDividerHeight;
        int top = childAt.getTop();
        int bottom = childAt.getBottom() + mDividerHeight;
        c.drawRect(left, top, right, bottom, mPaint);
    }
}
