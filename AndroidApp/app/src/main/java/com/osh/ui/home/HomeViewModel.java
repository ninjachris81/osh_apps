package com.osh.ui.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.number.LocalizedNumberFormatter;
import android.icu.number.Notation;
import android.icu.number.NumberFormatter;
import android.icu.number.Precision;
import android.icu.util.MeasureUnit;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.osh.R;
import com.osh.core.actor.DigitalActor;
import com.osh.core.log.LogFacade;
import com.osh.core.service.IServiceContext;
import com.osh.core.utils.OshValueFormats;
import com.osh.core.value.BooleanValue;
import com.osh.core.value.DoubleValue;
import com.osh.core.value.EnumValue;
import com.osh.core.value.IntegerValue;
import com.osh.core.value.StringValue;
import com.osh.core.value.enums.EnergyMode;
import com.osh.core.value.enums.EnergyTrend;
import com.osh.core.value.enums.HomeConnectOperationalState;
import com.osh.wbb12.WBB12_Enums;
import com.osh.wbb12.WBB12_Input_Registers;
import com.osh.wbb12.service.IWBB12Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HomeViewModel extends HomeViewModelBase {


    public HomeViewModel(Context context, IServiceContext serviceContext, IWBB12Service wbb12Service, IBatteryDataChangeListener batteryDataChangeListener) {
        super(context, serviceContext, wbb12Service, batteryDataChangeListener);

        setupCommon();
        setupBattery(context);
        setupCurrentMode();
        setupHomeAppliances();
        setupConsumption();
        setupTrend();
        setupWeather();
        setupWindowState();
        setupWbb12();
        setupMusic();
        setupWater();
        setupExceed();

    }
}