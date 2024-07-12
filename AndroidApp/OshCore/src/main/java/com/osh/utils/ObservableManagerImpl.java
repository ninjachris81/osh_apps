package com.osh.utils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ObservableManagerImpl<ITEM_TYPE> implements IObservableManager<ITEM_TYPE>, IObservableListenerHolder<ITEM_TYPE> {

    private final List<IItemChangeListener<ITEM_TYPE>> listeners = Collections.synchronizedList(new ArrayList<>());
    private final Map<IItemChangeListener<ITEM_TYPE>, IObservableGuard> guards = Collections.synchronizedMap(new HashMap<>());

    @Override
    public IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener) {
        listeners.add(listener);
        return listener;
    }

    @Override
    public IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener, boolean fireOnConnect) {
        return addItemChangeListener(listener);
    }

    @Override
    public IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener, boolean fireOnConnect, IObservableGuard guard) {
        guards.put(listener, guard);
        return addItemChangeListener(listener, fireOnConnect);
    }

    @Override
    public void removeItemChangeListener(IItemChangeListener<ITEM_TYPE> listener) {
        listeners.remove(listener);
    }

    @Override
    synchronized public void invokeListeners(ITEM_TYPE item) {
        Iterator<IItemChangeListener<ITEM_TYPE>> it = listeners.iterator();
        while(it.hasNext()) {
            IItemChangeListener<ITEM_TYPE> listener = it.next();

            // check guard
            if (guards.containsKey(listener) && !guards.get(listener).check()) {
                it.remove();
            } else {
                listener.onItemChanged(item);
            }
        }
    }
}
