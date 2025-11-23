package com.osh.ui.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.osh.R;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.ToggleActor;
import com.osh.core.service.IServiceContext;

import java.util.List;

public class MobileCardAdapter extends RecyclerView.Adapter<MobileCardAdapter.MyViewHolder> {

    private final IServiceContext serviceContext;
    private final List<ToggleActor> actorList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MaterialButton button;
        public MyViewHolder(View v) {
            super(v);
            button = v.findViewById(R.id.materialButton);
        }
    }

    public MobileCardAdapter(IServiceContext serviceContext, List<ToggleActor> actorList) {
        this.serviceContext = serviceContext;
        this.actorList = actorList;
    }

    @Override
    public MobileCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mobile_card_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.button.setText(actorList.get(position).getKnownRoom().getFullName());
        holder.button.setOnClickListener(view -> {
            serviceContext.getActorService().publishCmd(actorList.get(position), ActorCmds.ACTOR_CMD_TOGGLE);
        });
    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }
}
