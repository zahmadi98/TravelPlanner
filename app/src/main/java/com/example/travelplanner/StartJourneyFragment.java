package com.example.travelplanner;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StartJourneyFragment extends Fragment {

    public StartJourneyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_journey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View background = requireActivity().findViewById(R.id.root_home);
        background.setOnClickListener(v -> {
            background.setVisibility(View.GONE);
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        Button btnSetAlarm = view.findViewById(R.id.btnSetAlarm);

        btnSetAlarm.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MapSelectFragment())
                    .addToBackStack(null)
                    .commit();
        });

        EditText toDoList = view.findViewById(R.id.toDoList);
        Button btnToDoList = view.findViewById(R.id.btnToDoList);
        LinearLayout itemsContainer = view.findViewById(R.id.itemsContainer);

        btnToDoList.setOnClickListener(v -> {
            String itemText = toDoList.getText().toString().trim();


            if (!itemText.isEmpty()) {
                // ساخت لایه افقی برای آیتم
                LinearLayout itemLayout = new LinearLayout(requireContext());
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setGravity(Gravity.CENTER_VERTICAL);
                itemLayout.setPadding(8, 8, 8, 8);
                itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                // متن وسیله
                EditText textView = new EditText(requireContext());
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                );
                textView.setLayoutParams(textParams);
                textView.setText(itemText);
                textView.setTextSize(16);
                textView.setEnabled(false); // فقط نمایش، قابل ویرایش نباشد

                // دکمه حذف
                android.widget.ImageView deleteIcon = new android.widget.ImageView(requireContext());
                deleteIcon.setImageResource(R.drawable.baseline_delete_24);
                deleteIcon.setPadding(16, 0, 0, 0);

                deleteIcon.setOnClickListener(view1 -> {
                    itemsContainer.removeView(itemLayout);
                });

                // اضافه کردن اجزا به آیتم
                itemLayout.addView(textView);
                itemLayout.addView(deleteIcon);

                // اضافه کردن آیتم به لیست
                itemsContainer.addView(itemLayout);

                // پاک کردن ورودی
                toDoList.setText("");
            }
        });

        Button btnSubmitTrip = view.findViewById(R.id.btnSubmitTrip);
        EditText editTextTripName = view.findViewById(R.id.nameTravel);
        EditText editTextTripDescription = view.findViewById(R.id.editDescription);
        EditText editTextPeopleCount = view.findViewById(R.id.editPeopleCount);
        EditText editTextDestination = view.findViewById(R.id.showAlarm);

        btnSubmitTrip.setOnClickListener(v -> {
            String tripName = editTextTripName.getText().toString().trim();
            String tripDescription = editTextTripDescription.getText().toString().trim();
            String peopleCountStr = editTextPeopleCount.getText().toString().trim();
            String tripDestination = editTextDestination.getText().toString().trim();

            if (tripName.isEmpty() || tripDescription.isEmpty() || peopleCountStr.isEmpty() || tripDestination.isEmpty()) {
                Toast.makeText(getContext(), "لطفا تمام فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            int peopleCount;
            try {
                peopleCount = Integer.parseInt(peopleCountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "تعداد نفرات باید عدد باشد", Toast.LENGTH_SHORT).show();
                return;
            }

            // ساخت Bundle برای ارسال داده به HomeFragment
            Bundle result = new Bundle();
            result.putString("tripName", tripName);
            result.putString("tripDescription", tripDescription);
            result.putInt("tripPeopleCount", peopleCount);
            result.putString("tripDestination", tripDestination);

            // ارسال نتیجه
            getParentFragmentManager().setFragmentResult("newTrip", result);

            background.setVisibility(View.GONE);

            requireActivity().getSupportFragmentManager().popBackStack();
        });
        // گرفتن نتیجه انتخاب مکان
        getParentFragmentManager().setFragmentResultListener("locationKey", this, (requestKey, bundle) -> {
            String location = bundle.getString("location");
            EditText editText = view.findViewById(R.id.showAlarm);
            editText.setText(location);
        });
    }
}