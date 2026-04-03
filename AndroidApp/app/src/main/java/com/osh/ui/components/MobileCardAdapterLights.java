package com.osh.ui.components;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.osh.R;
import com.osh.core.actor.ActorBase;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.ToggleActor;
import com.osh.core.service.IServiceContext;

import java.util.ArrayList;
import java.util.List;

public class MobileCardAdapterLights extends RecyclerView.Adapter<MobileCardAdapterLights.MyViewHolder> {

    private final IServiceContext serviceContext;
    private final List<ToggleActor> actorList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MaterialButton button;
        public MyViewHolder(View v) {
            super(v);
            button = v.findViewById(R.id.materialButton);
        }
    }

    public MobileCardAdapterLights(IServiceContext serviceContext, List<ActorBase> actorList) {
        this.serviceContext = serviceContext;
        this.actorList = new ArrayList<>();
        for (ActorBase actor : actorList) {
            this.actorList.add((ToggleActor) actor);
        }

    }

    @Override
    public MobileCardAdapterLights.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mobile_card_view_lights, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.button.setText(actorList.get(position).getKnownRoom().getFullName());
        holder.button.setOnClickListener(view -> {
            serviceContext.getActorService().publishCmd(actorList.get(position), ActorCmds.ACTOR_CMD_TOGGLE);
        });

        actorList.get(position).addItemChangeListener(item -> {
            holder.button.setBackgroundColor(item.getValue() ? Color.YELLOW : Color.GRAY);
        }, true, () -> {return true;});

    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }
}
