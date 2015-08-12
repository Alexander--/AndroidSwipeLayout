package com.daimajia.swipe.interfaces;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface SwipeItemMangerInterface {

    public void openItem(long id);

    public void closeItem(long id);

    public void closeAllExcept(SwipeLayout layout);
    
    public void closeAllItems();

    /**
     * Returns an actual collection instance, used for holding open items.
     * (useful for saving/restoring ListAdapter state)
     */
    public <T extends Collection<Long> & Serializable> T getOpenItems();

    public List<SwipeLayout> getOpenLayouts();

    public void removeShownLayouts(SwipeLayout layout);

    public boolean isOpen(long id);

    public Attributes.Mode getMode();

    public void setMode(Attributes.Mode mode);
}
