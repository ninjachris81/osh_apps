package com.osh.utils;

public interface IObservableListenerHolder<ITEM_TYPE> {

    IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener);

    IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener, boolean fireOnConnect);

    IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener, boolean fireOnConnect, IObservableGuard guard);

    void removeItemChangeListener(IItemChangeListener<ITEM_TYPE> listener);

}
