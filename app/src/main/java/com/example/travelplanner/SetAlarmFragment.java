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

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SetAlarmFragment extends Fragment {

    private MaterialAutoCompleteTextView choseTrip;
    private List<HomeFragment.Trip> tripList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        choseTrip = view.findViewById(R.id.choseTrip);

        // خواندن سفرها از SharedPreferences
        loadTrips();

        // تنظیم adapter با dropdown style
        setupTripDropdown();

        // نمایش dropdown هنگام کلیک روی فیلد
        choseTrip.setOnClickListener(v -> choseTrip.showDropDown());
        choseTrip.setOnItemClickListener((parent, itemView, position, id) -> {
            String selectedTrip = (String) parent.getItemAtPosition(position);
            // می‌توانید اینجا selectedTrip را ذخیره یا استفاده کنید
        });

        View background = requireActivity().findViewById(R.id.root_home);
        background.setOnClickListener(v -> {
            background.setVisibility(View.GONE);
            requireActivity().getSupportFragmentManager().popBackStack();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                R.layout.dropdown_item, R.id.dropdownItemText, tripNames) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                // می‌توان ظاهر آیتم‌ها را اینجا تغییر داد
                return view;
            }
        };

        choseTrip.setAdapter(adapter);
        choseTrip.setText("", false);
    }
}
