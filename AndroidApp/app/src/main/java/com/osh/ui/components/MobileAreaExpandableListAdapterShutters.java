package com.osh.ui.components;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.osh.R;
import com.osh.core.datamodel.meta.KnownArea;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.ValueType;

public class MobileAreaExpandableListAdapterShutters extends MobileAreaExpandableListAdapter {

    public MobileAreaExpandableListAdapterShutters(Context context, IServiceContext serviceContext, ValueType valueType, DisplayMetrics displayMetrics) {
        super(context, serviceContext, valueType, displayMetrics, 160);
    }

    @Override
    void setupAdapter(RecyclerView cardRecyclerView, KnownArea knownArea) {
        MobileCardAdapterShutters adapter = new MobileCardAdapterShutters(serviceContext, serviceContext.getActorService().getActors(knownArea, valueType), serviceContext.getValueService().getValues(knownArea, ValueType.VT_SHUTTER_MODE));
        cardRecyclerView.setAdapter(adapter);
    }

}
