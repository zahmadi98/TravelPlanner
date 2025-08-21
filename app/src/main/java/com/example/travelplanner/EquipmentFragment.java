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

        // دریافت اطلاعات سفر
        getParentFragmentManager()
                .setFragmentResultListener("equipmentTrip", this, (requestKey, bundle) -> {
                    String tripName = bundle.getString("tripName");

                    // مخفی کردن emptyView
                    View emptyView = view.findViewById(R.id.emptyView);
                    emptyView.setVisibility(View.GONE);

                    equipmentContainer.setVisibility(View.VISIBLE);

                    // نمایش نام سفر
                    TextView title = new TextView(requireContext());
                    title.setTextSize(18);
                    title.setText(tripName);
                    title.setPadding(8, 16, 8, 8);
                    equipmentContainer.addView(title);

                    // گرفتن لیست از SharedPreferences
                    SharedPreferences prefs = requireContext().getSharedPreferences("travel_prefs", Context.MODE_PRIVATE);
                    String json = prefs.getString("equipmentList_" + tripName, null);
                    ArrayList<String> equipmentList = new ArrayList<>();
                    if (json != null) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<String>>(){}.getType();
                        equipmentList = gson.fromJson(json, type);
                    }

                    // نمایش آیتم‌ها با todo_item.xml
                    LayoutInflater inflater = LayoutInflater.from(requireContext());
                    for (String itemText : equipmentList) {
                        View itemView = inflater.inflate(R.layout.todo_item, equipmentContainer, false);

                        CheckBox checkBox = itemView.findViewById(R.id.todoCheckBox);
                        TextView textView = itemView.findViewById(R.id.todoText);
                        ImageView deleteIcon = itemView.findViewById(R.id.todoDeleteIcon);

                        textView.setText(itemText);

                        deleteIcon.setOnClickListener(v -> equipmentContainer.removeView(itemView));

                        equipmentContainer.addView(itemView);
                    }
                });


    }
}