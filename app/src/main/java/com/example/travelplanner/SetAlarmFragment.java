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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetAlarmFragment extends Fragment {

    private MaterialAutoCompleteTextView choseTrip;
    private List<HomeFragment.Trip> tripList = new ArrayList<>();
    private EditText startDate, finishDate, timeEditText;
    private Button btnSetTime, btnSubmitTrip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View background = requireActivity().findViewById(R.id.root_home);
        background.setOnClickListener(v -> {
            background.setVisibility(View.GONE);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        choseTrip = view.findViewById(R.id.choseTrip);
        startDate = view.findViewById(R.id.startDate);
        finishDate = view.findViewById(R.id.finishDate);
        timeEditText = view.findViewById(R.id.timeEditText);
        btnSetTime = view.findViewById(R.id.btnSetTime);
        btnSubmitTrip = view.findViewById(R.id.btnSubmitTrip);

        startDate.setOnClickListener(v -> showDatePicker(startDate));
        finishDate.setOnClickListener(v -> showDatePicker(finishDate));
        btnSetTime.setOnClickListener(v -> showMaterialTimePicker(timeEditText));

        loadTrips();
        setupTripDropdown();

        choseTrip.setOnClickListener(v -> choseTrip.showDropDown());

        btnSubmitTrip.setOnClickListener(v -> submitForm());
    }

    private void showMaterialTimePicker(final EditText targetEditText) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTheme(R.style.PurpleMaterialTimePicker)
                .setTitleText("انتخاب ساعت")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            String time = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());
            targetEditText.setText(time);
        });

        timePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    private void showDatePicker(final EditText targetEditText) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("انتخاب تاریخ")
                .setTheme(R.style.PurpleDatePickerDialog)
                .build();

        picker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String date = calendar.get(Calendar.YEAR) + "/" +
                    (calendar.get(Calendar.MONTH) + 1) + "/" +
                    calendar.get(Calendar.DAY_OF_MONTH);
            targetEditText.setText(date);
        });
    }

    private void loadTrips() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyTrips", Context.MODE_PRIVATE);
        String json = prefs.getString("trip_list", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<HomeFragment.Trip>>() {}.getType();
            tripList = gson.fromJson(json, type);
        }
    }

    private void setupTripDropdown() {
        List<String> tripNames = new ArrayList<>();
        for (HomeFragment.Trip trip : tripList) {
            tripNames.add(trip.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.dropdown_item, R.id.dropdownItemText, tripNames);
        choseTrip.setAdapter(adapter);
        choseTrip.setText("", false);
    }

    private void submitForm() {
        String tripName = choseTrip.getText().toString();
        String start = startDate.getText().toString();
        String finish = finishDate.getText().toString();
        String time = timeEditText.getText().toString();

        if(tripName.isEmpty() || start.isEmpty() || finish.isEmpty() || time.isEmpty()){
            Toast.makeText(requireContext(), "لطفاً همه فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        // ساخت آلارم جدید
        Alarm newAlarm = new Alarm(tripName, start, finish, time);

        // ذخیره در SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAlarms", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("alarm_list", null);
        Type type = new TypeToken<List<Alarm>>(){}.getType();
        List<Alarm> alarmList = json == null ? new ArrayList<>() : gson.fromJson(json, type);

        alarmList.add(newAlarm);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("alarm_list", gson.toJson(alarmList));
        editor.apply();

        // ارسال نتیجه برای AlarmsFragment
        getParentFragmentManager().setFragmentResult("newAlarm", new Bundle());

        View popupRoot = getActivity().findViewById(R.id.root_home);
        if (popupRoot != null) {
            popupRoot.setVisibility(View.GONE);
        }
        getParentFragmentManager().popBackStack();
    }

}
