package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

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

        getParentFragmentManager().setFragmentResultListener("newTrip", this, (requestKey, bundle) -> {
            String name = bundle.getString("tripName");
            String desc = bundle.getString("tripDescription");
            int people = bundle.getInt("tripPeopleCount");
            String dest = bundle.getString("tripDestination");
            tripList.add(new Trip(name, desc, people, dest));
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

                tripListContainer.addView(tripView);
            }
        }
    }

    public static class Trip {
        private final String name;
        private final String description;
        private final String destination;
        private final int peopleCount;

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
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}