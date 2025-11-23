package com.osh.core.utils;

public interface IObservableManager<ITEM_TYPE> {

    void invokeListeners(ITEM_TYPE changedItem);
}
