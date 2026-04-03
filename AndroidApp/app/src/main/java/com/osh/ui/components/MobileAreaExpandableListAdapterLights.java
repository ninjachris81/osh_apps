package com.osh.ui.components;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.osh.R;
import com.osh.core.datamodel.meta.KnownArea;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.ValueType;

public class MobileAreaExpandableListAdapterLights extends MobileAreaExpandableListAdapter {

    public MobileAreaExpandableListAdapterLights(Context context, IServiceContext serviceContext, ValueType valueType, DisplayMetrics displayMetrics) {
        super(context, serviceContext, valueType, displayMetrics, 120);
    }

    @Override
    void setupAdapter(RecyclerView cardRecyclerView, KnownArea knownArea) {
        MobileCardAdapterLights adapter = new MobileCardAdapterLights(serviceContext, serviceContext.getActorService().getActors(knownArea, valueType));
        cardRecyclerView.setAdapter(adapter);
    }

}
