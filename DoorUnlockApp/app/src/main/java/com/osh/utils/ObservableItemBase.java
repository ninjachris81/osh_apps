package com.osh.utils;

public abstract class ObservableItemBase<ITEM_TYPE> implements IObservableListenerHolder<ITEM_TYPE>, IValueHolder<ITEM_TYPE> {

    private ObservableManagerImpl<ITEM_TYPE> listenerHolder = new ObservableManagerImpl<>();

    private ITEM_TYPE value;

    public ObservableItemBase(ITEM_TYPE initialValue) {
        this.value = initialValue;
    }

    public void changeValue(ITEM_TYPE newValue) {
        this.value = newValue;
        listenerHolder.invokeListeners(newValue);
    }
    @Override
    public IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener) {
        return listenerHolder.addItemChangeListener(listener);
    }

    @Override
    public IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener, boolean fireOnConnect) {
        IItemChangeListener<ITEM_TYPE> returnVal = listenerHolder.addItemChangeListener(listener, fireOnConnect);
        if (fireOnConnect) {
            listener.onItemChanged(value);
        }
        return returnVal;
    }

    @Override
    public IItemChangeListener<ITEM_TYPE> addItemChangeListener(IItemChangeListener<ITEM_TYPE> listener, boolean fireOnConnect, IObservableGuard guard) {
        IItemChangeListener<ITEM_TYPE> returnVal = listenerHolder.addItemChangeListener(listener, fireOnConnect, guard);
        if (fireOnConnect) {
            listener.onItemChanged(value);
        }
        return returnVal;
    }

    @Override
    public void removeItemChangeListener(IItemChangeListener<ITEM_TYPE> listener) {
        listenerHolder.removeItemChangeListener(listener);
    }

    @Override
    public ITEM_TYPE getValue() {
        return value;
    }

}
