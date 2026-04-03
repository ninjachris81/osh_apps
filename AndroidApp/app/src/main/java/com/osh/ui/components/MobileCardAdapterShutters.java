package com.osh.ui.components;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.osh.R;
import com.osh.core.actor.ActorBase;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.ShutterActor;
import com.osh.core.actor.ToggleActor;
import com.osh.core.log.LogFacade;
import com.osh.core.service.IServiceContext;

import java.util.ArrayList;
import java.util.List;

public class MobileCardAdapterShutters extends RecyclerView.Adapter<MobileCardAdapterShutters.MyViewHolder> {

    private final IServiceContext serviceContext;
    private final List<ShutterActor> actorList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ShutterModeButton button;
        public TextView label;

        public MyViewHolder(View v) {
            super(v);
            button = v.findViewById(R.id.shutterModeButton);
            label = v.findViewById(R.id.shutterModeLabel);
        }
    }

    public MobileCardAdapterShutters(IServiceContext serviceContext, List<ActorBase> actorList) {
        this.serviceContext = serviceContext;
        this.actorList = new ArrayList<>();
        for (ActorBase actor : actorList) {
            this.actorList.add((ShutterActor) actor);
        }

    }

    @Override
    public MobileCardAdapterShutters.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mobile_card_view_shutters, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.label.setText(actorList.get(position).getKnownRoom().getFullName());
        //holder.button.setUpClickListener();

        actorList.get(position).addItemChangeListener(item -> {
            LogFacade.i("MobileCardAdapterShutters", item.getValueAsString());
        }, true, () -> {return true;});

    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }
}
