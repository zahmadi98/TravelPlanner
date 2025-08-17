package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlarmsFragment extends Fragment {
    private LinearLayout alarmListContainer;
    private View emptyView;
    private List<Alarm> alarmList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarmListContainer = view.findViewById(R.id.alarmListContainer);
        emptyView = view.findViewById(R.id.emptyView);

        alarmList = loadAlarms();
        refreshAlarmList();

        // گوش دادن به آلارم جدید
        getParentFragmentManager().setFragmentResultListener("newAlarm", this, (key, bundle) -> {
            alarmList = loadAlarms();
            refreshAlarmList();
        });
    }

    private void refreshAlarmList() {
        alarmListContainer.removeAllViews();

        if(alarmList.isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
            alarmListContainer.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            alarmListContainer.setVisibility(View.VISIBLE);

            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (int i = 0; i < alarmList.size(); i++) {
                Alarm alarm = alarmList.get(i);

                View alarmView = inflater.inflate(R.layout.alarm_item, alarmListContainer, false);

                TextView tripName = alarmView.findViewById(R.id.tripName);
                TextView startDate = alarmView.findViewById(R.id.startDate);
                TextView finishDate = alarmView.findViewById(R.id.finishDate);
                TextView alarmTime = alarmView.findViewById(R.id.timeEditText);
                View btnDelete = alarmView.findViewById(R.id.btnDelete);

                tripName.setText(alarm.getTripName());
                startDate.setText("شروع: " + alarm.getStartDate());
                finishDate.setText("پایان: " + alarm.getFinishDate());
                alarmTime.setText("ساعت: " + alarm.getAlarmTime());

                // 🚮 حذف آلارم
                int index = i; // اندیس آلارم برای حذف
                btnDelete.setOnClickListener(v -> {
                    alarmList.remove(index);
                    saveAlarms(alarmList);
                    refreshAlarmList();
                });

                alarmListContainer.addView(alarmView);
            }

        }
    }
    private void saveAlarms(List<Alarm> alarms){
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAlarms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString("alarm_list", gson.toJson(alarms));
        editor.apply();
    }


    private List<Alarm> loadAlarms(){
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAlarms", Context.MODE_PRIVATE);
        String json = prefs.getString("alarm_list", null);
        if(json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Alarm>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
