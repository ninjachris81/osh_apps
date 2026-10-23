package com.osh.ui.components;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.osh.R;
import com.osh.core.actor.ActorBase;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.ShutterActor;
import com.osh.core.actor.ToggleActor;
import com.osh.core.log.LogFacade;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.EnumValue;
import com.osh.core.value.ValueBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MobileCardAdapterShutters extends RecyclerView.Adapter<MobileCardAdapterShutters.MyViewHolder> {

    private final IServiceContext serviceContext;
    private final List<ShutterActor> actorList;
    private final List<EnumValue> shutterAutoModes;

    protected TimerTask shutterRepeatTimerTask;
    protected Timer shutterRepeatTimer = new Timer();

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ShutterModeButton button;
        public TextView label;
        public TextView status;

        public MyViewHolder(View v) {
            super(v);
            button = v.findViewById(R.id.shutterModeButton);
            label = v.findViewById(R.id.shutterModeLabel);
            status = v.findViewById(R.id.shutterStatus);
        }
    }

    public MobileCardAdapterShutters(IServiceContext serviceContext, List<ActorBase> actorList, List<ValueBase> shutterModeList) {
        this.serviceContext = serviceContext;
        this.actorList = new ArrayList<>();

        assert actorList.size() == shutterModeList.size();

        for (ActorBase actor : actorList) {
            this.actorList.add((ShutterActor) actor);
        }
        this.shutterAutoModes = new ArrayList<>();
        for (ValueBase value : shutterModeList) {
            this.shutterAutoModes.add((EnumValue) value);
        }
    }

    void scheduleNewTimerTask(TimerTask timerTask) {
        if (shutterRepeatTimerTask != null) shutterRepeatTimerTask.cancel();
        shutterRepeatTimerTask = timerTask;
        shutterRepeatTimer.scheduleAtFixedRate(shutterRepeatTimerTask, 0, 500);
    }

    @Override
    public MobileCardAdapterShutters.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mobile_card_view_shutters, parent, false);
        return new MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ShutterActor shutterActor = actorList.get(position);
        EnumValue shutterMode = shutterAutoModes.get(position);

        holder.label.setText(shutterActor.getKnownRoom().getFullName());
        holder.button.setAutoClickListener(view -> {
            shutterMode.updateValue(shutterMode.getValue(ShutterActor.SHUTTER_OPERATION_MODE_AUTO) == ShutterActor.SHUTTER_OPERATION_MODE_AUTO ? ShutterActor.SHUTTER_OPERATION_MODE_MANUAL : ShutterActor.SHUTTER_OPERATION_MODE_AUTO);
            serviceContext.getValueService().publishValue(shutterMode);
        });

        holder.button.setUpClickListener(new ShutterModeButton.OnClickListener() {
            @Override
            public void onTap(View view) {
                shutterMode.updateValue(ShutterActor.SHUTTER_OPERATION_MODE_MANUAL);
                serviceContext.getValueService().publishValue(shutterMode);
                serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_UP);
            }

            @Override
            public void onLongClick(View view, boolean release) {
                if (release) {
                    if (shutterRepeatTimerTask != null) shutterRepeatTimerTask.cancel();
                    serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_STOP);
                } else {
                    scheduleNewTimerTask(new TimerTask() {
                        @Override
                        public void run() {
                            serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_SHUTTER_MANUAL_UP);
                        }
                    });
                }
            }
        });

        holder.button.setDownClickListener(new ShutterModeButton.OnClickListener() {
            @Override
            public void onTap(View view) {
                shutterMode.updateValue(ShutterActor.SHUTTER_OPERATION_MODE_MANUAL);
                serviceContext.getValueService().publishValue(shutterMode);
                serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_DOWN);
            }

            @Override
            public void onLongClick(View view, boolean release) {
                if (release) {
                    if (shutterRepeatTimerTask != null) shutterRepeatTimerTask.cancel();
                    serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_STOP);
                } else {
                    scheduleNewTimerTask(new TimerTask() {
                        @Override
                        public void run() {
                            serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_SHUTTER_MANUAL_DOWN);
                        }
                    });
                }
            }
        });



        shutterActor.addItemChangeListener(item -> {
            LogFacade.i("MobileCardAdapterShutters", item.getValueAsString());
            holder.status.setText(item.getCloseAndTiltState());
        }, true, () -> {return true;});

        shutterMode.addItemChangeListener(item -> {
            holder.button.setAuto(item.getValue(0) == ShutterActor.SHUTTER_OPERATION_MODE_AUTO);
        }, true, () -> {return true;});

    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }
}
