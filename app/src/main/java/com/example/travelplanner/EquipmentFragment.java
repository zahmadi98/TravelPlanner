package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EquipmentFragment extends Fragment {

    private LinearLayout equipmentContainer;
    private View emptyView;
    private SharedPreferences prefs;

    public EquipmentFragment() {
// Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equipment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        equipmentContainer = view.findViewById(R.id.equipmentListContainer);
        emptyView = view.findViewById(R.id.emptyView);
        prefs = requireContext().getSharedPreferences("travel_prefs", Context.MODE_PRIVATE);

// 📌 اگر آخرین سفر ذخیره شده‌ای داریم، دوباره بارگذاری کن
        String lastTripName = prefs.getString("last_trip", null);
        if (lastTripName != null) {
            loadEquipmentList(lastTripName);
        }

// 📌 وقتی از StartJourneyFragment نتیجه بیاد
        getParentFragmentManager().setFragmentResultListener("equipmentTrip", this, (requestKey, bundle) -> {
            String tripName = bundle.getString("tripName");

// ذخیره کن که این آخرین سفر بوده
            prefs.edit().putString("last_trip", tripName).apply();

            loadEquipmentList(tripName);
        });
    }

    /**
     * متد لود کردن لیست تجهیزات یک سفر
     */
    private void loadEquipmentList(String tripName) {
// پاک کردن ویوهای قبلی
        equipmentContainer.removeAllViews();

        emptyView.setVisibility(View.GONE);
        equipmentContainer.setVisibility(View.VISIBLE);

// نمایش نام سفر
        TextView title = new TextView(requireContext());
        title.setTextSize(18);
        title.setText(tripName);
        title.setPadding(8, 16, 8, 8);
        equipmentContainer.addView(title);

// گرفتن لیست تجهیزات از SharedPreferences
        String json = prefs.getString("equipmentList_" + tripName, null);
        ArrayList<String> equipmentList = new ArrayList<>();
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            equipmentList = gson.fromJson(json, type);
        }

// ساخت آیتم‌ها با todo_item.xml
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        ArrayList<String> finalEquipmentList = new ArrayList<>(equipmentList);

        for (String itemText : equipmentList) {
            View itemView = inflater.inflate(R.layout.todo_item, equipmentContainer, false);

            CheckBox checkBox = itemView.findViewById(R.id.todoCheckBox);
            TextView textView = itemView.findViewById(R.id.todoText);
            ImageView deleteIcon = itemView.findViewById(R.id.todoDeleteIcon);

            textView.setText(itemText);

// حذف آیتم و آپدیت SharedPreferences
            deleteIcon.setOnClickListener(v -> {
                equipmentContainer.removeView(itemView);
                finalEquipmentList.remove(itemText);

                Gson gson = new Gson();
                String updatedJson = gson.toJson(finalEquipmentList);
                prefs.edit().putString("equipmentList_" + tripName, updatedJson).apply();

// اگر لیست خالی شد، emptyView نشون بده
                if (finalEquipmentList.isEmpty()) {
                    equipmentContainer.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });

            equipmentContainer.addView(itemView);
        }
    }
}