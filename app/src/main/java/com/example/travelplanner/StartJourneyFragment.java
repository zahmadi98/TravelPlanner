package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.ArrayList;

public class StartJourneyFragment extends Fragment {

    public StartJourneyFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_journey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ریشه صفحه هوم برای بستن پاپ‌آپ
        View background = requireActivity().findViewById(R.id.root_home);
        background.setOnClickListener(v -> {
            background.setVisibility(View.GONE);
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // دکمه انتخاب مکان
        Button btnEditDestination = view.findViewById(R.id.btnEditDestination);
        btnEditDestination.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MapSelectFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // لیست کارها (To-Do)
        EditText toDoList = view.findViewById(R.id.toDoList);
        Button btnToDoList = view.findViewById(R.id.btnToDoList);
        LinearLayout itemsContainer = view.findViewById(R.id.itemsContainer);

        btnToDoList.setOnClickListener(v -> {
            String itemText = toDoList.getText().toString().trim();
            if (!itemText.isEmpty()) {
                LinearLayout itemLayout = createToDoItem(itemText, itemsContainer);
                itemsContainer.addView(itemLayout);
                toDoList.setText("");
            }
        });

        EditText editTextTripName = view.findViewById(R.id.nameTravel);
        EditText editTextTripDescription = view.findViewById(R.id.editDescription);
        EditText editTextPeopleCount = view.findViewById(R.id.editPeopleCount);
        EditText editTextDestination = view.findViewById(R.id.editDestination);

        getParentFragmentManager().setFragmentResult("hideBackground", new Bundle());

        // دریافت مکان انتخاب شده از MapSelectFragment
        getParentFragmentManager().setFragmentResultListener("locationKey", this, (requestKey, bundle) -> {
            String location = bundle.getString("location");
            editTextDestination.setText(location);
        });
        Button btnSubmitTrip = view.findViewById(R.id.btnSubmitTrip);

        btnSubmitTrip.setOnClickListener(v -> {
            String tripName = editTextTripName.getText().toString().trim();
            String tripDescription = editTextTripDescription.getText().toString().trim();
            int tripPeopleCount = Integer.parseInt(editTextPeopleCount.getText().toString().trim());
            String tripDestination = editTextDestination.getText().toString().trim();

            // ✅ گرفتن آیتم‌های لیست کارها (to-do)
            ArrayList<String> toDoItems = new ArrayList<>();
            for (int i = 0; i < itemsContainer.getChildCount(); i++) {
                LinearLayout itemLayout = (LinearLayout) itemsContainer.getChildAt(i);
                EditText textView = (EditText) itemLayout.getChildAt(0); // متن هر آیتم
                toDoItems.add(textView.getText().toString().trim());
            }

            // ذخیره در SharedPreferences
            SharedPreferences prefs = requireContext().getSharedPreferences("travel_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson(); // برای تبدیل لیست به JSON
            String json = gson.toJson(toDoItems);
            editor.putString("equipmentList_" + tripName, json); // کلید اختصاصی بر اساس نام سفر
            editor.apply();

            Bundle result = new Bundle();
            result.putString("tripName", tripName);
            result.putString("tripDescription", tripDescription);
            result.putInt("tripPeopleCount", tripPeopleCount);
            result.putString("tripDestination", tripDestination);
            result.putStringArrayList("equipmentList", toDoItems);
            getParentFragmentManager().setFragmentResult("newTrip", result);
            getParentFragmentManager().setFragmentResult("equipmentTrip", result);

            View popupRoot = getActivity().findViewById(R.id.root_home);
            if (popupRoot != null) {
                popupRoot.setVisibility(View.GONE);
            }
            getParentFragmentManager().popBackStack();
        });
    }


    // متد ساخت آیتم To-Do
    private LinearLayout createToDoItem(String text, LinearLayout parent) {
        LinearLayout itemLayout = new LinearLayout(requireContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(Gravity.CENTER_VERTICAL);
        itemLayout.setPadding(8, 8, 8, 8);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        EditText textView = new EditText(requireContext());
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        textView.setLayoutParams(textParams);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setEnabled(false);

        ImageView deleteIcon = new ImageView(requireContext());
        deleteIcon.setImageResource(R.drawable.baseline_delete_24);
        deleteIcon.setPadding(16, 0, 0, 0);
        deleteIcon.setOnClickListener(v -> parent.removeView(itemLayout));

        itemLayout.addView(textView);
        itemLayout.addView(deleteIcon);

        return itemLayout;
    }
}