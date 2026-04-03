package com.osh.ui.components;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.osh.R;
import com.osh.core.datamodel.meta.KnownArea;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.ValueType;

public abstract class MobileAreaExpandableListAdapter extends BaseExpandableListAdapter {

    protected final Context context;
    protected final IServiceContext serviceContext;
    protected final ValueType valueType;
    protected final DisplayMetrics displayMetrics;
    private final int columnWidthDp;

    abstract void setupAdapter(RecyclerView cardRecyclerView, KnownArea knownArea);

    public MobileAreaExpandableListAdapter(Context context, IServiceContext serviceContext, ValueType valueType, DisplayMetrics displayMetrics, int columnWidthDp) {
        this.context = context;
        this.serviceContext = serviceContext;
        this.valueType = valueType;
        this.displayMetrics = displayMetrics;
        this.columnWidthDp = columnWidthDp;
    }

    @Override
    public int getGroupCount() {
        return serviceContext.getDatamodelService().getDatamodel().getKnownAreas().size();
    }

    @Override
    public int getChildrenCount(int areaIndex) {
        KnownArea area = serviceContext.getDatamodelService().getDatamodel().getKnownAreas().get(areaIndex);
        return serviceContext.getActorService().getActors(area, valueType).isEmpty() ? 0 : 1;
    }

    @Override
    public Object getGroup(int areaIndex) {
        return serviceContext.getDatamodelService().getDatamodel().getKnownAreas().get(areaIndex);
    }

    @Override
    public Object getChild(int areaIndex, int childIndex) {
        KnownArea area = serviceContext.getDatamodelService().getDatamodel().getKnownAreas().get(areaIndex);
        return serviceContext.getActorService().getActors(area, valueType).get(childIndex);
    }

    @Override
    public View getGroupView(int listPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final KnownArea knownArea = (KnownArea) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.simple_listview_item, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.label);
        expandedListTextView.setText(knownArea.getName());
        return convertView;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final KnownArea knownArea = (KnownArea) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.area_card_view, null);
        }
        RecyclerView cardRecyclerView = convertView.findViewById(R.id.cardRecyclerView);
        cardRecyclerView.setLayoutManager(new AutoFitGridLayoutManager(context, displayMetrics, columnWidthDp));

        setupAdapter(cardRecyclerView, knownArea);

        return convertView;
    }

    @Override
    public long getGroupId(int areaIndex) {
        return getGroup(areaIndex).hashCode();
    }

    @Override
    public long getChildId(int areaIndex, int childIndex) {
        return getChild(areaIndex, childIndex).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
