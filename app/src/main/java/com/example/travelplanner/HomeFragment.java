package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private LinearLayout tripListContainer;
    private View emptyView;
    public List<Trip> tripList = new ArrayList<>();

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tripListContainer = view.findViewById(R.id.tripListContainer);
        emptyView = view.findViewById(R.id.emptyView);

        tripList = loadTrips();

        getParentFragmentManager().setFragmentResultListener("newTrip", this, (requestKey, bundle) -> {
            String name = bundle.getString("tripName");
            String desc = bundle.getString("tripDescription");
            int people = bundle.getInt("tripPeopleCount");
            String dest = bundle.getString("tripDestination");

            tripList.add(new Trip(name, desc, people, dest));
            saveTrips(); // ذخیره کل سفرها
            refreshTripList();
        });

        refreshTripList();
    }



    private void refreshTripList() {
        tripListContainer.removeAllViews();

        if (tripList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            tripListContainer.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            tripListContainer.setVisibility(View.VISIBLE);

            LayoutInflater inflater = LayoutInflater.from(getContext());

            for (int i = 0; i < tripList.size(); i++) {
                Trip trip = tripList.get(i);
                View tripView = inflater.inflate(R.layout.trip_item, tripListContainer, false);

                ImageView editIcon = tripView.findViewById(R.id.editIcon);
                ImageView deleteIcon = tripView.findViewById(R.id.deleteIcon);
                TextView tripName = tripView.findViewById(R.id.tripName);
                TextView tripDescription = tripView.findViewById(R.id.tripDescription);
                TextView peopleCount = tripView.findViewById(R.id.peopleCount);

                tripName.setText(trip.getName());
                tripDescription.setText(trip.getDescription());
                peopleCount.setText(trip.getPeopleCount() + " نفر");

                int index = i;
                editIcon.setOnClickListener(v -> {
                    openEditDialog(trip, index);
                });

                deleteIcon.setOnClickListener(v -> {
                    LayoutInflater deleteInflater = getLayoutInflater(); // اسم جدید
                    View dialogView = deleteInflater.inflate(R.layout.dialog_confirm_delete, null);

                    AlertDialog dialog = new AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setPositiveButton("بله", (d, which) -> {
                                tripList.remove(index);
                                saveTrips();
                                refreshTripList();
                            })
                            .setNegativeButton("خیر", (d, which) -> d.dismiss())
                            .create();

                    dialog.show();
                });


                tripListContainer.addView(tripView);

            }
        }
    }

    public static class Trip {
        private String name;
        private String description;
        private String destination;
        private int peopleCount;

        public Trip(String name, String description, int peopleCount, String destination) {
            this.name = name;
            this.description = description;
            this.peopleCount = peopleCount;
            this.destination = destination;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getPeopleCount() { return peopleCount; }
        public String getDestination() { return destination; }
    }

    private void openEditDialog(Trip trip, int index) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_trip, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editPeopleCount = dialogView.findViewById(R.id.editPeopleCount);
        EditText editDestination = dialogView.findViewById(R.id.editDestination);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        editName.setText(trip.getName());
        editDescription.setText(trip.getDescription());
        editPeopleCount.setText(String.valueOf(trip.getPeopleCount()));
        editDestination.setText(trip.getDestination());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newDescription = editDescription.getText().toString().trim();
            int newPeopleCount = Integer.parseInt(editPeopleCount.getText().toString().trim());
            String newDestination = editDestination.getText().toString().trim();

            tripList.set(index, new Trip(newName, newDescription, newPeopleCount, newDestination));
            refreshTripList();
            dialog.dismiss();
            saveTrips();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void saveTrips() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyTrips", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(tripList); // لیست کامل به JSON

        editor.putString("trip_list", json);
        editor.apply();
    }

    private List<Trip> loadTrips() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyTrips", Context.MODE_PRIVATE);
        String json = prefs.getString("trip_list", null);

        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Trip>>() {}.getType();
        return gson.fromJson(json, type);
    }



}