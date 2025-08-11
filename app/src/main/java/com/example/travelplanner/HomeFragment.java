package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private LinearLayout tripListContainer;
    private ArrayList<Trip> trips = new ArrayList<>();

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

        // دریافت سفر جدید از StartJourneyFragment
        getParentFragmentManager().setFragmentResultListener("newTrip", this, (requestKey, bundle) -> {
            String name = bundle.getString("tripName");
            String description = bundle.getString("tripDescription");
            int peopleCount = bundle.getInt("tripPeopleCount");
            String destination = bundle.getString("tripDestination");

            addTrip(new Trip(name, description, peopleCount, destination));
        });

        refreshTripList();
    }

    private void refreshTripList() {
        tripListContainer.removeAllViews();

        if (trips.isEmpty()) {
            // وقتی لیست خالیه، یک TextView نشون بده
            TextView emptyText = new TextView(getContext());
            emptyText.setText("هیچ سفری ثبت نشده است");
            emptyText.setTextSize(18);
            emptyText.setPadding(20, 20, 20, 20);
            tripListContainer.addView(emptyText);
        } else {
            // نمایش هر سفر
            for (Trip trip : trips) {
                View tripView = LayoutInflater.from(getContext()).inflate(R.layout.trip_item, tripListContainer, false);

                TextView tripName = tripView.findViewById(R.id.tripName);
                TextView tripDescription = tripView.findViewById(R.id.tripDescription);
                TextView peopleCount = tripView.findViewById(R.id.peopleCount);

                tripName.setText(trip.name);
                tripDescription.setText(trip.description);
                peopleCount.setText("تعداد نفرات: " + trip.peopleCount);

                tripListContainer.addView(tripView);
            }
        }
    }

    private void addTrip(Trip trip) {
        trips.add(trip);
        refreshTripList();
    }

    // مدل سفر
    public static class Trip {
        String name;
        String description;
        int peopleCount;
        String destination;

        public Trip(String name, String description, int peopleCount, String destination) {
            this.name = name;
            this.description = description;
            this.peopleCount = peopleCount;
            this.destination = destination;
        }
    }
}
