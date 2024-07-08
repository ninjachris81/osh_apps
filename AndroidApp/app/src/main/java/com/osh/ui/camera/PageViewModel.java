package com.osh.ui.camera;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    //private LiveData<String> mText = Transformations.map();

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    /*public LiveData<String> getText() {
        return mText;
    }

     */
}