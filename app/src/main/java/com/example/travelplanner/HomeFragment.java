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
import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout tripListContainer;
    private View emptyView;
    private List<Trip> tripList = new ArrayList<>();

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

            for (Trip trip : tripList) {
                View tripView = inflater.inflate(R.layout.trip_item, tripListContainer, false);

                TextView tripName = tripView.findViewById(R.id.tripName);
                TextView tripDescription = tripView.findViewById(R.id.tripDescription);
                TextView peopleCount = tripView.findViewById(R.id.peopleCount);

                tripName.setText(trip.getName());
                tripDescription.setText(trip.getDescription());
                peopleCount.setText("نفر: " + trip.getPeopleCount());

                tripListContainer.addView(tripView);
            }
        }
    }

    private static class Trip {
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
}
