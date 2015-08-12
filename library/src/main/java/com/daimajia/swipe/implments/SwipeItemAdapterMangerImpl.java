package com.daimajia.swipe.implments;

import android.view.View;
import android.widget.BaseAdapter;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SwipeItemMangerImpl is a helper class to help all the adapters to maintain open status.
 */
public class SwipeItemAdapterMangerImpl extends SwipeItemMangerImpl{

    protected BaseAdapter mAdapter;

    public SwipeItemAdapterMangerImpl(BaseAdapter adapter) {
        super(adapter);
        this.mAdapter = adapter;
    }

    @Override
    public void initialize(View target, int position) {
        long id = mAdapter.getItemId(position);

        int resId = getSwipeLayoutId(position);

        OnLayoutListener onLayoutListener = new OnLayoutListener(id);
        SwipeLayout swipeLayout = (SwipeLayout) target.findViewById(resId);
        if (swipeLayout == null)
            throw new IllegalStateException("can not find SwipeLayout in target view");

        SwipeMemory swipeMemory = new SwipeMemory(id);
        swipeLayout.addSwipeListener(swipeMemory);
        swipeLayout.setTag(resId, new ValueBox(id, swipeMemory, onLayoutListener));
        swipeLayout.addOnLayoutListener(onLayoutListener);

        mShownLayouts.add(swipeLayout);
    }

    @Override
    public void updateConvertView(View target, int position) {
        long id = mAdapter.getItemId(position);

        int resId = getSwipeLayoutId(position);

        SwipeLayout swipeLayout = (SwipeLayout) target.findViewById(resId);
        if (swipeLayout == null)
            throw new IllegalStateException("can not find SwipeLayout in target view");

        ValueBox valueBox = (ValueBox) swipeLayout.getTag(resId);

        boolean needLayout = target != swipeLayout && isOpen(valueBox.id) != isOpen(id);

        valueBox.swipeMemory.setPosition(id);
        valueBox.onLayoutListener.setPosition(id);
        valueBox.id = id;

        swipeLayout.invalidate();
        if (needLayout) swipeLayout.requestLayout();
    }

    @Override
    public void bindView(View target, int position){

    }

}
