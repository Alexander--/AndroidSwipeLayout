package com.daimajia.swipe.implments;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.View;
import android.widget.BaseAdapter;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * SwipeItemMangerImpl is a helper class to help all the adapters to maintain open status.
 */
public abstract class SwipeItemMangerImpl implements SwipeItemMangerInterface {

    private Attributes.Mode mode = Attributes.Mode.Single;

    protected HashSet<Long> mOpenPositions = new HashSet<>();
    protected Set<SwipeLayout> mShownLayouts = Collections.newSetFromMap(new WeakHashMap<SwipeLayout, Boolean>());

    protected BaseAdapter mBaseAdapter;
    protected RecyclerView.Adapter mRecyclerAdapter;

    public SwipeItemMangerImpl(BaseAdapter adapter) {
        if (adapter == null)
            throw new IllegalArgumentException("Adapter can not be null");

        if (!(adapter instanceof SwipeItemMangerInterface))
            throw new IllegalArgumentException("adapter should implement the SwipeAdapterInterface");

        this.mBaseAdapter = adapter;
    }

    public SwipeItemMangerImpl(RecyclerView.Adapter adapter) {
        if (adapter == null)
            throw new IllegalArgumentException("Adapter can not be null");

        if (!(adapter instanceof SwipeItemMangerInterface))
            throw new IllegalArgumentException("adapter should implement the SwipeAdapterInterface");

        this.mRecyclerAdapter = adapter;
    }

    public Attributes.Mode getMode() {
        return mode;
    }

    public void setMode(Attributes.Mode mode) {
        this.mode = mode;
        mOpenPositions.clear();
        mShownLayouts.clear();
    }

    /* initialize and updateConvertView used for AdapterManagerImpl */
    public abstract void initialize(View target, int position);

    public abstract void updateConvertView(View target, int position);

    /* bindView used for RecyclerViewManagerImpl */
    public abstract void bindView(View target, int position);

    public int getSwipeLayoutId(int position) {
        if (mBaseAdapter != null) {
            return ((SwipeAdapterInterface) (mBaseAdapter)).getSwipeLayoutResourceId(position);
        } else if (mRecyclerAdapter != null) {
            return ((SwipeAdapterInterface) (mRecyclerAdapter)).getSwipeLayoutResourceId(position);
        } else {
            return -1;
        }
    }

    @Override
    public void openItem(long id) {
        if (!mOpenPositions.contains(id))
            mOpenPositions.add(id);

        if (mBaseAdapter != null) {
            mBaseAdapter.notifyDataSetChanged();
        } else if (mRecyclerAdapter != null) {
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void closeItem(long id) {
        mOpenPositions.remove(id);

        if (mBaseAdapter != null) {
            mBaseAdapter.notifyDataSetChanged();
        } else if (mRecyclerAdapter != null) {
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        for (SwipeLayout s : mShownLayouts) {
            if (s != layout)
                s.close();
        }
    }

    @Override
    public void closeAllItems() {
        mOpenPositions.clear();

        for (SwipeLayout s : mShownLayouts) {
            s.close();
        }
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mShownLayouts.remove(layout);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Collection<Long> & Serializable> T getOpenItems() {
        return (T) mOpenPositions;
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return new ArrayList<>(mShownLayouts);
    }

    @Override
    public boolean isOpen(long id) {
        return mOpenPositions.contains(id);
    }

    class ValueBox {
        OnLayoutListener onLayoutListener;
        SwipeMemory swipeMemory;
        long id;

        ValueBox(long id, SwipeMemory swipeMemory, OnLayoutListener onLayoutListener) {
            this.swipeMemory = swipeMemory;
            this.onLayoutListener = onLayoutListener;
            this.id = id;
        }
    }

    class OnLayoutListener implements SwipeLayout.OnLayout {

        private long id;

        OnLayoutListener(long id) {
            this.id = id;
        }

        public void setPosition(long id) {
            this.id = id;
        }

        @Override
        public void onLayout(SwipeLayout v) {
            if (isOpen(id)) {
                v.open(false, false);
            } else {
                v.close(false, false);
            }
        }

    }

    class SwipeMemory extends SimpleSwipeListener {

        private long id;

        SwipeMemory(long id) {
            this.id = id;
        }

        @Override
        public void onClose(SwipeLayout layout) {
            mOpenPositions.remove(id);
        }

        @Override
        public void onStartOpen(SwipeLayout layout) {
            if (mode == Attributes.Mode.Single) {
                closeAllExcept(layout);
            }
        }

        @Override
        public void onOpen(SwipeLayout layout) {
            if (mode == Attributes.Mode.Multiple)
                mOpenPositions.add(id);
            else {
                closeAllExcept(layout);
                mOpenPositions.add(id);
            }
        }

        public void setPosition(long id) {
            this.id = id;
        }
    }

}
